// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package alu

import chisel3._
import chisel3.util._

import params._
import isa._

class Lgc extends Module {

    //I/O
    val io = IO(new ALU_IO)

    io.dst  := DontCare
    when (io.vld) {
        switch (io.fc3) {
            is(params.Parameters.FC3_XOR.U) {
                //XOR
                io.dst   := io.rs1 ^ io.rs2
            }
            is(params.Parameters.FC3_OR.U) {
                //OR
                io.dst   := io.rs1 | io.rs2
            }
            is(params.Parameters.FC3_AND.U) {
                //AND
                io.dst   := io.rs1 & io.rs2
            }
        }
    }
    .otherwise {
        //NOP
        io.dst   := 0.U
    }
}
