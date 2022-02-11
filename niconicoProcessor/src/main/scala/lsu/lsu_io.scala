// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package lsu

import chisel3._
import chisel3.util._

import params._

class Load_IO extends Bundle {
        val LdReq   = Input( Bool())
        val LdAck   = Input( Bool())
        val Stall   = Input( Bool())
        val Req     = Output(Bool())
        val LdValid = Output(Bool())
        val Busy    = Output(Bool())
}

class LSU_IO extends Bundle {
    val i_vld = Input( Bool())                                  //Activate Operation
    val i_opc = Input( UInt((params.Parameters.OpcWidth).W))    //Opcode
    val i_fc3 = Input( UInt((params.Parameters.Fc3Width).W))    //Function-3
    val i_rs1 = Input( UInt((params.Parameters.DatWidth).W))    //Source Operand-1
    val i_rs2 = Input( UInt((params.Parameters.DatWidth).W))    //Source Operand-2
    val i_imm = Input( UInt((params.Parameters.DatWidth).W))    //Address Offset

    val i_wrn = Input( UInt(params.Parameters.LogNumReg.W))     //Write-Back Index
    val o_wrn = Output(UInt(params.Parameters.LogNumReg.W))     //Write-Back Index
    val o_wrb = Output(Bool())                                  //Write-Back Flag

    val o_dreq = Output(Bool())                                 //Memory Access Request
    val i_dack = Input( Bool())                                 //Memory Access Acknowledge
    val o_stor = Output(Bool())                                 //Memory Store(Write) Flag
    val o_dmar = Output(UInt((params.Parameters.AddrWidth).W))  //Memory Address Register
    val i_idat = Input( UInt((params.Parameters.DatWidth).W))   //From Memory Port
    val o_odat = Output(UInt((params.Parameters.DatWidth).W))   //To Memory Port
    val o_dst  = Output(UInt((params.Parameters.DatWidth).W))   //To Pipeline Port

    val o_csel = Output(Vec(4, UInt(1.W)))                      //Chip Select (Byte-width)
}
