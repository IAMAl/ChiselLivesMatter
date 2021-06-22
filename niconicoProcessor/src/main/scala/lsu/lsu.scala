// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
// Load/Store Unit
package lsu

import scala.math.BigInt

import chisel3._
import chisel3.util._

import params._
import isa._


class LdReq extends Module {

    val io extends Bundle {
        val LdReq   = Input( Bool())
        val LdAck   = Input( Bool())
        val Stall   = Input( Bool())
        val Req     = Output(Bool())
        val LdValid = Output(Bool())
        val Busy    = Output(Bool())
    })

    //Register
    val FSM     = RegInit(UInt(1.W), 0.U)

    io.Busy     := (FSM === 1.U)

    io.LdValid  := DontCare
    io.Req      := DontCare
    when (FSM) {
        is (0.U) {  //Init
            when (io.LdReq && !io.Stall) {
                io.Req      := true.B
                io.LdValid  := false.B
                FSM         := 1.U
            }
            .otherwise {
                io.Req      := false.B
                io.LdValid  := false.B
                FSM         := 0.U
            }
        }
        is (1.U) {  //In Loading
            when (io.LdReq && !io.Stall) {
                io.Req      := true.B
                io.LdValid  := io.LdAck
                FSM         := 1.U
            }
            .elsewhen(!io.LdReq) {
                io.Req      := false.B
                io.LdValid  := false.B
                FSM         := 0.U
            }
            .otherwise {
                io.Req      := true.B
                io.LdValid  := false.B
                FSM         := 1.U
            }
        }
    }
}

class LSU extends Module {

    /* I/O                          */
    val io = IO(new LSU_IO)

    /* Module                       */
    val ISA_fc3_lsu = Module(new ISA_fc3_lsu)               //Func3 Decoder
    val LdReq       = Module(new LdReq)                     //Load Request Controller

    /* Register                     */
    val mar     = Reg(UInt((params.Parameters.DatWidth).W)) //Memory Address Register (MAR)
    val mdr     = Reg(UInt((params.Parameters.DatWidth).W)) //Memory Data Register (MDR)
    val LdDone  = RegInit(Bool(), false.B)                  //

    /* Wire                         */
    val is_Ld   = Wire(Bool())                              //Load  Instruction Flag
    val is_St   = Wire(Bool())                              //Store Instruction Flag
    val dat     = Wire(UInt((params.Parameters.DatWidth).W))//Load Data
    val msk     = Wire(SInt((params.Parameters.DatWidth).W))//Access Mask

    /* Assign                       */
    //Func3 Decode
    ISA_fc3_lsu.io.fc3  := io.fc3

    //Load-Data Word Formatter
    when(io.fc3 === (params.Parameters.FC3_BYTEU).U) {
        //1-Byte Word with Sign-Extension
        dat := io.idat( 7, 0).asSInt.asUInt
    }
    .elsewhen(io.fc3 === (params.Parameters.FC3_HWORDU).U) {
        //2-Byte Word with Sign-Extension
        dat := io.idat(15, 0).asSInt.asUInt
    }
    .elsewhen(io.fc3 === (params.Parameters.FC3_BYTE).U) {
        //1-Byte Word
        dat := io.idat( 7, 0).asUInt
    }
    .elsewhen(io.fc3 === (params.Parameters.FC3_HWORD).U) {
        //2-Byte Word
        dat := io.idat(15, 0).asUInt
    }
    .elsewhen(io.fc3 === (params.Parameters.FC3_WORD).U) {
        //4-Byte Word
        dat := io.idat.asUInt
    }
    .otherwise {
        //NOP
        dat := 0.U
    }

    //MAR & MDR Porting
    //Check Instruction is Load or not
    is_Ld   := io.vld && (io.opc === (params.Parameters.OP_LOAD).U)

    //Check Instruction is Store or not
    is_St   := io.vld && (io.opc === (params.Parameters.OP_STORE).U)

    //Loading Path
    when (!io.vld && !LdReq.io.Busy) {
        LdDone  := false.B
    }
    .elsewhen (LdReq.io.io.LdValid) {
        LdDone  := true.B
    }
    LdReq.io.Stall  := false.B  //ToDo
    LdReq.io.LdReq  := is_Ld
    LdReq.io.LdAck  := io.dack

    //Set Memory Address Register
    when (io.vld) {
        mar := io.rs1 + io.imm
    }

    //Set Memory Data Register
    when (is_St) {
        //Store
        //Mask is used for Byte, and Half-Word Accesses
        mdr := io.rs2 & msk.asUInt
    }
    .elsewhen (LdReq.io.io.LdValid) {
            //Load
            mdr := dat
        }
    }

    //Output
    when (ISA_fc3_lsu.io.LSType ===  (params.Parameters.FC3_BYTE).U) {
        //1-Byte Access
        io.csel(0) := 1.U
        io.csel(1) := 0.U
        io.csel(2) := 0.U
        io.csel(3) := 0.U

        msk := 0x000000FF.S
    }
    .elsewhen (ISA_fc3_lsu.io.LSType ===  (params.Parameters.FC3_HWORD).U) {
        //2-Byte Access
        io.csel(0) := 1.U
        io.csel(1) := 1.U
        io.csel(2) := 0.U
        io.csel(3) := 0.U

        msk := 0x0000FFFF.S
    }
    .elsewhen (ISA_fc3_lsu.io.LSType ===  (params.Parameters.FC3_WORD).U) {
        //4-Byte Access
        io.csel(0) := 1.U
        io.csel(1) := 1.U
        io.csel(2) := 1.U
        io.csel(3) := 1.U

        msk := 0xFFFFFFFF.S
    }
    .otherwise {
        //Disable to Access
        io.csel(0) := 0.U
        io.csel(1) := 0.U
        io.csel(2) := 0.U
        io.csel(3) := 0.U

        msk := 0x00000000.S
    }
    io.dreq  := LdReq.io.Req
    io.stor  := is_St
    io.dmar  := mar
    io.dst   := mdr
    io.odat  := mdr
    io.wrb   := LdDone
}
