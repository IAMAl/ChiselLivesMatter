// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package route

import chisel3._
import chisel3.util._

import params._

class ISplit_IO extends Bundle {

	val ISAWidth	= params.Parameters.ISAWidth
	val OpcWidth	= params.Parameters.OpcWidth
	val Fc3Width	= params.Parameters.Fc3Width
	val Fc7Width	= params.Parameters.Fc7Width
	val LogNumReg	= params.Parameters.LogNumReg

	val i_ins		= Input( UInt(ISAWidth.W))	//Instruction
	val o_opc		= Output(UInt(OpcWidth.W))	//Opcode
	val o_wno		= Output(UInt(LogNumReg.W)) //Register Destination No
	val o_rn1		= Output(UInt(LogNumReg.W)) //Register Source No1
	val o_rn2		= Output(UInt(LogNumReg.W)) //Register Source No2
	val o_fc3		= Output(UInt(Fc3Width.W))	//Function-3 Value
	val o_fc7		= Output(UInt(Fc7Width.W))	//Function-7 Value
}
