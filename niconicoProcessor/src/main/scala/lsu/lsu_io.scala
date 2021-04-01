// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package lsu

import chisel3._
import chisel3.util._

import params._

class LSU_IO extends Bundle {   
    
    val vld = Input( Bool())                                    //Activate Operation
    val opc = Input( UInt((params.Parameters.OpcWidth).W))      //Opcode
    val fc3 = Input( UInt((params.Parameters.Fc3Width).W))      //Function-3
    val rs1 = Input( UInt((params.Parameters.DatWidth).W))      //Source Operand-1
    val rs2 = Input( UInt((params.Parameters.DatWidth).W))      //Source Operand-2
    val imm = Input( UInt((params.Parameters.ImmWidth).W))      //Address Offset
    val wrb = Output(Bool())                                    //Write-Back Flag

    val dreq = Output(Bool())                                   //Memory Access Request
    val dack = Input( Bool())                                   //Memory Access Acknowledge
    val stor = Output(Bool())                                   //Memory Store(Write) Flag
    val dmar = Output(UInt((params.Parameters.AddrWidth).W))    //Memory Address Register
    val idat = Input( UInt((params.Parameters.DatWidth).W))     //From Memory Port
    val odat = Output(UInt((params.Parameters.DatWidth).W))     //To Memory Port
    val dst  = Output(UInt((params.Parameters.DatWidth).W))     //To Pipeline Port

    val csel = Output(Vec(4, UInt(1.W)))                        //Chip Select (Byte-width)
}
