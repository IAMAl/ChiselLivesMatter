// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package csr

import chisel3._
import chisel3.util._

import params._

class CSR_IO extends Bundle {

    val Fc3Width    = params.Parameters.Fc3Width
    val LogNumReg   = params.Parameters.LogNumReg
    val PLogNumReg  = params.Parameters.PLogNumReg
    val DataWidth   = params.Parameters.DataWidth


    val i_vld = Input( Bool())              //ALU Operation Validation
    val i_rn1 = Input( UInt(LogNumReg.W))   //Read Index
    val i_rs1 = Input( UInt(DataWidth.W))   //Source Operand-1 Port
    val i_fc3 = Input( UInt(Fc3Width.W))    //Func-3 Port
    val i_imm = Input( UInt(DataWidth.W))   //Source Operand-2 Port
    val i_wrn = Input( UInt(PLogNumReg.W))  //Write-Back Index
    val o_wrn = Output(UInt(PLogNumReg.W))  //Write-Back Index
    val o_dst = Output(UInt(DataWidth.W))   //Destination Operand Port
    val o_wrb = Output(Bool())              \//Writeback Request
}
