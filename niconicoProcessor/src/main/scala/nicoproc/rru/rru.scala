// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package rru

import chisel3._
import chisel3.util._

import params._
import isa._
import route._


class Encoder_ (
        NumInputs:  Int
    ) extends Module {


    /* I/O                                                  */
    val io = IO(new Bundle {
        val i_data  = Input( Vec(NumInputs, Bool()))        // Set of Signal
        val o_enc   = Output(UInt((log2Ceil(NumInputs)).W)) // Encoded No.
    })


    /* Wire                                                 */
    val GrantNo = Wire(Vec(NumInputs, UInt(log2Ceil(NumInputs).W)))


    /* Assign                                               */
    // i_data is exclusive single-bit signal
    for (index<-0 until NumInputs) {
        when (io.i_data(index)) {
            GrantNo(index)  := index.U
        }
        .otherwise {
            GrantNo(index)  := 0.U
        }
    }

    io.o_enc    := GrantNo.reduce(_ | _).asUInt
}


class Decoder_ (
        NumInputs:  Int
    ) extends Module {


    /* I/O                                                  */
    val io = IO(new Bundle {
        val i_req   = Input( Bool())
        val i_wrn   = Input( UInt(log2Ceil(NumInputs).W))   // Set of Signal
        val o_grt   = Output(Vec(NumInputs, Bool()))        // Decoded Flag
    })

    for (idx<-0 until NumInputs) {
        when (idx.U === io.i_wrn) {
            io.o_grt    := true.B
        }
        .otherwise {
            io.o_grt    := false.B
        }
    }
}


class Arbiter_ (
        NumInputs:  Int
    ) extends Module {


    /* I/O                                                  */
    val io = IO(new Bundle {
        // Number of Inputs (Req/Rls):   NumInputs
        // Number of Validation:         Numinputs
        // Grant-Number:                 ceil(log(NumInputs))-bit
        val i_req   = Input( Vec(NumInputs, Bool()))        // Requests
        val I_Rls   = Input( Vec(NumInputs, Bool()))        // Valid Bar
        val o_grt   = Output(UInt(log2Ceil(NumInputs).W))   // Grant-No
        val o_vld   = Output(Vec(NumInputs, Bool()))        // Validation
        val o_full  = Output(Bool())                        // Full
        val o_empty = Output(Bool())                        // Empty
    })


    /* Module                                               */
    // Encoding Single-Bits to Number
    val Encoder = Module(new Encoder_(NumInputs))


    /* Register                                             */
    // Grant Flag, Exclusive Assertion
    val valid   = RegInit(false.B.asTypeOf(Vec(NumInputs, Bool())))


    /* Wire                                                 */
    //   False-Active (so this has name of "in"-valid)
    val invalid = Wire(Vec(NumInputs, Vec(NumInputs, Bool())))

    // Validation Detection
    //   Checking that Grant does Already exist
    val v_exist = Wire(Bool())


    /* Assign                                               */
    //// Tournament Assignment                              ////
    //   Priority: Youngest Number
    //   Example (NumInputs = 4)
    // in_no    cn_no
    //    -     0   1   2
    //    0     f   f   f
    //    1     f   f   r0
    //    2     f   r1  r0
    //    3     r2  r1  r0
    for (in_no<-0 until NumInputs by 1) {
        if ((NumInputs-1) != 0) {
            for (cn_no<-0 until NumInputs by 1) {
                if (cn_no == (NumInputs-1)) {
                    invalid(in_no)(cn_no)   := false.B
                }
                else if (in_no == 0) {
                    invalid(0)(cn_no)       := false.B
                }
                else {
                    if (in_no < (NumInputs-cn_no-1)) {
                        invalid(in_no)(cn_no)   := false.B
                    }
                    else {
                        invalid(in_no)(cn_no)   := io.i_req(NumInputs-cn_no-2)
                    }
                }
            }
        }
        else {
            invalid(in_no)(0)   := false.B
        }
    }


    //// Validation                                         ////
    v_exist     := valid.reduce(_ | _).asBool
    io.o_vld    := valid
    for (in_no<-0 until NumInputs) {
        when (io.I_Rls(in_no)) {
            // Release is Priority
            valid(in_no)    := false.B
        }
        when (io.i_req(in_no) && !invalid(in_no).reduce(_ | _).asBool && !v_exist) {
            valid(in_no)    := true.B
        }
        Encoder.io.i_data   := io.i_req(in_no) && !invalid(in_no).reduce(_ | _).asBool && !v_exist
    }


    //// Output Grant                                       ////
    io.o_grt            := Encoder.io.o_enc


    //// Ourput Flag
    io.o_full           := valid.asUInt.andR
    io.o_empty          := !(valid.asUInt() =/= 0.U)
}


class RenameEntry (
        LogNumReg:  Int
    ) extends Bundle {
    val RFN         = Reg(UInt(LogNumReg.W))
}


class RRU extends Module {

    val LSB_Opc     = params.Parameters.LSB_Opc
    val MSB_Opc     = params.Parameters.MSB_Opc
    val LSB_Fc3     = params.Parameters.LSB_Fc3
    val MSB_Fc3     = params.Parameters.MSB_Fc3
    val OpcWidth    = params.Parameters.OpcWidth
    val Fc3Width    = params.Parameters.Fc3Width
    val Fc7Width    = params.Parameters.Fc7Width
    val DataWidth   = params.Parameters.DataWidth
    val LogNumReg   = params.Parameters.LogNumReg
    val PNumReg     = params.Parameters.PNumReg
    val PLogNumReg  = params.Parameters.PLogNumReg


    /* I/O                              */
    val io = IO(new RRU_IO)


    /* Module                           */
    val ISplit      = Module(new ISplit)

    //Opcode Bit-Field Extraction
    val ISA_Opcode  = Module(new ISA_Opcode)

    //Rename Handler
    val Arbiter     = Module(new Arbiter_(PNumReg))
    val Encoder     = Module(new Encoder_(PLogNumReg))
    val Decoder     = Module(new Decoder_(PLogNumReg))


    /* Register                         */
    val Vld         = RegInit(Bool(), false.B)  //Validation for Next Stage
    val Opc         = Reg(UInt(OpcWidth.W))     //Opcode
    val Wno         = Reg(UInt(PLogNumReg.W))   //Write-Back Register No.
    val Rn1         = Reg(UInt(PLogNumReg.W))   //Source Register No.
    val Rn2         = Reg(UInt(PLogNumReg.W))   //Source REgister No.
    val Fc3         = Reg(UInt(Fc3Width.W))     //Func3
    val Fc7         = Reg(UInt(Fc7Width.W))     //Func7
    val Re1         = RegInit(Bool(), false.B)  //Read-Enable
    val Re2         = RegInit(Bool(), false.B)  //Read-Enable
    val EnWB        = RegInit(Bool(), false.B)  //Write-Back Enable
    val Hzd         = RegInit(Bool(), false.B)  //Hazard for Cond-Branch

    //Rename Table
    val RRFN        = Reg(Vec(PNumReg, new RenameEntry(PLogNumReg)))


    /* Wire                             */
    //Value Holder
    val opc         = Wire(UInt(OpcWidth.W))
    val wno         = Wire(UInt(LogNumReg.W))
    val rn1         = Wire(UInt(LogNumReg.W))
    val rn2         = Wire(UInt(LogNumReg.W))
    val fc3         = Wire(UInt(Fc3Width.W))
    val fc7         = Wire(UInt(Fc7Width.W))
    val empty       = Wire(Bool())
    val full        = Wire(Bool())
    val cond        = Wire(Bool())
    val hzrd        = Wire(Vec(PLogNumReg, Bool()))

    //Immediate Value
    val imm_dst     = Wire(Bool())
    val imm_src     = Wire(Bool())

    //Use of Register File
    val reg_req     = Wire(Bool())

    //Back-End Unit ID
    val UnitID      = Wire(UInt(3.W))


    /* Assign                           */
    //Instruction Split
    ISplit.io.i_ins := io.i_ins
    opc     := ISplit.io.o_opc
    fc3     := ISplit.io.o_fc3
    fc7     := ISplit.io.o_fc7
    wno     := ISplit.io.o_wno
    rn1     := ISplit.io.o_rn1
    rn2     := ISplit.io.o_rn2

    ISA_Opcode.io.i_opc := opc
    UnitID      := ISA_Opcode.io.o_OpcodeType

    //Flag: Instruction is Conditional Branch
    cond        := (opc(MSB_Fc3, LSB_Fc3) === (params.Parameters.OP_BRJMP).U)

    //Flag: Reading Immediate (WN) at Store or Branch
    imm_dst     := ((opc(MSB_Fc3, LSB_Fc3) === (params.Parameters.OP_STORE).U) ||
                    (opc(MSB_Fc3, LSB_Fc3) === (params.Parameters.OP_BRJMP).U))

    //Flag: Reading Immediate (RN1/RN2) at Link, Branch or ALU
    imm_src     := ((opc(MSB_Fc3, LSB_Fc3) === (params.Parameters.OP_JAL).U)   ||
                    (opc(MSB_Fc3, LSB_Fc3) === (params.Parameters.OP_BRJMP).U) ||
                    (opc(MSB_Fc3, LSB_Fc3) === (params.Parameters.OP_RandI).U))


    //// Register Renaming
    //Flag: Write-Back to Register File
    reg_req             := !imm_dst

    //Register to Rename Table
    Decoder.io.i_req    := io.i_wrb
    Decoder.io.i_wrn    := io.i_wbn
    Arbiter.io.i_req    := io.i_vld && (reg_req || cond)
    Arbiter.io.I_Rls    := Decoder.io.o_grt
    full                := Arbiter.io.o_full
    empty               := Arbiter.io.o_empty
    for (idx<-0 until PNumReg) {
        when (io.i_vld && reg_req && !full) {
            when (cond) {
                //Conditional Branch then use this entry as a Flag
                RRFN(Arbiter.io.o_grt).RFN  := (PNumReg-1).U
            }
            .otherwise {
                RRFN(Arbiter.io.o_grt).RFN  := wno
            }
        }
    }
    Wno         := Decoder.io.o_grt

    //Write-Back Enable Assertion
    when (  (UnitID === (params.Parameters.OP_RandI).U) ||
            (UnitID === (params.Parameters.OP_RandR).U) ||
            (UnitID === (params.Parameters.OP_LOAD).U)  ||
            (UnitID === (params.Parameters.OP_JAL).U)   ||
            (UnitID === (params.Parameters.OP_CSR).U)
            ) {
        EnWB    := io.i_vld
    }
    .otherwise {
        EnWB    := false.B
    }


    //// Output
    //Write-Back Request
    io.o_wrb    := EnWB

    //Register File Read-Enable
    Re1         := io.i_vld
    Re2         := io.i_vld && !imm_src
    io.o_re1    := Re1
    io.o_re2    := Re2

    //Output Renamed Register No.
    for (idx<-0 until PLogNumReg) {
        when (rn1 === RRFN(idx).RFN) {
            Rn1 := RRFN(idx).RFN
        }
        when (rn2 === RRFN(idx).RFN) {
            Rn2 := RRFN(idx).RFN
        }
        when ((PNumReg-1).U === RRFN(idx).RFN) {
            hzrd(idx)   := Arbiter.io.o_vld(idx)
        }
        .otherwise {
            hzrd(idx)   := false.B
        }
    }
    Hzd         := (hzrd.asUInt() =/= 0.U)

    //Function Command
    when (io.i_vld) {
        Opc     := opc
        Fc3     := fc3
        Fc7     := fc7
    }
    io.o_opc    := Opc
    io.o_wno    := Wno
    io.o_rn1    := Rn1
    io.o_rn2    := Rn2
    io.o_fc3    := Fc3
    io.o_fc7    := Fc7

    //Flag: Conditional Branch is in Pipeline
    io.o_hzd    := Hzd

    //Execution Enable on Back-End Pipeline
    Vld         := io.i_vld && !full
    io.o_exe    := Vld
}