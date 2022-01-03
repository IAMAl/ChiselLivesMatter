// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
// RISC-V Instruction Set Architecture (ISA) Definision
package isa

import chisel3._
import chisel3.util._

import params._

class ISA_ILEncode extends Module {


     /* I/O                         */
    val io = IO(new Bundle {
        //Opcode
        val i_ilenc     = Input( UInt((params.Parameters.ILEnc).W))

        //RV32I Instr Validation
        val o_ilv       = Output(Bool())
    })

    //Check 32b-Length ISA
    io.o_ilv    := (io.i_ilenc === 3.U)
}

class ISA_Opcode extends Module {


     /* I/O                         */
    val io = IO(new Bundle {
        //Opcode
        val i_opc       = Input( UInt((params.Parameters.OpcWidth).W))

        //Operation Type
        val o_OpcodeType= Output(UInt(3.W))
    })

    //Opcode Classification
    io.o_OpcodeType   := io.i_opc(params.Parameters.OpcWidth-1, params.Parameters.OpcWidth-3)
}

class ISA_fc3_lsu extends Module {


     /* I/O                         */
    val io = IO(new Bundle {
        val i_fc3       = Input( UInt((params.Parameters.Fc3Width).W))
        val o_LSType    = Output(UInt((params.Parameters.Fc3Width-1).W))
        val o_LUSign    = Output(Bool())
    })

    //Memory Access Classification
    io.o_LSType := io.i_fc3(params.Parameters.Fc3Width-2, 0)
    io.o_LUSign := io.i_fc3(params.Parameters.Fc3Width-1)
}
