// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package alu

import chisel3._
import chisel3.util._

import params._

class ALU_IO extends Bundle {

	val Fc3Width	= params.Parameters.Fc3Width
	val Fc7Width	= params.Parameters.Fc7Width
	val DataWidth	= params.Parameters.DataWidth

	val i_vld = Input( Bool())				//ALU Operation Validation
	val i_rs1 = Input( UInt(DataWidth.W))	//Source Operand-1 Port
	val i_rs2 = Input( UInt(DataWidth.W))	//Source Operand-2 Port
	val i_fc3 = Input( UInt(Fc3Width.W))	//Function-3 Port
	val i_fc7 = Input( UInt(Fc7Width.W))	//Function-7 Port
	val i_imm = Input( UInt(DataWidth.W))	//Immediate Port
	val o_dst = Output(UInt(DataWidth.W))	//Destination Operand Port
}
