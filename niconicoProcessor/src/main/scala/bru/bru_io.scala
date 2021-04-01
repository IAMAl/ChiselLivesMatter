// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package bru

import chisel3._
import chisel3.util._

import params._

class BRU_IO extends Bundle { 
    
    val vld = Input( Bool())                                    //BRJ Operation Validation
    val rs1 = Input( UInt((params.Parameters.DatWidth).W))      //Source Operand-1 Port
    val rs2 = Input( UInt((params.Parameters.DatWidth).W))      //Source Operand-2 Port
    val rn1 = Input( UInt((params.Parameters.LogNumReg).W))     //Register Read No-1
    val rn2 = Input( UInt((params.Parameters.LogNumReg).W))     //Register Read No-2
    val fc3 = Input( UInt((params.Parameters.Fc3Width).W))      //Function-3 Port
    val fc7 = Input( UInt((params.Parameters.Fc7Width).W))      //Function-7 Port

    val pc  = Output(UInt((params.Parameters.AddrWidth).W))     //Program Counter (PC)
    val jal = Input( UInt(2.W))                                 //Jump and Link Flag
    val imm = Input( UInt((params.Parameters.ImmWidth).W))      //Immediate Port
    val brc = Output(Bool())                                    //Branch Taken
    val wrb = Output(Bool())                                    //Write-back
    val dst = Output(UInt((params.Parameters.AddrWidth).W))     //Link Address
}
