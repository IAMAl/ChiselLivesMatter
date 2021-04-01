// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
//RISC-V Instruction Set Architecture (ISA) Definision
package isa

import chisel3._
import chisel3.util._

import params._


class ISA_Opcode extends Module {

    val io = IO(new Bundle {
        val opc         = Input( UInt((params.Parameters.OpcWidth).W))
        val OpcodeType  = Output(UInt(3.W))
    })

    //Opcode Classification
    io.OpcodeType   := io.opc(params.Parameters.OpcWidth-1, params.Parameters.OpcWidth-3)
}

class ISA_fc3_lsu extends Module {

    val io = IO(new Bundle {
        val fc3     = Input( UInt((params.Parameters.Fc3Width).W))
        val LSType  = Output(UInt((params.Parameters.Fc3Width-1).W))
        val usign   = Output(Bool())
    })

    //Memory Access Classification
    io.LSType   := io.fc3(params.Parameters.Fc3Width-2, 0)
    io.usign    := io.fc3(params.Parameters.Fc3Width-1)
}
