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
    LdReq.io.i_LdReq    := io.i_boot
    LdReq.io.i_LdAck    := io.i_iack
    LdReq.io.i_Stall    := io.i_stall
    //LdReq.io.Busy

    //Valid Followed Pipeline Stage
    exe := LdReq.io.LdValid && !io.i_brc && !io.i_stall

    //Capture Instruction Register
    when LdReq.io.o_LdValid) {
        IR  := io.i_ifch
    }

    //Output
    io.o_ins  := IR
    io.o_exe  := exe
    io.o_ireq := LdReq.io.Req
}
