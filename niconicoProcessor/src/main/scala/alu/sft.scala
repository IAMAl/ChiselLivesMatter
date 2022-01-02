// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package alu

import chisel3._
import chisel3.util._

import params._
import isa._

class Sft extends Module {


    /* I/O                          */
    val io = IO(new ALU_IO)


    /* Assign                       */
    when (io.i_vld) {
        when (io.i_fc3 === (params.Parameters.FC3_SR).U) {
            when (io.i_fc7 === (params.Parameters.FC7_ART).U) {
                //Arithmetic Right Shift
                //Signed Integer makes Sign-fill
                io.o_dst    := (io.i_rs1.asSInt >>> io.i_rs2(4, 0)).asUInt
            }
            .elsewhen (io.i_fc7 === (params.Parameters.FC7_LGC).U) {
                //Logical Right Shift
                io.o_dst    := io.i_rs1 >> io.i_rs2(4, 0)
            }
            .otherwise {
                //NOP
                io.o_dst    := 0.U
            }
        }
        .elsewhen (io.i_fc3 === (params.Parameters.FC3_SL).U) {
            //Logical Left Shift
            io.o_dst    := io.i_rs1 << io.i_rs2(4, 0)
        }
        .otherwise {
            //NOP
            io.o_dst    := 0.U
        }
    }
    .otherwise {
        //NOP
        io.o_dst    := 0.U
    }
}
