// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package rru

import chisel3._
import chisel3.util._

import params.Parameters._

class RRU_IO extends Bundle {

    val ISAWidth    = params.Parameters.ISAWidth
    val OpcWidth    = params.Parameters.OpcWidth
    val Fc3Width    = params.Parameters.Fc3Width
    val Fc7Width    = params.Parameters.Fc7Width
    val PLogNumReg  = params.Parameters.PLogNumReg

    val i_vld = Input( Bool())              //Input Validation
    val i_ins = Input( UInt(ISAWidth.W))    //Fetched Instruction Port

    val o_opc = Output(UInt(OpcWidth.W))    //Opcode
    val o_rn1 = Output(UInt(PLogNumReg.W))  //Read Register No-1
    val o_rn2 = Output(UInt(PLogNumReg.W))  //Read Register No-2
    val o_wno = Output(UInt(PLogNumReg.W))  //Write Register No
    val o_fc3 = Output(UInt(Fc3Width.W))    //Function-3 Port
    val o_fc7 = Output(UInt(Fc7Width.W))    //Function-7 Port

    val o_re1 = Output(Bool())              //Register Read Enable-1
    val o_re2 = Output(Bool())              //Register Read Enable-2

    val i_wbn = Input( UInt(PLogNumReg.W))  //Write-Back No.
    val i_wrb = Input( Bool())              //Write-Back Done
    val o_wrb = Output(Bool())              //Write-Back Request
    val o_exe = Output(Bool())              //Execution Timing Flag
    val o_hzd = Output(Bool())              //Control Hazard
}
