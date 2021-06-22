// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
// Instruction Fetch Unit
package fch

import chisel3._
import chisel3.util._

import params._
import lsu._

class FCH extends Module {

    /* I/O                          */
    val io  = IO(new FCH_IO)

    /* Module                       */
    //Load Request Controller
    val LdReq   = Module(new LdReq)

    /* Register                     */
    //Execution Enable on Next Pipeline Stage
    val exe     = RegInit(Bool(), false.B)

    //Instruction Register
    val IR      = RegInit(UInt((params.Parameters.ISAWidth).W), 0.U)

    /* Assign                       */
    //Access Validation
    LdReq.io.LdReq  := io.boot
    LdReq.io.LdAck  := io.iack
    LdReq.io.Stall  := io.stall
    //LdReq.io.Busy

    //Valid Followed Pipeline Stage
    exe := LdReq.io.LdValid && !io.brc && !io.stall

    //Capture Instruction Register
    when LdReq.io.LdValid) {
        IR  := io.ifch
    }

    //Output
    io.ins  := IR
    io.exe  := exe
    io.ireq := LdReq.io.Req
}
