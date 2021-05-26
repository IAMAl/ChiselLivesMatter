// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package alu

import chisel3._
import chisel3.util._

import params._
import isa._

class Add extends Module {

    /* I/O                          */
    val io = IO(new ALU_IO)

    /* Wire                         */
    val rs2     = Wire(UInt((params.Parameters.DatWidth).W))
    val c_in    = Wire(UInt(1.W))

    /* Assign                       */
    //Selection of Right-Source Operand
    when (io.fc7 === (params.Parameters.FC7_SUB).U) {
        //1's Complement for Subtraction
        rs2 := ~io.rs2
    }
    .elsewhen (io.fc7 === (params.Parameters.FC7_ADD).U) {
        //Addition
        rs2 := io.rs2
    }
    .otherwise {
        //NOP
        rs2 := 0.U
    }

    //Addition
    //Carry-in for Subtraction to make 2's Complemt Binary
    c_in    := (io.fc7 === (params.Parameters.FC7_SUB).U).asUInt
    when (io.vld) {
        //Addition
        io.dst   := io.rs1 + rs2 + c_in
    }
    .otherwise {
        //NOP
        io.dst   := 0.U
    }
}