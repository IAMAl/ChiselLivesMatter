// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package csr

import chisel3._
import chisel3.util._

import params._

class CSU extends Module {

	val FC3_CSRRW	 = params.Parameters.FC3_CSRRW.U
	val FC3_CSRRS	 = params.Parameters.FC3_CSRRS.U
	val FC3_CSRRC	 = params.Parameters.FC3_CSRRW.U
	val FC3_CSRRWI	= params.Parameters.FC3_CSRRWI.U
	val FC3_CSRRSI	= params.Parameters.FC3_CSRRSI.U
	val FC3_CSRRCI	= params.Parameters.FC3_CSRRWI.U
	val NumReg			= params.Parameters.NumReg
	val LogNumReg	 = params.Parameters.LogNumReg
	val DataWidth	 = params.Parameters.DataWidth


	/* I/O										 		*/
	val io	= IO(new CSR_IO)

	/* Register											*/
	//Register File
	//4x32x32 Bank Configuration
	val CSR		 = RegInit(0.U.asTypeOf(Vec(2, Vec(LogNumReg, Vec(LogNumReg, UInt(DataWidth.W))))))


	/* Wire												*/
	val csr		 = Wire(UInt(DataWidth.W))	//CSR-Output
	val imm_z	 = Wire(UInt(5.W))			//Immediate
	val idx		 = Wire(UInt(12.W))			//Index


	/* Assign											*/
	imm_z	 := io.i_rn1
	idx		 := io.i_imm(11, 0)

	csr		 := CSR(idx(11, 10))(idx(9, 5))(idx(4, 0))

	when (io.i_vld) {
		when(io.i_fc3 === FC3_CSRRW) {
			CSR(idx(11, 10))(idx(9, 5))(idx(4, 0))	:= io.i_rs1
		}
		.elsewhen (io.i_fc3 === FC3_CSRRWI) {
			CSR(idx(11, 10))(idx(9, 5))(idx(4, 0))	:= imm_z
		}
		.elsewhen (io.i_fc3 === FC3_CSRRS) {
			CSR(idx(11, 10))(idx(9, 5))(idx(4, 0))	:= csr | io.i_rs1
		}
		.elsewhen (io.i_fc3 === FC3_CSRRSI) {
			CSR(idx(11, 10))(idx(9, 5))(idx(4, 0))	:= csr | imm_z
		}
		.elsewhen (io.i_fc3 === FC3_CSRRC) {
			CSR(idx(11, 10))(idx(9, 5))(idx(4, 0))	:= csr & ~io.i_rs1
		}
		.elsewhen (io.i_fc3 === FC3_CSRRCI) {
			CSR(idx(11, 10))(idx(9, 5))(idx(4, 0))	:= csr & ~imm_z
		}
	}

	io.o_dst		:= csr

	io.o_wrb		:= io.i_vld

	io.o_wrn		:= io.i_wrn
}
