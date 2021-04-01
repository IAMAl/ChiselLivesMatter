// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
//Branch Unit
package bru

import chisel3._
import chisel3.util._

import params._

class BRU extends Module {

    val InitPC  = params.Parameters.InitPC

    //I/O
    val io      = IO(new BRU_IO)

    /* Register                 */
    //Write-back Flag
    val WRB     = RegInit(Bool(), false.B)

    //Program Counter
    val PC      = RegInit(UInt((params.Parameters.AddrWidth).W), InitPC.U)

    //Link Register
    val LNK     = RegInit(UInt((params.Parameters.AddrWidth).W), InitPC.U)

    /* Wire                     */
    //Brach Condition
    val BRC     = Wire(Bool())

    //Pre Program Counter Value
    val PC_in   = Wire(UInt((params.Parameters.AddrWidth+1).W))

    //Jump Address
    val jmp     = Wire(UInt((params.Parameters.AddrWidth).W))

    //Immediate to Jump/Branch
    val imm     = Wire(UInt(21.W))


    /* Assign                   */
    //Jump-Immediate Composition
    imm     := Cat(io.fc7, io.rn2, io.rn1, io.fc3)
    when (io.jal === 0.U) {
        jmp := 0.U
    }
    .elsewhen (io.jal === 1.U) {
        jmp := Cat(imm(20), imm(7, 0), imm(8), imm(19, 9))
    }  
    .elsewhen (io.jal === 2.U) {
        jmp := 0.U
    } 
    .elsewhen (io.jal === 3.U) {
        jmp := imm(20, 9).asSInt.asUInt
    }
    .otherwise {
        jmp := 0.U
    }

    PC_in   := PC.asUInt
    BRC     := DontCare
    when (io.vld) {

        //Program Counter and Link
        when (io.jal === 1.U) {
            //Jump and Link
            PC  := jmp << 1.U
            LNK := PC + 4.U
        }
        .elsewhen (io.jal === 3.U) {
            //Jump and Link Register
            PC  := (io.rs1.asSInt + jmp.asSInt).asUInt
            LNK := PC + 4.U
        }
        .elsewhen (BRC && (io.jal === 0.U)) {
            //Branch Taken
            PC  := (PC_in.asSInt + io.imm.asSInt).asUInt
        }
        .elsewhen (!BRC && (io.jal === 0.U)) {
            //Branch NOT Taken
            PC  := PC + 4.U
        }

        //Branch Condition
        switch(io.fc3) {
            is((params.Parameters.FC3_BEQ).U) {
                //Equal (Signed)
                BRC    := (io.rs1.asSInt === io.rs2.asSInt).asBool
            }
            is((params.Parameters.FC3_BNE).U) {
                //Not Equal (Signed)
                BRC    := (io.rs1.asSInt =/= io.rs2.asSInt).asBool
            }
            is((params.Parameters.FC3_BLT).U) {
                //Less Than (Signed)
                BRC    := (io.rs1.asSInt <   io.rs2.asSInt).asBool
            }
            is((params.Parameters.FC3_BGE).U) {
                //Greater Than or Equal (Signed)
                BRC    := (io.rs1.asSInt >=  io.rs2.asSInt).asBool
            }
            is((params.Parameters.FC3_BLTU).U) {
                //Less Than (Unsigned)
                BRC    := (io.rs1 <  io.rs2).asBool
            }
            is((params.Parameters.FC3_BGEU).U) {
                //Greater Than or Equal (Unsigned)
                BRC    := (io.rs1 >= io.rs2).asBool
            }
        }
    }

    //Output
    //
    io.brc  := BRC
    
    //Program Counter Value
    io.pc   := PC

    //Write-back Request
    WRB     := ((io.jal === 1.U) || (io.jal === 3.U)) && io.vld
    io.wrb  := WRB

    //Link Value
    io.dst  := LNK
}