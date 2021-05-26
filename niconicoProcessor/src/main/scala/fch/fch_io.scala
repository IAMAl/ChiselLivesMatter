// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package fch

import chisel3._
import chisel3.util._

import params._

class FCH_IO extends Bundle {
    val boot    = Input( Bool())                                //System Boot Signal

    val stall   = Input( Bool())                                //Stall to Fetch
    val brc     = Input( Bool())                                //Branch Taken

    val ireq    = Output(Bool())                                //Instruction Fetch Request
    val iack    = Input( Bool())                                //Instruction Fetched Acknowledge
    val ifch    = Input( UInt((params.Parameters.ISAWidth).W))  //Instruction Fetch Port

    val exe     = Output(Bool())                                //Validation followed Pipeline Stages
    val ins     = Output(UInt((params.Parameters.ISAWidth).W))  //Instruction Output to Followed Pipe-Stage
}
