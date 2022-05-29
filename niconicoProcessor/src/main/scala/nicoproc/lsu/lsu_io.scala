// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package lsu

import chisel3._
import chisel3.util._

import params._

class Load_IO extends Bundle {
	val LdReq   = Input( Bool())
	val LdAck	= Input( Bool())
	val Stall	= Input( Bool())
	val Req		= Output(Bool())
	val LdValid = Output(Bool())
	val Busy	= Output(Bool())
}

class LSU_IO extends Bundle {
		
	val OpcWidth	= params.Parameters.OpcWidth
	val Fc3Width	= params.Parameters.Fc3Width
	val PLogNumReg	= params.Parameters.PLogNumReg
	val DataWidth	= params.Parameters.DataWidth
	val AddrWidth	= params.Parameters.AddrWidth

	val i_vld = Input( Bool())				//Activate Operation
	val i_opc = Input( UInt(OpcWidth.W))	//Opcode
	val i_fc3 = Input( UInt(Fc3Width.W))	//Function-3
	val i_rs1 = Input( UInt(DataWidth.W))	//Source Operand-1
	val i_rs2 = Input( UInt(DataWidth.W))	//Source Operand-2
	val i_imm = Input( UInt(DataWidth.W))	//Address Offset

	val i_wrn = Input( UInt(PLogNumReg.W))	//Write-Back Index
	val o_wrn = Output(UInt(PLogNumReg.W))	//Write-Back Index
	val o_wrb = Output(Bool())				//Write-Back Flag

	val o_dreq = Output(Bool())				//Memory Access Request
	val i_dack = Input( Bool())				//Memory Access Acknowledge
	val o_stor = Output(Bool())				//Memory Store(Write) Flag
	val o_dmar = Output(UInt(AddrWidth.W))	//Memory Address Register
	val i_idat = Input( UInt(DataWidth.W))	//From Memory Port
	val o_odat = Output(UInt(DataWidth.W))	//To Memory Port
	val o_dst	= Output(UInt(DataWidth.W))	//To Pipeline Port

	val o_csel = Output(Vec(4, UInt(1.W)))	//Chip Select (Byte-width)
}
