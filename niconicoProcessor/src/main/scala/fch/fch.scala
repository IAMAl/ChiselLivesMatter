// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
//Instruction Fetch Unit
package fch

import chisel3._
import chisel3.util._

import params._

class FCH extends Module {

    //I/O
    val io  = IO(new FCH_IO)

    /* Register                 */
    //Valid
    val vld = RegInit(Bool(), false.B)

    //Execution Enable on Next Pipeline Stage
    val exe = RegInit(Bool(), false.B)

    //Instruction Register
    val IR  = RegInit(UInt((params.Parameters.ISAWidth).W), 0.U)

    //Assign
    //Access Validation
    when (io.boot ^ io.iack) {
        vld := true.B
    }
    .elsewhen (exe && !io.stall) {
        vld := false.B
    }
    
    //Nack Generation
    when (io.brc) {
        //Flush by Branch-Taken
        exe := false.B
    }
    .elsewhen (vld && !io.stall) {
        exe := io.iack
    }

    //Latch to Instruction Register
    when (vld && io.iack) {
        IR  := io.ifch
    }

    //Output
    io.ins  := IR
    io.exe  := exe && !io.stall
    io.ireq := vld && !io.stall
}
