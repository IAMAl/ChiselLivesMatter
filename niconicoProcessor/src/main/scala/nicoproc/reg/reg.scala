// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
// Register File Unit
package reg

import chisel3._
import chisel3.util._

import params._

class REG extends Module {

	val DataWidth	= params.Parameters.DataWidth
	val NumReg		= params.Parameters.NumReg


	/* I/O													*/
	val io = IO(new REG_IO)


	/* Register												*/
	//Register File
	val RF			= RegInit(0.U.asTypeOf(Vec(NumReg, UInt((params.Parameters.DataWidth).W))))

	//Pipeline Registers
	val exe		 = RegInit(Bool(), false.B)											//Exec Validation
	val rs1		 = Reg(UInt((params.Parameters.DataWidth).W))		//RegisterFile Val. for Source-1
	val rs2		 = Reg(UInt((params.Parameters.DataWidth).W))		//RegisterFile Val. for Source-2
	val r_imm	 = Reg(UInt((params.Parameters.DataWidth).W))		//Immediate
	val opcode	= Reg(UInt((params.Parameters.OpcWidth).W))		 //Opcode

	val rn1		 = Reg(UInt((params.Parameters.LogNumReg).W))		//RegisterFile No. for Source-1
	val rn2		 = Reg(UInt((params.Parameters.LogNumReg).W))		//RegisterFile No. for Source-2
	val fc3		 = Reg(UInt((params.Parameters.Fc3Width).W))		 //Func3
	val fc7		 = Reg(UInt((params.Parameters.Fc7Width).W))		 //Func7

	val alu		 = Reg(Bool())																	 //Destination
	val lsu		 = Reg(Bool())																	 //Destination
	val bru		 = Reg(Bool())																	 //Destination
	val csu		 = Reg(Bool())																	 //Destination

	val wrn		 = Reg(UInt((params.Parameters.LogNumReg).W))


	/* Wire														*/
	val imm		 = Wire(UInt((params.Parameters.DataWidth).W))  //Immediate


	/* Assign													*/
	//Signed Immediate's Pre-Formatting
	when (io.i_opc === (params.Parameters.OP_STORE).U) {		//Store
		imm := Cat(Fill(20, io.i_fc7(6)), Cat(io.i_fc7, io.i_wno))
	}
	.elsewhen (io.i_opc === (params.Parameters.OP_LOAD).U) {	//Load
		imm := Cat(Fill(20, io.i_fc7(6)), Cat(io.i_fc7, io.i_rn2))
	}
	.elsewhen (io.i_opc === (params.Parameters.OP_BRJMP).U) {	 //Branch/Jump
		imm := Cat(Fill(19, io.i_fc7(6)), Cat(io.i_fc7(6), Cat(io.i_wno(0), Cat(io.i_fc7(5,0), Cat(io.i_wno(4, 1), 0.U.asTypeOf(UInt(1.W)))))))
	}
	.elsewhen ((io.i_opc === (params.Parameters.OP_RandI).U) && ((io.i_fc3 === (params.Parameters.FC3_AUIPC).U) || (io.i_fc3 === (params.Parameters.FC3_AUIPC).U))) {
		//LUI/AUIPC
		imm := Cat(Fill(11, io.i_fc7(6)), Cat(io.i_fc7, Cat(io.i_rn2, Cat(io.i_rn1, Cat(io.i_fc3, 0.U.asTypeOf(UInt(12.W)))))))
	}
	.otherwise {
		imm := 0.U
	}

	//Write Data
	when (io.i_wed && io.i_wrb_r) {
		RF(io.i_wno)		:= io.i_wrb_d
	}

	//Read Source-1
	when (io.i_vld) {
		when (io.i_re1) {
			//Read Register File
			rs1 := RF(io.i_rn1)
		}
		.elsewhen (io.i_fc3 === (params.Parameters.FC3_AUIPC).U) {
			//AUIPC
			rs1 := io.i_pc
		}
	}

	//Read Source-2
	when (io.i_vld) {
		when (io.i_re2) {
			//Read Register File
			rs2 := RF(io.i_rn2)
		}
		.otherwise {
			//Set Immediate
			rs2 := imm
		}
	}
	r_imm	:= imm

	//Output to Follower Pipeline Stage
	exe			:= io.i_vld //Exec Validation
	io.o_exe	:= exe

	opcode		:= io.i_opc //Send Opcode
	io.o_opcode := opcode

	rn1			:= io.i_rn1 //RegisterFile No for Source-1
	io.o_rn1	:= rn1

	rn2			:= io.i_rn2 //RegisterFile No for Source-2
	io.o_rn2	:= rn2

	fc3			:= io.i_fc3 //Func3
	io.o_fc3	:= fc3

	fc7			:= io.i_fc7 //Func7
	io.o_fc7	:= fc7

	//Arithmetic Source Operands
	alu			:= (io.i_opc === (params.Parameters.OP_RandI).U) || (io.i_opc === (params.Parameters.OP_RandR).U)
	when (alu) {
		//Set Source Operands for
		// Source-1&2 are from RegisterFile
		// Source-1 is from RegisterFile
		io.o_as1	:= rs1
		io.o_as2	:= rs2
	}
	.otherwise {
		//Otherwise NOT Send Value
		io.o_as1	:= 0.U
		io.o_as2	:= 0.U
	}

	//Load/Store Source Operands
	lsu			:= (io.i_opc === (params.Parameters.OP_STORE).U) || (io.i_opc === (params.Parameters.OP_LOAD).U)
	when (lsu) {
		//Set Load/Store Reference Data
		io.o_ls1	:= rs1
		io.o_ls2	:= rs2
	}
	.otherwise {
		//Otherwise NOT Send Value
		io.o_ls1	:= 0.U
		io.o_ls2	:= 0.U
	}

	//Branch Source Operands
	bru			:= (io.i_opc === (params.Parameters.OP_BRJMP).U)
	when (bru) {
		//Set Branch Reference Value
		io.o_bs1	:= rs1
		io.o_bs2	:= rs2
	}
	.otherwise {
		//Otherwise NOT Send Value
		io.o_bs1	:= 0.U
		io.o_bs2	:= 0.U
	}

	//Branch Source Operands
	csu			:= (io.i_opc === (params.Parameters.OP_CSR).U)
	when (csu) {
		//Set CSR Value
		io.o_cs1	:= rs1
		io.o_cs2	:= rs2
	}
	.otherwise {
		//Otherwise NOT Send Value
		io.o_cs1	:= 0.U
		io.o_cs2	:= 0.U
	}		

	//Immediate
	io.o_imm	:= r_imm

	//WB Reg No.
	wrn			:= io.i_wno
	io.o_wrn	:= wrn
}
