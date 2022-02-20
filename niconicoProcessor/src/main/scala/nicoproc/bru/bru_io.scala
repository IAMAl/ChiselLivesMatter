// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package bru

import chisel3._
import chisel3.util._

import params._

class BRU_IO extends Bundle {

    val OpcWidth    = params.Parameters.OpcWidth
    val Fc3Width    = params.Parameters.Fc3Width
    val DataWidth   = params.Parameters.DataWidth
    val PLogNumReg  = params.Parameters.PLogNumReg
    val AddrWidth   = params.Parameters.AddrWidth

    val i_vld = Input( Bool())              //BRJ Operation Validation
    val i_rs1 = Input( UInt(DataWidth.W))   //Source Operand-1 Port
    val i_rs2 = Input( UInt(DataWidth.W))   //Source Operand-2 Port

    val i_jal = Input( UInt(OpcWidth.W))    //Jump and Link Flag
    val i_imm = Input( UInt(DataWidth.W))   //Immediate Port
    val i_fc3 = Input( UInt(Fc3Width.W))    //Immediate (Func3)
    val i_wrn = Input( UInt(PLogNumReg.W))  //Write-Back Index

    val o_wrn = Output(UInt(PLogNumReg.W))  //Write-Back Index
    val o_brc = Output(Bool())              //Branch Taken
    val o_wrb = Output(Bool())              //Write-back
    val o_dst = Output(UInt(AddrWidth.W))   //Link Address
    val o_pc  = Output(UInt(AddrWidth.W))   //Program Counter (PC)
}
