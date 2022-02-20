// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package fch

import chisel3._
import chisel3.util._

import params._

class FCH_IO extends Bundle {

    val ISAWidth = params.Parameters.ISAWidth

    val i_boot      = Input( Bool())            //System Boot Signal

    val i_stall     = Input( Bool())            //Stall to Fetch
    val i_brc       = Input( Bool())            //Branch Taken

    val o_ireq      = Output(Bool())            //Instruction Fetch Request
    val i_iack      = Input( Bool())            //Instruction Fetched Acknowledge
    val i_ifch      = Input( UInt(ISAWidth.W))  //Instruction Fetch Port

    val o_exe       = Output(Bool())            //Validation followed Pipeline Stages
    val o_ins       = Output(UInt(ISAWidth.W))  //Instruction Output to Followed Pipe-Stage
}
