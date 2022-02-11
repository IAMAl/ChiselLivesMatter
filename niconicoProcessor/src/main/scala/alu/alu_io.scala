// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package alu

import chisel3._
import chisel3.util._

import params._

class ALU_IO extends Bundle {
    val i_vld = Input( Bool())                                  //ALU Operation Validation
    val i_rs1 = Input( UInt((params.Parameters.DatWidth).W))    //Source Operand-1 Port
    val i_rs2 = Input( UInt((params.Parameters.DatWidth).W))    //Source Operand-2 Port
    val i_fc3 = Input( UInt((params.Parameters.Fc3Width).W))    //Function-3 Port
    val i_fc7 = Input( UInt((params.Parameters.Fc7Width).W))    //Function-7 Port
    val i_imm = Input( UInt((params.Parameters.DatWidth).W))    //Immediate Port
    val o_dst = Output(UInt((params.Parameters.DatWidth).W))    //Destination Operand Port
}
