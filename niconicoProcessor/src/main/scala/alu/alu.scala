// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package alu

import chisel3._
import chisel3.util._

import params._

class ALU extends Module {

    /* I/O                          */
    val io = IO(new Bundle {
        val vld     = Input( Bool())                                // Exec Validation
        val rs1     = Input( UInt((params.Parameters.DatWidth).W))  // RegisterFile Source-1
        val rs2     = Input( UInt((params.Parameters.DatWidth).W))  // RegisterFile Source-2
        val fc3     = Input( UInt((params.Parameters.Fc3Width).W))  // Immadiate (Func3)
        val fc7     = Input( UInt((params.Parameters.Fc7Width).W))  // Immediate (Func7)
        val dst     = Output(UInt((params.Parameters.DatWidth).W))  // RegisterFile Destination
        val wrb     = Output(Bool())                                // Writeback Request
        val UnitID  = Input( UInt(3.W))                             // Operation Unit ID
    })

    /* Module                       */
    val Add     = Module(new Add)       //Adder
    val Lgc     = Module(new Lgc)       //Logic
    val Sft     = Module(new Sft)       //Shifter


    /* Register                     */
    val vld     = RegInit(Bool(), false.B)
    val dst     = Reg(UInt((params.Parameters.DatWidth).W))


    /* Assign                       */
    //Adder
    Add.io.vld   := (io.UnitID === (params.Parameters.UnitID_Add).U) && io.vld
    Add.io.fc3   := io.fc3
    Add.io.fc7   := io.fc7
    Add.io.rs1   := io.rs1
    Add.io.rs2   := io.rs2

    //Logic
    Lgc.io.vld   := (io.UnitID === (params.Parameters.UnitID_Lgc).U) && io.vld
    Lgc.io.fc3   := io.fc3
    Lgc.io.fc7   := io.fc7
    Lgc.io.rs1   := io.rs1
    Lgc.io.rs2   := io.rs2

    //Shifter
    Sft.io.vld   := (io.UnitID === (params.Parameters.UnitID_Sft).U) && io.vld
    Sft.io.fc3   := io.fc3
    Sft.io.fc7   := io.fc7
    Sft.io.rs1   := io.rs1
    Sft.io.rs2   := io.rs2

    //Output
    vld     := io.vld
    io.wrb  := vld

    //ORed 
    //Exclusive-Output by NOP
    dst     := Add.io.dst | Lgc.io.dst | Sft.io.dst
    io.dst  := dst
}
