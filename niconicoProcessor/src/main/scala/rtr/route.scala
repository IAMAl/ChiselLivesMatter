// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package route

import chisel3._
import chisel3.util._

import isa._
import params._

class URT extends Module {

    //I/O
    val io = IO(new Bundle {
        val opc     = Input( UInt((params.Parameters.OpcWidth).W))
        val UnitID  = Output(UInt(3.W))
        val EnWB    = Output(Bool())
        val is_ALU  = Output(Bool())
        val is_LSU  = Output(Bool())
        val is_BRU  = Output(Bool())
    })

    //Module
    //Opcode Bit-Field Extraction
    val ISA_Opcode  = Module(new ISA_Opcode)

    //Register
    val UnitID  = Reg(UInt(3.W))
    val EnWB    = Reg(Bool())

    //Assign
    //Routing (UnitID) Assignment and Write-Back Enable
    ISA_Opcode.io.opc   := io.opc
    UnitID              := ISA_Opcode.io.OpcodeType

    //Write-Back Enable Assertion
    when (  (UnitID === (params.Parameters.OP_RandI).U) || 
            (UnitID === (params.Parameters.OP_RandR).U) || 
            (UnitID === (params.Parameters.OP_LOAD).U)  ||
            (UnitID === (params.Parameters.OP_JAL).U)
            ) {
        EnWB    := true.B
    }
    .otherwise {
        EnWB    := false.B
    }

    //Output
    io.UnitID   := UnitID
    io.EnWB     := EnWB
    io.is_ALU   := (UnitID === (params.Parameters.OP_RandI).U) || (UnitID === (params.Parameters.OP_RandR).U)
    io.is_LSU   := (UnitID === (params.Parameters.OP_LOAD).U)  || (UnitID === (params.Parameters.OP_STORE).U)
    io.is_BRU   := (UnitID === (params.Parameters.OP_BRJMP).U) || (UnitID === (params.Parameters.OP_JAL).U)
}
