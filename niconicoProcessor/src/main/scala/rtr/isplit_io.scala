// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package route

import chisel3._
import chisel3.util._

import params._

class ISplit_IO extends Bundle {   
    val ins     = Input( UInt((params.Parameters.ISAWidth).W))  //Instruction
    val opc     = Output(UInt((params.Parameters.OpcWidth).W))  //Opcode
    val wno     = Output(UInt((params.Parameters.LogNumReg).W)) //Register Destination No
    val rn1     = Output(UInt((params.Parameters.LogNumReg).W)) //Register Source No1
    val rn2     = Output(UInt((params.Parameters.LogNumReg).W)) //Register Source No2
    val fc3     = Output(UInt((params.Parameters.Fc3Width).W))  //Function-3 Value
    val fc7     = Output(UInt((params.Parameters.Fc7Width).W))  //Function-7 Value
}
