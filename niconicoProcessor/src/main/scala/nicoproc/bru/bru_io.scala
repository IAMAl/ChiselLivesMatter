// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package bru

import chisel3._
import chisel3.util._

import params._

class BRU_IO extends Bundle {
    val i_vld = Input( Bool())                                  //BRJ Operation Validation
    val i_rs1 = Input( UInt((params.Parameters.DatWidth).W))    //Source Operand-1 Port
    val i_rs2 = Input( UInt((params.Parameters.DatWidth).W))    //Source Operand-2 Port

    val i_jal = Input( UInt((params.Parameters.OpcWidth).W))    //Jump and Link Flag
    val i_imm = Input( UInt((params.Parameters.DatWidth).W))    //Immediate Port
    val i_fc3 = Input( UInt((params.Parameters.Fc3Width).W))    //Immediate (Func3)
    val i_wrn = Input( UInt((params.Parameters.LogNumReg.W)))   //Write-Back Index
    
    val o_wrn = Output(UInt((params.Parameters.LogNumReg.W)))   //Write-Back Index
    val o_brc = Output(Bool())                                  //Branch Taken
    val o_wrb = Output(Bool())                                  //Write-back
    val o_dst = Output(UInt((params.Parameters.AddrWidth).W))   //Link Address
    val o_pc  = Output(UInt((params.Parameters.AddrWidth).W))   //Program Counter (PC)
}
