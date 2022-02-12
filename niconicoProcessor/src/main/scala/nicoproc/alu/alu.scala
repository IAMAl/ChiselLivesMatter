// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package alu

import chisel3._
import chisel3.util._

import params._

class ALU extends Module {


    /* I/O                                  */
    val io = IO(new Bundle {
        val i_vld       = Input( Bool())                                // Exec Validation
        val i_rs1       = Input( UInt((params.Parameters.DatWidth).W))  // RegisterFile Source-1
        val i_rs2       = Input( UInt((params.Parameters.DatWidth).W))  // RegisterFile Source-2
        val i_imm       = Input( UInt((params.Parameters.DatWidth).W))  // Immediate
        val i_fc3       = Input( UInt((params.Parameters.Fc3Width).W))  // Immediate (Func3)
        val i_fc7       = Input( UInt((params.Parameters.Fc7Width).W))  // Immediate (Func7)
        val i_wrn       = Input( UInt((params.Parameters.LogNumReg).W)) // Write-Back Index
        val o_wrn       = Output(UInt((params.Parameters.LogNumReg).W)) // Write-Back Index
        val o_dst       = Output(UInt((params.Parameters.DatWidth).W))  // RegisterFile Destination
        val o_wrb       = Output(Bool())                                // Writeback Request
        val i_UID       = Input( UInt(3.W))                             // Operation Unit ID
    })


    /* Module                               */
    val Add     = Module(new Add)           //Adder
    val Lgc     = Module(new Lgc)           //Logic
    val Sft     = Module(new Sft)           //Shifter


    /* Assign                               */
    //Adder
    Add.io.i_vld    := (io.i_UID === (params.Parameters.UnitID_Add).U) && io.i_vld
    Add.io.i_fc3    := io.i_fc3
    Add.io.i_fc7    := io.i_fc7
    Add.io.i_rs1    := io.i_rs1
    Add.io.i_rs2    := io.i_rs2
    Add.io.i_imm    := io.i_imm

    //Logic
    Lgc.io.i_vld    := (io.i_UID === (params.Parameters.UnitID_Lgc).U) && io.i_vld
    Lgc.io.i_fc3    := io.i_fc3
    Lgc.io.i_fc7    := io.i_fc7
    Lgc.io.i_rs1    := io.i_rs1
    Lgc.io.i_rs2    := io.i_rs2
    Lgc.io.i_imm    := 0.U

    //Shifter
    Sft.io.i_vld    := (io.i_UID === (params.Parameters.UnitID_Sft).U) && io.i_vld
    Sft.io.i_fc3    := io.i_fc3
    Sft.io.i_fc7    := io.i_fc7
    Sft.io.i_rs1    := io.i_rs1
    Sft.io.i_rs2    := io.i_rs2
    Sft.io.i_imm    := 0.U

    //Output
    io.o_wrb        := io.i_vld

    //ORed
    //Exclusive-Output by NOP
    io.o_dst        := Add.io.o_dst | Lgc.io.o_dst | Sft.io.o_dst

    io.o_wrn       := io.i_wrn
}
