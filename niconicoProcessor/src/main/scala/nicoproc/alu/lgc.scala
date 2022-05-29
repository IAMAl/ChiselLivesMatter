// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package alu

import chisel3._
import chisel3.util._

import params._
import isa._

class Lgc extends Module {


	/* I/O													*/
	val io = IO(new ALU_IO)


	/* Assign												*/
	io.o_dst	:= DontCare
	when (io.i_vld) {
		switch (io.i_fc3) {
			is(params.Parameters.FC3_XOR.U) {
				//Bit-Wise XOR
				io.o_dst	 := io.i_rs1 ^ io.i_rs2
			}
			is(params.Parameters.FC3_OR.U) {
				//Bit-Wise OR
				io.o_dst	 := io.i_rs1 | io.i_rs2
			}
			is(params.Parameters.FC3_AND.U) {
				//Bit-Wise AND
				io.o_dst	 := io.i_rs1 & io.i_rs2
			}
		}
	}
	.otherwise {
		//NOP
		io.o_dst	 := 0.U
	}
}
