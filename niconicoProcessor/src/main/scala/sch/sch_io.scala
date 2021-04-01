// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package sch

import chisel3._
import chisel3.util._

import params.Parameters._

class SCH_IO extends Bundle {   

    val vld = Input( Bool())                                //Input Validation
    val ins = Input( UInt((params.Parameters.ISAWidth).W))  //Fetched Instruction Port

    val opc = Output(UInt((params.Parameters.OpcWidth).W))  //Opcode
    val rn1 = Output(UInt((params.Parameters.LogNumReg).W)) //Read Register No-1
    val rn2 = Output(UInt((params.Parameters.LogNumReg).W)) //Read Register No-2
    val wno = Output(UInt((params.Parameters.LogNumReg).W)) //Write Register No
    val fc3 = Output(UInt((params.Parameters.Fc3Width).W))  //Function-3 Port
    val fc7 = Output(UInt((params.Parameters.Fc7Width).W))  //Function-7 Port

    val by1 = Output(Bool())                                //Bypass Control-1
    val by2 = Output(Bool())                                //Bypass Control-2

    val re1 = Output(Bool())                                //Register Read Enable-1
    val re2 = Output(Bool())                                //Register Read Enable-2
    val wed = Output(Bool())                                //Register Write Enable
    val cnd = Output(Bool())                                //Branch Unit is Active

    val exe = Output(Bool())                                //Execution Timing Flag
}
