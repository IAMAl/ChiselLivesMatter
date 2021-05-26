// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package alu

import chisel3._
import chisel3.util._

import params._
import isa._

class Sft extends Module {

    //I/O
    val io = IO(new ALU_IO)

    //Assign
    when (io.vld) {
        when (io.fc3 === (params.Parameters.FC3_SR).U) {
            when (io.fc7 === (params.Parameters.FC7_ART).U) {
                //Arithmetic Right Shift
                //Signed Integer makes Sign-fill
                io.dst   := (io.rs1.asSInt >> io.rs2(4, 0)).asUInt
            }
            .elsewhen (io.fc7 === (params.Parameters.FC7_LGC).U) {
                //Logical Right Shift
                io.dst   := io.rs1 >> io.rs2(4, 0)
            }
            .otherwise {
                //NOP
                io.dst   := 0.U
            }
        }
        .elsewhen (io.fc3 === (params.Parameters.FC3_SL).U) {
            //Logical Left Shift
            io.dst  := io.rs1 << io.rs2(4, 0)
        }
        .otherwise {
            //NOP
            io.dst  := 0.U
        }
    }
    .otherwise {
        //NOP
        io.dst   := 0.U
    }
}
