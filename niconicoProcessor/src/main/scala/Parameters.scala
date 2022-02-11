// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package params

import chisel3.util._

object Parameters {

    //ISA
    val ISAWidth        = 32

    //Instruction Bit-Field Width
    val ILEnc           = 2 
    val OpcWidth        = 4
    val Fc3Width        = 3
    val Fc7Width        = 7
    val ImmWidth        = 12

    //Datapath Width
    val DatWidth        = 32
    val LogDWidth       = log2Ceil(DatWidth)

    //Register File Size
    val NumReg          = 32
    val LogNumReg       = log2Ceil(NumReg)

    //Instruction Bit Field Definition
    val LSB_Opc         = 0
    val MSB_Opc         = OpcWidth - 1
    val LSB_Dst         = OpcWidth
    val MSB_Dst         = OpcWidth +              LogNumReg - 1
    val LSB_Rs1         = OpcWidth + Fc3Width
    val MSB_Rs1         = OpcWidth + Fc3Width +   LogNumReg - 1
    val LSB_Rs2         = OpcWidth + Fc3Width + 2*LogNumReg
    val MSB_Rs2         = OpcWidth + Fc3Width + 3*LogNumReg - 1
    val LSB_Fc3         = OpcWidth +              LogNumReg
    val MSB_Fc3         = OpcWidth + Fc3Width +   LogNumReg - 1
    val LSB_Fc7         = OpcWidth + Fc3Width + 3*LogNumReg
    val MSB_Fc7         = ISAWidth - 1

    //Opcode Type
    val OP_Type0_LSb    = 2
    val OP_Type0_MSb    = 3
    val OP_Type1_LSb    = 4
    val OP_Type1_MSb    = 6

    //Instr[6:4]
    val OP_LOAD         = 0x0   //Load
    val OP_STORE        = 0x2   //Store
    val OP_RandI        = 0x1   //Register and Immediate
    val OP_RandR        = 0x3   //Register and Register
    val OP_BRJMP        = 0x6   //Branch/Jump
    val OP_CSR          = 0x7   //CSR
    val OP_JALR         = 0x6   //Jump and Link Register
    val OP_JAL          = 0x6   //Jump and Link
    val OP_ECALL        = 0x7   //Environment Call

    //Func-3 Encode
    //Instr[14:12]
    //CSR
    val FC3_CSRRW       = 0x1
    val FC3_CSRRS       = 0x2
    val FC3_CSRRC       = 0x3
    val FC3_CSRRWI      = 0x5
    val FC3_CSRRSI      = 0x6
    val FC3_CSRRCI      = 0x7
    
    //Adder
    val FC3_ADD         = 0x0
    val FC3_AUIPC       = 0x1   //AUIPC
    val FC3_LUI         = 0x3   //LUI

    //Shifter
    val FC3_SL          = 0x1
    val FC3_SR          = 0x5

    //Logic
    val FC3_XOR         = 0x4
    val FC3_OR          = 0x6
    val FC3_AND         = 0x7

    //Branch
    val FC3_BEQ         = 0x0
    val FC3_BNE         = 0x1
    val FC3_BLT         = 0x4
    val FC3_BGE         = 0x5
    val FC3_BLTU        = 0x6
    val FC3_BGEU        = 0x7

    //Access Data-Type
    val FC3_BYTE        = 0x0
    val FC3_HWORD       = 0x1
    val FC3_WORD        = 0x2
    val FC3_BYTEU       = 0x4
    val FC3_HWORDU      = 0x5

    //Func-7 Encode
    //Instr[31:25]
    //Interger R-R Func7 Bit-Field
    //Adder Operation-ID
    val FC7_ADD         = 0x00
    val FC7_SUB         = 0x02

    //Shifter Operation-ID
    val FC7_LGC         = 0x00
    val FC7_ART         = 0x02

    //ALU Sub-Route ID
    val UnitID_Add      = 0
    val UnitID_Mlt      = 1
    val UnitID_Lgc      = 2
    val UnitID_Sft      = 3

    //Address Spase
    val AddrWidth       = 32

    //Initial Value for Program Counter (PC)
    val InitPC          = 0x00000000

    //Environment Call
    val ECALL_Const     = 0x000000

    //ROB
    val BUFFLENGTH      = 4
}