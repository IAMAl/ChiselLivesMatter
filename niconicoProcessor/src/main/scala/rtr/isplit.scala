// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
// Instruction Decoder
package route

import chisel3._
import chisel3.util._

import params._

class ISplit extends Module {

    /* I/O                      */
    val io = IO(new ISplit_IO)

    /* Register                 */
    //Captures Bit-Fields in Instruction
    val opc = Reg(UInt((params.Parameters.OpcWidth).W))
    val wno = Reg(UInt((params.Parameters.LogNumReg).W))
    val rn1 = Reg(UInt((params.Parameters.LogNumReg).W))
    val rn2 = Reg(UInt((params.Parameters.LogNumReg).W))
    val fc3 = Reg(UInt((params.Parameters.Fc3Width).W))
    val fc7 = Reg(UInt((params.Parameters.Fc7Width).W))

    /* Assign                   */
    //Bit-Field Extraction
    opc := io.ins(params.Parameters.MSB_Opc, params.Parameters.LSB_Opc)
    wno := io.ins(params.Parameters.MSB_Dst, params.Parameters.LSB_Dst)
    rn1 := io.ins(params.Parameters.MSB_Rs1, params.Parameters.LSB_Rs1)
    rn2 := io.ins(params.Parameters.MSB_Rs2, params.Parameters.LSB_Rs2)
    fc3 := io.ins(params.Parameters.MSB_Fc3, params.Parameters.LSB_Fc3)
    fc7 := io.ins(params.Parameters.MSB_Fc7, params.Parameters.LSB_Fc7)

    //Output
    io.opc   := opc
    io.wno   := wno
    io.rn1   := rn1
    io.rn2   := rn2
    io.fc3   := fc3
    io.fc7   := fc7
}