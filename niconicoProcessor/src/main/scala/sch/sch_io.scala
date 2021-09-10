// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package sch

import chisel3._
import chisel3.util._

import params.Parameters._

class SCH_IO extends Bundle {
    val i_vld = Input( Bool())                                  //Input Validation
    val i_ins = Input( UInt((params.Parameters.ISAWidth).W))    //Fetched Instruction Port

    val o_opc = Output(UInt((params.Parameters.OpcWidth).W))    //Opcode
    val o_rn1 = Output(UInt((params.Parameters.LogNumReg).W))   //Read Register No-1
    val o_rn2 = Output(UInt((params.Parameters.LogNumReg).W))   //Read Register No-2
    val o_wno = Output(UInt((params.Parameters.LogNumReg).W))   //Write Register No
    val o_fc3 = Output(UInt((params.Parameters.Fc3Width).W))    //Function-3 Port
    val o_fc7 = Output(UInt((params.Parameters.Fc7Width).W))    //Function-7 Port

    val o_by1 = Output(Bool())                                  //Bypass Control-1
    val o_by2 = Output(Bool())                                  //Bypass Control-2

    val o_re1 = Output(Bool())                                  //Register Read Enable-1
    val o_re2 = Output(Bool())                                  //Register Read Enable-2
    val o_wed = Output(Bool())                                  //Register Write Enable
    val o_cnd = Output(Bool())                                  //Branch Unit is Active

    val o_exe = Output(Bool())                                  //Execution Timing Flag
}
