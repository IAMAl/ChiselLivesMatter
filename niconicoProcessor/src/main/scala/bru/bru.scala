// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
// Branch Unit
package bru

import chisel3._
import chisel3.util._

import params._

class BRU extends Module {

    val InitPC  = params.Parameters.InitPC.U
    val JAL     = params.Parameters.OP_JAL.U
    val JALR    = params.Parameters.OP_JALR.U

    /* I/O                          */
    val io      = IO(new BRU_IO)


    /* Register                     */
    //Write-back Flag
    val WRB     = RegInit(Bool(), false.B)

    //Program Counter
    val PC      = RegInit(UInt((params.Parameters.AddrWidth).W), InitPC)

    //Link Register
    val LNK     = RegInit(UInt((params.Parameters.AddrWidth).W), InitPC)

    // Branch Condition
    val BC      = RegInit(Bool(), false.B)


    /* Wire                         */
    //Brach Condition
    val BRC     = Wire(Bool())

    //Pre Program Counter Value
    val PC_in   = Wire(UInt((params.Parameters.AddrWidth+1).W))

    //Jump Address
    val jmp     = Wire(SInt((params.Parameters.AddrWidth).W))


    /* Assign                       */
    //Jump and Link
    jmp := io.i_imm

    PC_in   := PC
    BRC     := DontCare
    when (io.i_vld) {
        //Program Counter and Link
        when (io.i_jal === JAL) {
            //Jump and Link
            PC  := jmp
            LNK := PC + 4.U
        }
        .elsewhen (io.i_jal === JALR) {
            //Jump and Link Register
            PC  := io.i_rs1 + jmp.asSInt
            LNK := PC + 4.U
        }
        .elsewhen (BRC && (io.i_jal === 0.U)) {
            //Branch Taken
            PC  := PC_in + io.i_imm.asSInt
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


    /* Output                       */
    //Branch Condition
    BC          := BRC
    io.o_brc    := BC

    //Program Counter Value
    io.o_pc     := PC

    //Write-back Request
    WRB         := ((io.i_jal === JAL) || (io.i_jal === JALR)) && io.i_vld
    io.o_wrb    := WRB

    //Link Value
    io.o_dst    := LNK
}