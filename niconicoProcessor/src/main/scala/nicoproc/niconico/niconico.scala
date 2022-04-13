// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
// RISC-V Processor Top Module
package niconico

import chisel3._
import chisel3.util._

import fch._
import route._
import rru._
import bru._
import reg._
import alu._
import lsu._
import csr._
import rob._

class NicoNico extends Module {


    /* I/O                    		  	*/
    val io = IO(new NICO2_IO)


    /* Module                       	*/
    //Front-End
    val FCH = Module(new FCH)     		//Instruction Fetch Unit
    val RRU = Module(new RRU)     		//Register Rename
    val REG = Module(new REG)     		//Register File

    //F/B Routing
    val URT = Module(new URT)     		//Router
    val ROB = Module(new ROB)   		//ROB

    //Back-End
    val ALU = Module(new ALU)     		//Arithmetic/Logic Unit
    val LSU = Module(new LSU)     		//Load/Store Unit
    val BRU = Module(new BRU)     		//Branch Unit
    val CSU = Module(new CSU)         	//Control Status Register


    /* Assign                       	*/
    //Stage-1
    //Instruction Fetch
    FCH.io.i_boot	:= io.boot			//Kick-Start (High-Active)
    FCH.io.i_ifch 	:= io.inst      	//Input 32b Word Instruction
    FCH.io.i_brc  	:= BRU.io.o_brc 	//Flush by Branch Taken
    FCH.io.i_stall	:= RRU.io.o_hzd ||  //Stall by Hazard
						ROB.io.o_full   //or by Full in ROB


    //Stage-2
    //Register Rename
    RRU.io.i_vld  	:= FCH.io.o_exe 	//Validate Scheduler
    RRU.io.i_ins  	:= FCH.io.o_ins 	//Feed Instruction Word
    RRU.io.i_wbn    := ROB.io.o_wrn     //WB Reg No.
    RRU.io.i_wrb    := ROB.io.o_wrb 	//Write-enable


    //Stage-3
    //Register-Read
    REG.io.i_vld  	:= RRU.io.o_exe 	//Validate Register File
    REG.io.i_opc  	:= RRU.io.o_opc 	//Opcode
    REG.io.i_re1  	:= RRU.io.o_re1 	//Read Enable-1
    REG.io.i_re2  	:= RRU.io.o_re2 	//Read Enable-2
    REG.io.i_rn1  	:= RRU.io.o_rn1 	//Read Register Number-1
    REG.io.i_rn2  	:= RRU.io.o_rn2 	//Read Register Number-2
    REG.io.i_fc3  	:= RRU.io.o_fc3 	//Func3 Value
    REG.io.i_fc7  	:= RRU.io.o_fc7 	//Func7 Value
    REG.io.i_wno  	:= RRU.io.o_wno 	//Write Register Number
    REG.io.i_wrb_r  := ROB.io.o_wrb     //WB Req

    //Route to Exec Units
    URT.io.i_opc  	:= RRU.io.o_opc 	//Routing by Opcode
    URT.io.i_fc3    := RRU.io.o_fc3 	//Func3 Value


    //Stage-4
    //Control Status Register
    CSU.io.i_vld	:= REG.io.o_exe && URT.io.o_is_CSU  				//Validate CSU
    CSU.io.i_rs1	:= Mux(ROB.io.o_bps1, ROB.io.o_dat1, REG.io.o_cs1)	//Operand Data Word-1
    CSU.io.i_imm	:= REG.io.o_imm										//Immediate Value
    CSU.io.i_fc3    := REG.io.o_fc3                                     //Func3 Value
    CSU.io.i_wrn    := REG.io.o_wrn                                     //WB Reg No.
    CSU.io.i_rn1    := REG.io.o_rn1

    //Arithmetic/Logic Unit
    ALU.io.i_UID	:= URT.io.o_UID										//Executing Datapath ID
    ALU.io.i_vld  	:= REG.io.o_exe && URT.io.o_is_ALU  				//Validate ALU
    ALU.io.i_wrn    := REG.io.o_wrn                                     //WB Reg No.
    ALU.io.i_fc3  	:= REG.io.o_fc3										//Func3 Value
    ALU.io.i_fc7  	:= REG.io.o_fc7										//Func7 Value
    ALU.io.i_imm    := REG.io.o_imm										//Immediate Value
    ALU.io.i_rs1  	:= Mux(ROB.io.o_bps1, ROB.io.o_dat1, REG.io.o_as1)	//Operand Data Word-1
    ALU.io.i_rs2  	:= Mux(ROB.io.o_bps2, ROB.io.o_dat2, REG.io.o_as2)	//Operand Data Word-2

    //Load/Store Unit
    LSU.io.i_vld  	:= REG.io.o_exe && URT.io.o_is_LSU  				//Validate LSU
    LSU.io.i_opc  	:= REG.io.o_opcode									//Opcode
    LSU.io.i_wrn    := REG.io.o_wrn                                     //WB Reg No.
    LSU.io.i_fc3  	:= REG.io.o_fc3 									//Func3 Value
    LSU.io.i_rs1  	:= Mux(ROB.io.o_bps1, ROB.io.o_dat1, REG.io.o_ls1)	//Operand Data Word-1
    LSU.io.i_rs2  	:= Mux(ROB.io.o_bps2, ROB.io.o_dat2, REG.io.o_ls2)	//Operand Data Word-2
    LSU.io.i_imm  	:= REG.io.o_imm 									//Immediate Value

    //Branch Unit
    BRU.io.i_vld  	:= REG.io.o_exe && URT.io.o_is_BRU  				//Validate BRU
    BRU.io.i_jal  	:= REG.io.o_opcode 							        //Opcode
    BRU.io.i_wrn    := REG.io.o_wrn                                     //WB Reg No.
    BRU.io.i_rs1  	:= Mux(ROB.io.o_bps1, ROB.io.o_dat1, REG.io.o_bs1)	//Operand Data Word-1
    BRU.io.i_rs2  	:= Mux(ROB.io.o_bps2, ROB.io.o_dat2, REG.io.o_bs2)	//Operand Data Word-2
    BRU.io.i_fc3    := REG.io.o_fc3                                     //Func3 Value
    BRU.io.i_imm  	:= REG.io.o_imm 									//Immediate Value


    //Stage-5
    //Write-Back
	ROB.io.i_set    := RRU.io.o_exe
    ROB.io.i_wrn    := RRU.io.o_wno

    ROB.io.i_wrb_c  := CSU.io.o_wrb
    ROB.io.i_wrb_a  := ALU.io.o_wrb
    ROB.io.i_wrb_b  := BRU.io.o_wrb
    ROB.io.i_wrb_m  := LSU.io.o_wrb

    ROB.io.i_wrn_c  := CSU.io.o_wrn
    ROB.io.i_wrn_a  := ALU.io.o_wrn
    ROB.io.i_wrn_b  := BRU.io.o_wrn
    ROB.io.i_wrn_m  := LSU.io.o_wrn

    ROB.io.i_dat_c  := CSU.io.o_dst
    ROB.io.i_dat_a  := ALU.io.o_dst
    ROB.io.i_dat_b  := BRU.io.o_dst
    ROB.io.i_dat_m  := LSU.io.o_dst

    ROB.io.i_rs1	:= RRU.io.o_rn1
    ROB.io.i_rs2    := RRU.io.o_rn2


	//Commit
    REG.io.i_wed	:= ROB.io.o_wrb 	//Write-enable
	REG.io.i_wrn 	:= ROB.io.o_wrn		//Write-index
    REG.io.i_wrb_d	:= ROB.io.o_dat		//Write-Data
    REG.io.i_pc     := BRU.io.o_pc      //PC Value


    //Instruction Memory Interface
    io.iadr       	:= BRU.io.o_pc		//Program Counter
    io.ireq       	:= FCH.io.o_ireq	//Instr. Fetch Req.
    FCH.io.i_iack 	:= io.iack      	//Instr. Fetch Ack.


    //Data Memory Interface
    io.dreq       	:= LSU.io.o_dreq	//Data Memory Access Req.
    io.stor       	:= LSU.io.o_stor	//Store Req.
    LSU.io.i_dack 	:= io.dack      	//Data Memory Access Ack.

    io.mar        	:= LSU.io.o_dmar	//Data Memory Address
    LSU.io.i_idat 	:= io.idat      	//Loading Data Word
    io.odat       	:= LSU.io.o_odat	//Data Word I/F
}
