// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package sch

import chisel3._
import chisel3.util._

import params._
import route._

class SCH extends Module {

    /* I/O                          */
    val io = IO(new SCH_IO)


    /* Module                       */
    val ISplit  = Module(new ISplit)


    /* Register                     */
    //Value Holder
    val RegVld  = RegInit(false.B.asTypeOf(Vec(4, Bool())))
    val RegOpc  = RegInit(0.U.asTypeOf(UInt((params.Parameters.OpcWidth).W)))
    val RegFc3  = RegInit(0.U.asTypeOf(UInt((params.Parameters.Fc3Width).W)))
    val RegFc7  = RegInit(0.U.asTypeOf(UInt((params.Parameters.Fc7Width).W)))
    val RegWNo  = RegInit(0.U.asTypeOf(Vec(4, UInt((params.Parameters.LogNumReg).W))))
    val RegRN1  = RegInit(0.U.asTypeOf(Vec(3, UInt((params.Parameters.LogNumReg).W))))
    val RegRN2  = RegInit(0.U.asTypeOf(Vec(3, UInt((params.Parameters.LogNumReg).W))))

    //Hazard Checker
    val RegLd   = RegInit(false.B.asTypeOf(Vec(3, Bool())))
    val RegDst  = RegInit(false.B.asTypeOf(Vec(3, Bool())))
    val RegSrc  = RegInit(false.B.asTypeOf(Vec(3, Bool())))
    val RegCnd  = RegInit(false.B.asTypeOf(Vec(3, Bool())))
    val ImmDst  = RegInit(false.B.asTypeOf(Vec(3, Bool())))
    val ImmSrc  = RegInit(false.B.asTypeOf(Vec(3, Bool())))
    val CndDst  = RegInit(Bool(), false.B)


    /* Wire                         */
    //Value Holder
    val opc         = Wire(UInt((params.Parameters.OpcWidth).W))
    val wno         = Wire(UInt((params.Parameters.LogNumReg).W))
    val rn1         = Wire(UInt((params.Parameters.LogNumReg).W))
    val rn2         = Wire(UInt((params.Parameters.LogNumReg).W))
    val fc3         = Wire(UInt((params.Parameters.Fc3Width).W))
    val fc7         = Wire(UInt((params.Parameters.Fc7Width).W))

    //Hazard Checker
    //Write-After-Write (WAW) Hazard
    val WAWDst      = Wire(Vec(4, Bool()))

    //Write-After-Read (WAR) Hazard
    val WARSr1      = Wire(Vec(3, Bool()))
    val WARSr2      = Wire(Vec(3, Bool()))

    //Read-After-Write (RAW) Hazard
    val RAWSr1      = Wire(Vec(3, Bool()))
    val RAWSr2      = Wire(Vec(3, Bool()))
    val RAWMem      = Wire(Bool())

    //Hazard Detector
    val WAW_Hzd     = Wire(Bool())
    val WAR_RS1     = Wire(Bool())
    val WAR_RS2     = Wire(Bool())
    val RAW_Hzd     = Wire(Bool())

    //Stall Generation
    val Stall       = Wire(Bool())
    val StallWrite  = Wire(Bool())
    val StallRead   = Wire(Bool())
    val StallBranch = Wire(Bool())

    val imm_SB      = Wire(Bool())
    val imm_LBA     = Wire(Bool())


    /* Assign                       */
    //Instruction Split
    ISplit.io.i_ins := io.i_ins
    opc     := ISplit.io.o_opc
    fc3     := ISplit.io.o_fc3
    fc7     := ISplit.io.o_fc7
    wno     := ISplit.io.o_wno
    rn1     := ISplit.io.o_rn1
    rn2     := ISplit.io.o_rn2


    //Jump and Link (JAL) Handler
    when (RegCnd(2)) {
        //Write-back and Clear
        CndDst  := false.B
    }
    .elsewhen (opc === (params.Parameters.OP_JAL).U) {
        //Set flag when Opcode is JAL
        CndDst  := true.B
    }

    //Writing at Store and or Branch
    imm_SB      := ((opc(6, 4) === (params.Parameters.OP_STORE).U) ||
                    (opc(6, 4) === (params.Parameters.OP_BRJMP).U))

    //Reading at Link, Branch and or ALU
    imm_LBA     := ((opc(6, 4) === (params.Parameters.OP_JAL).U)   ||
                    (opc(6, 4) === (params.Parameters.OP_BRJMP).U) ||
                    (opc(6, 4) === (params.Parameters.OP_RandI).U))

    //Register Read Stage
    RegVld(0)   := io.i_vld && !Stall
    when (io.i_vld) {
        ImmDst(0)   := imm_SB
        ImmSrc(0)   := imm_LBA
        RegRN1(0)   := rn1
        RegRN2(0)   := rn2
        RegWNo(0)   := wno
        RegCnd(0)   := (opc(6, 4) === (params.Parameters.OP_JAL).U)  ||
                       (opc(6, 4) === (params.Parameters.OP_BRJMP).U)
        RegLd(0)    := (opc(6, 4) === (params.Parameters.OP_LOAD).U)
    }

    //Execution Stage
    RegVld(1)   := RegVld(0)
    when (RegVld(0)) {
        ImmDst(1)   := ImmDst(0)
        ImmSrc(1)   := ImmSrc(0)
        RegRN1(1)   := RegRN1(0)
        RegRN2(1)   := RegRN2(0)
        RegWNo(1)   := RegWNo(0)
        RegCnd(1)   := RegCnd(0)
        RegLd(1)    := RegLd(0)
    }

    //Write-Back Stage
    RegVld(2)   := RegVld(1)
    when (RegVld(1)) {
        ImmDst(2)   := ImmDst(1)
        ImmSrc(2)   := ImmSrc(1)
        RegRN1(2)   := RegRN1(1)
        RegRN2(2)   := RegRN2(1)
        RegWNo(2)   := RegWNo(1)
        RegCnd(2)   := RegCnd(1)
        RegLd(2)    := RegLd(1)
    }

    //Load Data (Spilling-out Loading)
    RegVld(3)   := RegVld(2)
    when (RegVld(2) && RegLd(2)) {
        RegWNo(3)   := RegWNo(2)
    }
    .otherwise {
        RegWNo(3)   := 0.U
    }


    /* Hazard Detection         */
    //Write-After-Write
    WAWDst(0)   := (RegWNo(0) === io.i_wno)  && RegVld(0) && !ImmDst(0)
    WAWDst(1)   := (RegWNo(1) === RegWNo(0)) && RegVld(1) && !ImmDst(1)
    WAWDst(2)   := (RegWNo(2) === RegWNo(0)) && RegVld(2) && !ImmDst(2)
    WAWDst(3)   := (RegWNo(3) === RegWNo(0)) && RegVld(3)

    //Write-After-Read
    WARSr1(0)   := (RegRN1(0) === io.i_wno)
    WARSr1(1)   := (RegRN1(1) === RegWNo(0))
    WARSr1(2)   := (RegRN1(2) === RegWNo(0))

    WARSr2(0)   := (RegRN2(0) === io.i_wno)
    WARSr2(1)   := (RegRN2(1) === RegWNo(0))
    WARSr2(2)   := (RegRN2(2) === RegWNo(0))

    //Structural Hazard Detection
    RAWSr1(0)   := (RegRN1(0) === RegWNo(2)) && RegVld(0)
    RAWSr1(1)   := (RegRN1(1) === RegWNo(2)) && RegVld(1)
    RAWSr1(2)   := (RegRN1(2) === RegWNo(2)) && RegVld(2)

    RAWSr2(0)   := (RegRN2(0) === RegWNo(2)) && RegVld(0) && !ImmSrc(0)
    RAWSr2(1)   := (RegRN2(1) === RegWNo(2)) && RegVld(1) && !ImmSrc(1)
    RAWSr2(2)   := (RegRN2(2) === RegWNo(2)) && RegVld(2) && !ImmSrc(2)

    //Load Hazard Detection
    RAWMem      := (RegRN1(0) === RegWNo(3))


    /* Stall Detection          */
    //Write-After-Write Hazard Detection
    //WAW between preceding load and follower destinations
    WAW_Hzd     := (WAWDst.asUInt =/= 0.U)

    //Write-After-Read Hazard Detection
    WAR_RS1     := (WARSr1.asUInt =/= 0.U)
    WAR_RS2     := (WARSr2.asUInt =/= 0.U)

    //Read-After-Write Hazard Detection
    RAW_Hzd     := (RAWSr1.asUInt =/= 0.U) || (RAWSr2.asUInt =/= 0.U) || RAWMem

    //Register-Write Stall
    StallWrite  := WAR_RS1 || WAR_RS2 || WAW_Hzd

    //Register-Read Stall
    StallRead   := RAW_Hzd

    //Stall by JAL Write-back
    StallBranch := CndDst

    //Stall
    Stall       := StallWrite || StallRead || StallBranch


    //Output
    when (io.i_vld && !Stall) {
        RegOpc  := opc
        RegFc3  := fc3
        RegFc7  := fc7
    }

    io.o_opc    := RegOpc
    io.o_rn1    := RegRN1(0)
    io.o_rn2    := RegRN2(0)
    io.o_wno    := RegWNo(0)
    io.o_fc3    := RegFc3
    io.o_fc7    := RegFc7

    io.o_wed    := !StallWrite
    io.o_re1    := !StallRead && !StallBranch && RegVld(0)
    io.o_re2    := !StallRead && !StallBranch && RegVld(0)

    //Bypassing from Write-Back Stage to Exe Stage
    io.o_by1    := RAWSr1(1)
    io.o_by2    := RAWSr2(1)

    //Validate Next Stage
    io.o_exe    := RegVld(0)

    //Active-flag for Branch Unit
    io.o_cnd    := (RegCnd.asUInt =/= 0.U)
}