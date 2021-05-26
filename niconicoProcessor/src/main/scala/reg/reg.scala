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

     /* I/O                         */
    val io = IO(new REG_IO)


    /* Register                     */
    //Register File
    val RF      = RegInit(0.U.asTypeOf(Vec(NumReg, UInt((params.Parameters.DatWidth).W))))

    //Pipeline Registers
    val exe     = RegInit(Bool(), false.B)                      //Exec Validation
    val rs1     = Reg(UInt((params.Parameters.DatWidth).W))     //RegisterFile Source-1
    val rs2     = Reg(UInt((params.Parameters.DatWidth).W))     //RegisterFile SOurce-2
    val opcode  = Reg(UInt((params.Parameters.OpcWidth).W))     //Opcode

    val imm     = Reg(UInt((params.Parameters.ImmWidth+1).W))   //Immediate
    val rn1     = Reg(UInt((params.Parameters.LogNumReg).W))    //RegisterFile No. for Source-1
    val rn2     = Reg(UInt((params.Parameters.LogNumReg).W))    //RegisterFile No. for SOurce-2
    val fc3     = Reg(UInt((params.Parameters.Fc3Width).W))     //Func3
    val fc7     = Reg(UInt((params.Parameters.Fc7Width).W))     //Func7


    /* Assign                       */
    //Immediate's Pre-Formatting
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

    //Output to Follower Pipeline Stage
    exe         := io.vld   //Exec Validation
    io.exe      := exe

    opcode      := io.opc   //Send Opcode
    io.opcode   := opcode

    rn1         := io.rn1   //RegisterFile No for Source-1
    io.rn1_o    := rn1

    rn2         := io.rn2   //RegisterFile No for Source-2
    io.rn2_o    := rn2

    fc3         := io.fc3   //Func3
    io.fc3_o    := fc3

    fc7         := io.fc7   //Func7
    io.fc7_o    := fc7

    //Arithmetic Source Operands
    when ((io.opc === (params.Parameters.OP_RandI).U) || (io.opc === (params.Parameters.OP_RandR).U)) {
        //Set Source Operands for
        // Source-1&2 are from RegisterFile
        // SOurce-1 is from RegisterFile
        io.as1  := rs1
        io.as2  := rs2
    }
    .otherwise {
        //Otherwise NOT Send Value
        io.as1  := 0.U
        io.as2  := 0.U
    }

    //Load/Store Source Operands
    when ((io.opc === (params.Parameters.OP_STORE).U) || (io.opc === (params.Parameters.OP_LOAD).U)) {
        //Set Load/Store Reference Data
        io.ls1  := rs1
        io.ls2  := rs2
    }
    .otherwise {
        //Otherwise NOT Send Value
        io.ls1  := 0.U
        io.ls2  := 0.U
    }

    //Branch Source Operands
    when (io.opc === (params.Parameters.OP_BRJMP).U) {
        //Set Branch Reference Value
        io.bs1  := rs1
        io.bs2  := rs2
    }
    .otherwise {
        //Otherwise NOT Send Value
        io.bs1  := 0.U
        io.bs2  := 0.U
    }

    //Set Immediate Value
    io.imm      := imm
}
