// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package reg

import chisel3._
import chisel3.util._

import params._

class REG_IO extends Bundle {
    val i_vld   = Input( Bool())                                    //Input Validation
    val i_opc   = Input( UInt((params.Parameters.OpcWidth).W))      //Opcode

    val i_by1   = Input( Bool())                                    //Bypassing Flag from EX-Stage-1
    val i_by2   = Input( Bool())                                    //Bypassing Flag from EX-Stage-2

    val i_wed   = Input( Bool())                                    //Register Write Enable
    val i_re1   = Input( Bool())                                    //Register Read Enable-1
    val i_re2   = Input( Bool())                                    //Register Read Enable-2

    val i_wno   = Input(UInt((params.Parameters.LogNumReg).W))      //Register Write No
    val i_rn1   = Input(UInt((params.Parameters.LogNumReg).W))      //Register Read No-1
    val i_rn2   = Input(UInt((params.Parameters.LogNumReg).W))      //Register Read No-2
    val i_fc3   = Input(UInt((params.Parameters.Fc3Width).W))       //Function-3 Port
    val i_fc7   = Input(UInt((params.Parameters.Fc7Width).W))       //Function-7 Port

    val o_rn1   = Output(UInt((params.Parameters.LogNumReg).W))     //Register Read No-1
    val o_rn2   = Output(UInt((params.Parameters.LogNumReg).W))     //Register Read No-2
    val o_fc3   = Output(UInt((params.Parameters.Fc3Width).W))      //Function-3 Port
    val o_fc7   = Output(UInt((params.Parameters.Fc7Width).W))      //Function-7 Port

    val i_wrb_r = Input(Bool())                                     //Write-back Req.
    val i_wrb_d = Input(UInt((params.Parameters.DatWidth).W))       //Write-back Data

    val i_pc    = Output(UInt((params.Parameters.AddrWidth).W))     //Program Counter (PC) Port

    val o_as1   = Output(UInt((params.Parameters.DatWidth).W))      //ALU Operand-1 Port
    val o_as2   = Output(UInt((params.Parameters.DatWidth).W))      //ALU Operand-2 Port
    val o_ls1   = Output(UInt((params.Parameters.DatWidth).W))      //LSU Operand-1 Port
    val o_ls2   = Output(UInt((params.Parameters.DatWidth).W))      //LSU Operand-2 Port
    val o_bs1   = Output(UInt((params.Parameters.DatWidth).W))      //BRJ Operand-1 Port
    val o_bs2   = Output(UInt((params.Parameters.DatWidth).W))      //BRJ Operand-2 Port

    val o_opcode= Output(UInt((params.Parameters.OpcWidth).W))      //Opcode
    val o_exe   = Output(Bool())                                    //Output Validation
}
