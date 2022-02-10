// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package csr

import chisel3._
import chisel3.util._

import params._

class CSR_IO extends Bundle {
    val i_vld = Input( Bool())                                  //ALU Operation Validation
    val i_rs1 = Input( UInt((params.Parameters.DatWidth).W))    //Source Operand-1 Port
    val i_imm = Input( UInt((params.Parameters.DatWidth).W))    //Source Operand-2 Port
    val i_wrn = Input( UInt((params.Parameters.LogNumReg).W))   //Write-Back Index
    val o_wrn = Output(UInt((params.Parameters.LogNumReg).W))   //Write-Back Index
    val o_dst = Output(UInt((params.Parameters.DatWidth).W))    //Destination Operand Port
    val o_wrb = Output(Bool())                                  //Writeback Request
}
