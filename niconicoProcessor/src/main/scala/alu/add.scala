// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package alu

import chisel3._
import chisel3.util._

import params._
import isa._

class Add extends Module {

    //I/O
    val io = IO(new ALU_IO)

    //Wire
    val rs2     = Wire(UInt((params.Parameters.DatWidth).W))

    //Assign
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
    when (io.vld) {
        //Addition
        io.dst   := io.rs1 + rs2 + (io.fc7 === (params.Parameters.FC7_SUB).U).asUInt
    }
    .otherwise {
        //NOP
        io.dst   := 0.U
    }
}
