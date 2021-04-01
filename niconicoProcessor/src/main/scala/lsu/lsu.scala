// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
//Load/Store Unit
package lsu

import scala.math.BigInt

import chisel3._
import chisel3.util._

import params._
import isa._

class LSU extends Module {

    /* I/O                          */
    val io = IO(new LSU_IO)

    /* Module                       */
    val ISA_fc3_lsu = Module(new ISA_fc3_lsu)               //Func3 Decoder

    /* Register                     */
    val vld = RegInit(Bool(), false.B)                      //Validation
    val ack = RegInit(Bool(), false.B)                      //Nack
    val mar = Reg(UInt((params.Parameters.DatWidth).W))     //Memory Address Register (MAR)
    val mdr = Reg(UInt((params.Parameters.DatWidth).W))     //Memory Data Register (MDR)

    val ld  = RegInit(Bool(), false.B)                      //Load  Instruction Flag

    /* Wire                         */
    val st  = Wire(Bool())                                  //Store Instruction Flag
    val dat = Wire(UInt((params.Parameters.DatWidth).W))    //Load Data
    val msk = Wire(SInt((params.Parameters.DatWidth).W))    //Access Mask

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
    //Check Instruction is Store or not
    st  := io.vld && (io.opc === (params.Parameters.OP_STORE).U)

    //Check Instruction is Load or not
    when (io.vld && (io.opc === (params.Parameters.OP_LOAD).U)) {
        ld  := true.B
    }
    .elsewhen (!vld && io.dack) {
        ld  := false.B
    }

    when (io.vld || (vld && io.dack)) {
        //Memory Address Register
        when (io.vld) {
            mar := io.rs1 + io.imm
        }

        //Memory Data Register
        when (st) {
            //Store
            mdr := io.rs2 & msk.asUInt
        }
        .elsewhen (ld) {
            //Load
            mdr := dat
        }
    }

    //Access Validation
    when (io.vld ^ io.dack) {
        vld := true.B
    }
    .elsewhen (!ack) {
        vld := false.B
    }

    //Nack Generation
    when (vld) {
        ack := io.dack
    }

    //Output
    //Quarter-Word Access
    when (ISA_fc3_lsu.io.LSType ===  (params.Parameters.FC3_BYTE).U) {
        io.csel(0) := 1.U
        io.csel(1) := 0.U
        io.csel(2) := 0.U
        io.csel(3) := 0.U

        msk := 0x000000FF.S
    }   //Half-Word Access
    .elsewhen (ISA_fc3_lsu.io.LSType ===  (params.Parameters.FC3_HWORD).U) {
        io.csel(0) := 1.U
        io.csel(1) := 1.U
        io.csel(2) := 0.U
        io.csel(3) := 0.U

        msk := 0x0000FFFF.S
    }   //Single-Word Access
    .elsewhen (ISA_fc3_lsu.io.LSType ===  (params.Parameters.FC3_WORD).U) {
        io.csel(0) := 1.U
        io.csel(1) := 1.U
        io.csel(2) := 1.U
        io.csel(3) := 1.U

        msk := 0xFFFFFFFF.S
    }   //Disable to Access
    .otherwise {
        io.csel(0) := 0.U
        io.csel(1) := 0.U
        io.csel(2) := 0.U
        io.csel(3) := 0.U

        msk := 0x00000000.S
    }
    io.dreq  := vld && ack
    io.stor  := st
    io.dmar  := mar
    io.dst   := mdr
    io.odat  := mdr
    io.wrb   := ld && ack
}
