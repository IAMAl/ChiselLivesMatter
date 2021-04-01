// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
//Register File Unit
package reg

import chisel3._
import chisel3.util._

import params._

class REG extends Module {

    val DataWidth   = params.Parameters.DatWidth
    val NumReg      = params.Parameters.NumReg

    //I/O
    val io = IO(new REG_IO)

    //Register File
    val RF      = RegInit(0.U.asTypeOf(Vec(NumReg, UInt((params.Parameters.DatWidth).W))))

    //Pipeline Registers
    val exe     = RegInit(Bool(), false.B)
    val rs1     = Reg(UInt((params.Parameters.DatWidth).W))
    val rs2     = Reg(UInt((params.Parameters.DatWidth).W))
    val opcode  = Reg(UInt((params.Parameters.OpcWidth).W))

    val imm     = Reg(UInt((params.Parameters.ImmWidth+1).W))
    val rn1     = Reg(UInt((params.Parameters.LogNumReg).W))
    val rn2     = Reg(UInt((params.Parameters.LogNumReg).W))
    val fc3     = Reg(UInt((params.Parameters.Fc3Width).W))
    val fc7     = Reg(UInt((params.Parameters.Fc7Width).W))


    //Assign
    //Immediate Composition
    when (io.opc === (params.Parameters.OP_STORE).U) {      //Store
        imm := Cat(io.fc7, io.wno)
    }
    .elsewhen (io.opc === (params.Parameters.OP_LOAD).U) {  //Load
        imm := Cat(io.fc7, io.rn2)
    }
    .elsewhen (io.opc === (params.Parameters.OP_BRJMP).U) { //Branch/Jump
        imm := Cat(io.fc7(6), io.wno(0), io.fc7(5,0), io.wno(4, 1)) << 1.U
    }    

    //Write Data
    when (io.wed && io.wrb_r) {
        RF(io.wno)  := io.wrb_d
    }

    //Read Source-1
    when (io.vld) {
        when (io.re1 && io.by1) {
            //Bypass
            rs1 := io.wrb_d
        }
        .elsewhen (io.re1) {
            //Read Register File
            rs1 := RF(io.rn1)
        }
    }

    //Read Source-2
    when (io.vld) {
        when (io.re2 && io.by2) {
            //Bypass
            rs2 := io.wrb_d
        }
        .elsewhen (io.re2) {
            //Read Register File
            rs2 := RF(io.rn2)
        }
        .otherwise {
            //Output Immediate
            rs2 := io.fc7
        }
    }

    //Output
    exe         := io.vld
    io.exe      := exe

    opcode      := io.opc
    io.opcode   := opcode

    rn1         := io.rn1
    io.rn1_o    := rn1

    rn2         := io.rn2
    io.rn2_o    := rn2

    fc3         := io.fc3
    io.fc3_o    := fc3

    fc7         := io.fc7
    io.fc7_o    := fc7
    
    //Arithmetic Source Operands
    when ((io.opc === (params.Parameters.OP_RandI).U) || (io.opc === (params.Parameters.OP_RandR).U)) {
        io.as1  := rs1
        io.as2  := rs2
    }
    .otherwise {
        io.as1  := 0.U
        io.as2  := 0.U
    }

    //Load/Store Source Operands
    when ((io.opc === (params.Parameters.OP_STORE).U) || (io.opc === (params.Parameters.OP_LOAD).U)) {
        io.ls1  := rs1
        io.ls2  := rs2
    }
    .otherwise {
        io.ls1  := 0.U
        io.ls2  := 0.U
    }

    //Branch Source Operands
    when (io.opc === (params.Parameters.OP_BRJMP).U) {
        io.bs1  := rs1
        io.bs2  := rs2
    }
    .otherwise {
        io.bs1  := 0.U
        io.bs2  := 0.U
    }

    //Output Immediate
    io.imm      := imm
}
