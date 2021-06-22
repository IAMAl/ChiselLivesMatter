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

    /* I/O                          */
    val io = IO(new NICO2_IO)


    /* Module                       */
    val FCH = Module(new FCH)     //Instruction Fetch Unit
    val SCH = Module(new SCH)     //Scheduler
    val REG = Module(new REG)     //Register File

    val URT = Module(new URT)     //Router

    val ALU = Module(new ALU)     //Arithmetic/Logic Unit
    val LSU = Module(new LSU)     //Load/Store Unit
    val BRU = Module(new BRU)     //Branch Unit


    /* Assign                       */
    //Stage-1: Instruction Fetch
    FCH.io.boot := io.boot        //Kick-Start(High-Active)
    FCH.io.ifch := io.inst        //Input 32b Word Instruction
    FCH.io.brc  := BRU.io.brc     //Flush by Branch Taken
    FCH.io.stall:= SCH.io.cnd     //Branch Unit is Active (so stall)


    //Stage-2: Scheduler
    SCH.io.vld  := FCH.io.exe     //Validate Scheduler
    SCH.io.ins  := FCH.io.ins     //Feed Instruction Word


    //Stage-3: Register-Read
    REG.io.vld  := SCH.io.exe     //Validate Register File
    REG.io.opc  := SCH.io.opc     //Opcode
    REG.io.by1  := SCH.io.by1     //Bypassing Data Word-1
    REG.io.by2  := SCH.io.by2     //Bypassing Data Word-2
    REG.io.re1  := SCH.io.re1     //Read Enable-1
    REG.io.re2  := SCH.io.re2     //Read Enable-2
    REG.io.rn1  := SCH.io.rn1     //Read Register Number-1
    REG.io.rn2  := SCH.io.rn2     //Read Register Number-2
    REG.io.fc3  := SCH.io.fc3     //Func3 Value
    REG.io.fc7  := SCH.io.fc7     //Func7 Value
    REG.io.wno  := SCH.io.wno     //Write Register Number

    //Stage-3: Route to Exec Units
    URT.io.opc  := SCH.io.opc     //Routing by Opcode


    //Stage-4: Arithmetic/Logic Unit
    ALU.io.UnitID := URT.io.UnitID//Executing Datapath ID
    ALU.io.vld  := REG.io.exe && URT.io.is_ALU  //Validate ALU
    ALU.io.fc3  := REG.io.fc3_o   //Func3 Value
    ALU.io.fc7  := REG.io.fc7_o   //Func7 Value
    ALU.io.rs1  := REG.io.as1     //Operand Data Word-1
    ALU.io.rs2  := REG.io.as2     //Operand Data Word-2

    //Stage-4: Load/Store Unit
    LSU.io.vld  := REG.io.exe && URT.io.is_LSU  //Validate LSU
    LSU.io.opc  := REG.io.opcode  //Opcode
    LSU.io.fc3  := REG.io.fc3_o   //Func3 Value
    LSU.io.rs1  := REG.io.ls1     //Operand Data Word-1
    LSU.io.rs2  := REG.io.ls2     //Operand Data Word-2
    LSU.io.imm  := REG.io.imm     //Immediate Value

    //Stage-4: Branch Unit
    BRU.io.vld  := REG.io.exe && URT.io.is_BRU  //Validate BRU
    BRU.io.jal  := REG.io.opcode(3, 2)  //Opcode
    BRU.io.rn1  := REG.io.rn1_o   //Read Register Number-1
    BRU.io.rn2  := REG.io.rn2_o   //Read Register Number-2
    BRU.io.fc3  := REG.io.fc3_o   //Func3 Value
    BRU.io.fc7  := REG.io.fc7_o   //Func7 Value
    BRU.io.rs1  := REG.io.bs1     //Operand Data Word-1
    BRU.io.rs2  := REG.io.bs2     //Operand Data Word-2
    BRU.io.imm  := REG.io.imm     //Operand Immediate Value


    //Stage-5: Write-Back
    REG.io.wed  := SCH.io.wed     //Write-enable
    REG.io.wrb_r:= ALU.io.wrb || LSU.io.wrb || BRU.io.wrb
    REG.io.wrb_d:= ALU.io.dst |  LSU.io.dst |  BRU.io.dst


    //Instruction Memory Interface
    io.iadr     := BRU.io.pc      //Program Counter
    io.ireq     := FCH.io.ireq    //Instr. Fetch Req.
    FCH.io.iack := io.iack        //Instr. Fetch Ack.


    //Data Memory Interface
    io.dreq     := LSU.io.dreq    //Data Memory Access Req.
    io.stor     := LSU.io.stor    //Store Req.
    LSU.io.dack := io.dack        //Data Memory Access Ack.

    io.mar      := LSU.io.dmar    //Data Memory Address
    LSU.io.idat := io.idat        //Loading Data Word
    io.odat     := LSU.io.odat    //Data Word I/F
}

object NicoNicoMain extends App {
  chisel3.Driver.execute(args,()=>new NicoNico)
}
