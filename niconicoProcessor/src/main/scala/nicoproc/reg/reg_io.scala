// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package reg

import chisel3._
import chisel3.util._

import params._

class REG_IO extends Bundle {
    val OpcWidth    = params.Parameters.OpcWidth
    val Fc3Width    = params.Parameters.Fc3Width
    val Fc7Width    = params.Parameters.Fc7Width
    val PLogNumReg  = params.Parameters.PLogNumReg
    val DataWidth   = params.Parameters.DataWidth
    val AddrWidth   = params.Parameters.AddrWidth

    val i_vld   = Input( Bool())                //Input Validation
    val i_opc   = Input( UInt(OpcWidth.W))      //Opcode

    val i_wed   = Input( Bool())                //Register Write Enable
    val i_re1   = Input( Bool())                //Register Read Enable-1
    val i_re2   = Input( Bool())                //Register Read Enable-2

    val i_wno   = Input( UInt(PLogNumReg.W))    //Register Write No
    val i_rn1   = Input( UInt(PLogNumReg.W))    //Register Read No-1
    val i_rn2   = Input( UInt(PLogNumReg.W))    //Register Read No-2
    val i_fc3   = Input( UInt(Fc3Width.W))      //Function-3 Port
    val i_fc7   = Input( UInt(Fc7Width.W))      //Function-7 Port

    val o_wrn   = Output(UInt(PLogNumReg.W))    //Write-back Reg No.
    val o_rn1   = Output(UInt(PLogNumReg.W))    //Register Read No-1
    val o_rn2   = Output(UInt(PLogNumReg.W))    //Register Read No-2
    val o_fc3   = Output(UInt(Fc3Width.W))      //Function-3 Port
    val o_fc7   = Output(UInt(Fc7Width.W))      //Function-7 Port
    val o_imm   = Output(UInt(DataWidth.W))     //Immediate

    val i_wrb_r = Input( Bool())                //Write-back Req.
    val i_wrn   = Input( UInt(PLogNumReg.W))    //Register Write No
    val i_wrb_d = Input( UInt(DataWidth.W))     //Write-back Data

    val i_pc    = Input( UInt(AddrWidth.W))     //Program Counter (PC) Port

    val o_as1   = Output(UInt(DataWidth.W))     //ALU Operand-1 Port
    val o_as2   = Output(UInt(DataWidth.W))     //ALU Operand-2 Port
    val o_ls1   = Output(UInt(DataWidth.W))     //LSU Operand-1 Port
    val o_ls2   = Output(UInt(DataWidth.W))     //LSU Operand-2 Port
    val o_bs1   = Output(UInt(DataWidth.W))     //BRJ Operand-1 Port
    val o_bs2   = Output(UInt(DataWidth.W))     //BRJ Operand-2 Port
    val o_cs1   = Output(UInt(DataWidth.W))     //CSR Operand-1 Port
    val o_cs2   = Output(UInt(DataWidth.W))     //CSR Operand-2 Port

    val o_opcode= Output(UInt(OpcWidth.W))      //Opcode
    val o_exe   = Output(Bool())                //Exe En
}
