// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
// Branch Unit
package bru

import chisel3._
import chisel3.util._

import params._

class BRU extends Module {

    val InitPC      = params.Parameters.InitPC.U
    val JAL         = params.Parameters.OP_JAL.U
    val JALR        = params.Parameters.OP_JALR.U
    val AddrWidth   = params.Parameters.AddrWidth


    /* I/O                              */
    val io      = IO(new BRU_IO)


    /* Register                         */
    //Program Counter
    val PC      = RegInit(UInt(AddrWidth.W), InitPC)


    /* Wire                             */
    //Brach Condition
    val BRC     = Wire(Bool())

    //Jump Address
    val jmp     = Wire(SInt(AddrWidth.W))

    //Link
    val LNK     = Wire(UInt(AddrWidth.W))

    //Immediate
    val imm     = Wire(UInt(AddrWidth.W))


    /* Assign                           */
    //Jump-Immediate Composition
    imm     := io.i_imm
    when (io.i_jal === JAL) {
        //Jump and Link
        jmp := imm.asSInt()
    }
    .elsewhen (io.i_jal === JALR) {
        //Jump and Link with Register-0
        jmp := imm.asSInt()
    }
    .otherwise {
        jmp := 0.S
    }

    BRC     := DontCare
    when (io.i_vld) {
        //Program Counter and Link
        when (io.i_jal === JAL) {
            //Jump and Link
            PC  := jmp.asUInt
            LNK := PC + 4.U
        }
        .elsewhen (io.i_jal === JALR) {
            //Jump and Link Register
            //Indirect-Jump
            PC  := io.i_rs1.asSInt + jmp
            LNK := PC + 4.U
        }
        .elsewhen (BRC && (io.i_jal === 0.U)) {
            //Branch Taken
            PC  := (PC.asSInt() + imm.asSInt()).asUInt()
        }
        .elsewhen (!BRC && (io.i_jal === 0.U)) {
            //Branch NOT Taken
            PC  := PC + 4.U
        }

        //Branch Condition
        switch(io.i_fc3) {
            is((params.Parameters.FC3_BEQ).U) {
                //Equal (Signed)
                BRC    := (io.i_rs1.asSInt === io.i_rs2.asSInt)
            }
            is((params.Parameters.FC3_BNE).U) {
                //Not Equal (Signed)
                BRC    := (io.i_rs1.asSInt =/= io.i_rs2.asSInt)
            }
            is((params.Parameters.FC3_BLT).U) {
                //Less Than (Signed)
                BRC    := (io.i_rs1.asSInt <   io.i_rs2.asSInt)
            }
            is((params.Parameters.FC3_BGE).U) {
                //Greater Than or Equal (Signed)
                BRC    := (io.i_rs1.asSInt >=  io.i_rs2.asSInt)
            }
            is((params.Parameters.FC3_BLTU).U) {
                //Less Than (Unsigned)
                BRC    := (io.i_rs1 <  io.i_rs2)
            }
            is((params.Parameters.FC3_BGEU).U) {
                //Greater Than or Equal (Unsigned)
                BRC    := (io.i_rs1 >= io.i_rs2)
            }
        }
    }


    /* Output                           */
    //Branch Condition
    io.o_brc    := BRC

    //Program Counter Value
    io.o_pc     := PC

    //Write-back Request
    io.o_wrb    := ((io.i_jal === JAL) || (io.i_jal === JALR)) && io.i_vld

    //Link Value
    io.o_dst    := LNK

    io.o_wrn    := io.i_wrn
}