// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package niconico

import chisel3._
import chisel3.util._

import params._

class NICO2_IO extends Bundle {
    
    val ISAWidth    = params.Parameters.ISAWidth
    val DataWidth   = params.Parameters.DataWidth
    val AddrWidth   = params.Parameters.AddrWidth

    val boot = Input( Bool())             //Boot Signal

    val ireq = Output(Bool())             //Instruction Fetch Request
    val iack = Input( Bool())             //Fetch Acknowledge
    val iadr = Output(UInt(AddrWidth.W))  //Instruction Address (PC)
    val inst = Input( UInt(ISAWidth.W))   //Instruction Port

    val dreq = Output(Bool())             //Memory Access Request
    val dack = Input( Bool())             //Memory Access Acknowledge
    val stor = Output(Bool())             //Store Flag
    val mar  = Output(UInt(AddrWidth.W))  //Memory Address Port
    val idat = Input( UInt(DataWidth.W))  //Loading Port
    val odat = Output(UInt(DataWidth.W))  //Storing Port
}
