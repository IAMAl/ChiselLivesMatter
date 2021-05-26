// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package niconico

import chisel3._
import chisel3.util._

import params._

class NICO2_IO extends Bundle {  
    val boot = Input( Bool())                               //Boot Signal

    val ireq = Output(Bool())                               //Instruction Fetch Request
    val iack = Input( Bool())                               //Fetch Acknowledge
    val iadr = Output(UInt(params.Parameters.AddrWidth.W))  //Instruction Address (PC)
    val inst = Input( UInt(params.Parameters.ISAWidth.W))   //Instruction Port

    val dreq = Output(Bool())                               //Memory Access Request
    val dack = Input( Bool())                               //Memory Access Acknowledge
    val stor = Output(Bool())                               //Store Flag
    val mar  = Output(UInt(params.Parameters.AddrWidth.W))  //Memory Address Port
    val idat = Input( UInt(params.Parameters.DatWidth.W))   //Loading Port
    val odat = Output(UInt(params.Parameters.DatWidth.W))   //Storing Port
}
