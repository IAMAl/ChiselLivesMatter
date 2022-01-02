// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package alu

import chisel3._
import chisel3.util._

import params._

class ALU extends Module {


    /* I/O                          */
    val io = IO(new Bundle {
        val i_vld       = Input( Bool())                                // Exec Validation
        val i_rs1       = Input( UInt((params.Parameters.DatWidth).W))  // RegisterFile Source-1
        val i_rs2       = Input( UInt((params.Parameters.DatWidth).W))  // RegisterFile Source-2
        val i_fc3       = Input( UInt((params.Parameters.Fc3Width).W))  // Immadiate (Func3)
        val i_fc7       = Input( UInt((params.Parameters.Fc7Width).W))  // Immediate (Func7)
        val o_dst       = Output(UInt((params.Parameters.DatWidth).W))  // RegisterFile Destination
        val o_wrb       = Output(Bool())                                // Writeback Request
        val i_UnitID    = Input( UInt(3.W))                             // Operation Unit ID
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
    Add.io.i_vld    := (io.i_UnitID === (params.Parameters.UnitID_Add).U) && io.i_vld
    Add.io.i_fc3    := io.i_fc3
    Add.io.i_fc7    := io.i_fc7
    Add.io.i_rs1    := io.i_rs1
    Add.io.i_rs2    := io.i_rs2

    //Logic
    Lgc.io.i_vld    := (io.i_UnitID === (params.Parameters.UnitID_Lgc).U) && io.i_vld
    Lgc.io.i_fc3    := io.i_fc3
    Lgc.io.i_fc7    := io.i_fc7
    Lgc.io.i_rs1    := io.i_rs1
    Lgc.io.i_rs2    := io.i_rs2

    //Shifter
    Sft.io.i_vld    := (io.i_UnitID === (params.Parameters.UnitID_Sft).U) && io.i_vld
    Sft.io.i_fc3    := io.i_fc3
    Sft.io.i_fc7    := io.i_fc7
    Sft.io.i_rs1    := io.i_rs1
    Sft.io.i_rs2    := io.i_rs2

    //Output
    vld         := io.i_vld
    io.o_wrb    := vld

    //ORed 
    //Exclusive-Output by NOP
    dst         := Add.io.o_dst | Lgc.io.o_dst | Sft.io.o_dst
    io.o_dst    := dst
}
