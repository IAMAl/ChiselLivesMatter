// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
// RISC-V Processor Top Module
package niconico

import chisel3._
import chisel3.util._

import fch._
import route._
import sch._
import bru._
import reg._
import alu._
import lsu._

class NicoNico extends Module {


    /* I/O                    		  	*/
    val io = IO(new NICO2_IO)


    /* Module                       	*/
    val FCH = Module(new FCH)     		//Instruction Fetch Unit
    val SCH = Module(new SCH)     		//Scheduler
    val REG = Module(new REG)     		//Register File
    val ROB = Module(new ROB)   		//ROB

    val URT = Module(new URT)     		//Router

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
    FCH.io.i_stall	:= SCH.io.o_hzd ||  //Stall by Hazard
						ROB.io.o_full


    //Stage-2
    //Scheduler
    SCH.io.i_vld  	:= FCH.io.o_exe 	//Validate Scheduler
    SCH.io.i_ins  	:= FCH.io.o_ins 	//Feed Instruction Word


    //Stage-3
    //Register-Read
    REG.io.i_vld  	:= SCH.io.o_exe 	//Validate Register File
    REG.io.i_opc  	:= SCH.io.o_opc 	//Opcode
    REG.io.i_by1  	:= SCH.io.o_by1 	//Bypassing Data Word-1
    REG.io.i_by2  	:= SCH.io.o_by2 	//Bypassing Data Word-2
    REG.io.i_re1  	:= SCH.io.o_re1 	//Read Enable-1
    REG.io.i_re2  	:= SCH.io.o_re2 	//Read Enable-2
    REG.io.i_rn1  	:= SCH.io.o_rn1 	//Read Register Number-1
    REG.io.i_rn2  	:= SCH.io.o_rn2 	//Read Register Number-2
    REG.io.i_fc3  	:= SCH.io.o_fc3 	//Func3 Value
    REG.io.i_fc7  	:= SCH.io.o_fc7 	//Func7 Value
    REG.io.i_wno  	:= SCH.io.o_wno 	//Write Register Number

    //Route to Exec Units
    URT.io.i_opc  	:= SCH.io.o_opc 	//Routing by Opcode


    //Stage-4
    //Control Status Register
    CSU.io.i_vld	:= REG.io.o_exe && URT.io.o_is_CSU  				//Validate CSU
    CSU.io.i_rs1	:= Mux(ROB.io.o_bps1, ROB.io.o_dat1, REG.io.o_cs1)	//Operand Data Word-1
    CSU.io.i_imm	:= REG.io.o_imm										//Immediate Value

    //Arithmetic/Logic Unit
    ALU.io.i_UID	:= URT.io.o_UID										//Executing Datapath ID
    ALU.io.i_vld  	:= REG.io.o_exe && URT.io.o_is_ALU  				//Validate ALU
    ALU.io.i_fc3  	:= REG.io.o_fc3										//Func3 Value
    ALU.io.i_fc7  	:= REG.io.o_fc7										//Func7 Value
    ALU.io.i_rs1  	:= Mux(ROB.io.o_bps1, ROB.io.o_dat1, REG.io.o_as1)	//Operand Data Word-1
    ALU.io.i_rs2  	:= Mux(ROB.io.o_bps2, ROB.io.o_dat2, REG.io.o_as2)	//Operand Data Word-2

    //Load/Store Unit
    LSU.io.i_vld  	:= REG.io.o_exe && URT.io.o_is_LSU  				//Validate LSU
    LSU.io.i_opc  	:= REG.io.o_opcode									//Opcode
    LSU.io.i_fc3  	:= REG.io.o_fc3 									//Func3 Value
    LSU.io.i_rs1  	:= Mux(ROB.io.o_bps1, ROB.io.o_dat1, REG.io.o_ls1)	//Operand Data Word-1
    LSU.io.i_rs2  	:= Mux(ROB.io.o_bps2, ROB.io.o_dat2, REG.io.o_ls2)	//Operand Data Word-2
    LSU.io.i_imm  	:= REG.io.o_imm 									//Immediate Value

    //Branch Unit
    BRU.io.i_vld  	:= REG.io.o_exe && URT.io.o_is_BRU  				//Validate BRU
    BRU.io.i_jal  	:= REG.io.o_opcode(3, 2)  							//Opcode
    BRU.io.i_rs1  	:= Mux(ROB.io.o_bps1, ROB.io.o_dat1, REG.io.o_bs1)	//Operand Data Word-1
    BRU.io.i_rs2  	:= Mux(ROB.io.o_bps2, ROB.io.o_dat2, REG.io.o_bs2)	//Operand Data Word-2
    BRU.io.i_imm  	:= REG.io.o_imm 									//Immediate Value


    //Stage-5
    //Write-Back
	ROB.io.i_set    := SCH.io.o_exe
    ROB.io.i_wrn    := SCH.io.o_wno

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

    ROB.io.i_rs1	:= SCH.io.o_rn1
    ROB.io.i_rs2    := SCH.io.o_rn2


	//Commit
    REG.io.i_wed	:= ROB.io.o_wrb 	//Write-enable
	REG.io.i_wrn 	:= ROB.io.o_wrn		//Write-index
    REG.io.i_wrb_d	:= ROB.io.o_dat		//Write-Data


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

object NicoNicoMain extends App {
  chisel3.Driver.execute(args,()=>new NicoNico)
}
