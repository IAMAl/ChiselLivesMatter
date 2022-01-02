// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package route

import chisel3._
import chisel3.util._

import isa._
import params._

class URT extends Module {

    /* I/O                          */
    val io = IO(new Bundle {
        val i_opc       = Input( UInt((params.Parameters.OpcWidth).W))
        val o_UID       = Output(UInt(3.W))
        val o_EnWB      = Output(Bool())
        val o_is_ALU    = Output(Bool())
        val o_is_LSU    = Output(Bool())
        val o_is_BRU    = Output(Bool())
    })

    /* Module                       */
    //Opcode Bit-Field Extraction
    val ISA_Opcode  = Module(new ISA_Opcode)


    /* Register                     */
    val UnitID  = Reg(UInt(3.W))
    val EnWB    = Reg(Bool())
    val is_ALU  = Reg(Bool())
    val is_LSU  = Reg(Bool())
    val is_BRU  = Reg(Bool())


    /* Assign                       */
    //Routing (UnitID) Assignment and Write-Back Enable
    ISA_Opcode.io.i_opc := io.i_opc
    UnitID              := ISA_Opcode.io.o_OpcodeType

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
    io.o_UID    := UnitID
    io.o_EnWB   := EnWB

    is_ALU      := (UnitID === (params.Parameters.OP_RandI).U) || (UnitID === (params.Parameters.OP_RandR).U)
    io.o_is_ALU := is_ALU

    is_LSU      := (UnitID === (params.Parameters.OP_LOAD).U)  || (UnitID === (params.Parameters.OP_STORE).U)
    io.o_is_LSU := is_LSU

    is_BRU      := (UnitID === (params.Parameters.OP_BRJMP).U) || (UnitID === (params.Parameters.OP_JAL).U)
    io.o_is_BRU := is_BRU
}
