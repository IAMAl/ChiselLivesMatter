// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package reg

import chisel3._
import chisel3.util._

import params._

class REG_IO extends Bundle {  
    
    val vld     = Input( Bool())                                    //Input Validation
    val opc     = Input( UInt((params.Parameters.OpcWidth).W))      //Opcode

    val by1     = Input( Bool())                                    //Bypassing Flag from EX-Stage-1
    val by2     = Input( Bool())                                    //Bypassing Flag from EX-Stage-2

    val re1     = Input( Bool())                                    //Register Read Enable-1
    val re2     = Input( Bool())                                    //Register Read Enable-2
    val wed     = Input( Bool())                                    //Register Write Enable

    val rn1     = Input(UInt((params.Parameters.LogNumReg).W))      //Register Read No-1
    val rn2     = Input(UInt((params.Parameters.LogNumReg).W))      //Register Read No-2
    val wno     = Input(UInt((params.Parameters.LogNumReg).W))      //Register Write No
    val fc3     = Input(UInt((params.Parameters.Fc3Width).W))       //Function-3 Port
    val fc7     = Input(UInt((params.Parameters.Fc7Width).W))       //Function-7 Port

    val rn1_o   = Output(UInt((params.Parameters.LogNumReg).W))     //Register Read No-1
    val rn2_o   = Output(UInt((params.Parameters.LogNumReg).W))     //Register Read No-2
    val fc3_o   = Output(UInt((params.Parameters.Fc3Width).W))      //Function-3 Port
    val fc7_o   = Output(UInt((params.Parameters.Fc7Width).W))      //Function-7 Port

    val wrb_r   = Input(Bool())                                     //Write-back Req.
    val wrb_d   = Input(UInt((params.Parameters.DatWidth).W))       //Write-back Data

    val imm     = Output(UInt((params.Parameters.ImmWidth).W))      //Immediate Port

    val as1     = Output(UInt((params.Parameters.DatWidth).W))      //ALU Operand-1 Port
    val as2     = Output(UInt((params.Parameters.DatWidth).W))      //ALU Operand-2 Port
    val ls1     = Output(UInt((params.Parameters.DatWidth).W))      //LSU Operand-1 Port
    val ls2     = Output(UInt((params.Parameters.DatWidth).W))      //LSU Operand-2 Port
    val bs1     = Output(UInt((params.Parameters.DatWidth).W))      //BRJ Operand-1 Port
    val bs2     = Output(UInt((params.Parameters.DatWidth).W))      //BRJ Operand-2 Port

    val opcode  = Output(UInt((params.Parameters.OpcWidth).W))      //Opcode
    val exe     = Output(Bool())                                    //Output Validation
}
