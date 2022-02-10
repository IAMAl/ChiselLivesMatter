// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package params

import chisel3.util._

object Parameters {

    //ISA
    val ISAWidth    = 32

    //Instruction Bit-Field Width
    val ILEnc       = 2 
    val OpcWidth    = 4
    val Fc3Width    = 3
    val Fc7Width    = 7
    val ImmWidth    = 12

    //Datapath Width
    val DatWidth    = 32
    val LogDWidth   = log2Ceil(DatWidth)

    //Register File Size
    val NumReg      = 32
    val LogNumReg   = log2Ceil(NumReg)

    //Instruction Bit Field Definition
    val LSB_Opc     = 0
    val MSB_Opc     = OpcWidth - 1
    val LSB_Dst     = OpcWidth
    val MSB_Dst     = OpcWidth +              LogNumReg - 1
    val LSB_Rs1     = OpcWidth + Fc3Width
    val MSB_Rs1     = OpcWidth + Fc3Width +   LogNumReg - 1
    val LSB_Rs2     = OpcWidth + Fc3Width + 2*LogNumReg
    val MSB_Rs2     = OpcWidth + Fc3Width + 3*LogNumReg - 1
    val LSB_Fc3     = OpcWidth +              LogNumReg
    val MSB_Fc3     = OpcWidth + Fc3Width +   LogNumReg - 1
    val LSB_Fc7     = OpcWidth + Fc3Width + 3*LogNumReg
    val MSB_Fc7     = ISAWidth - 1

    //Opcode Type
    //Instr[6:4]
    val OP_Type0_LSb= 2
    val OP_Type0_LSb= 3
    val OP_Type1_LSb= 4
    val OP_Type1_LSb= 6

    val OP_LOAD     = 0x0   //Load
    val OP_STORE    = 0x2   //Store
    val OP_RandI    = 0x1   //Register and Immediate
    val OP_RandR    = 0x3   //Register and Register
    val OP_BRJMP    = 0x4   //Branch/Jump
    val OP_JALR     = 0x6   //Jump and Link Register
    val OP_JAL      = 0x7   //Jump and Link

    //FC-3 Encode
    //Adder
    val FC3_ADD     = 0x0
    val FC3_AUIPC   = 0x1   //AUIPC
    val FC3_LUI     = 0x3   //LUI

    //Shifter
    val FC3_SL      = 0x1
    val FC3_SR      = 0x5

    //Logic
    val FC3_XOR     = 0x4
    val FC3_OR      = 0x6
    val FC3_AND     = 0x7

    //Branch
    val FC3_BEQ     = 0x0
    val FC3_BNE     = 0x1
    val FC3_BLT     = 0x4
    val FC3_BGE     = 0x5
    val FC3_BLTU    = 0x6
    val FC3_BGEU    = 0x7

    //Access Data-Type
    val FC3_BYTE    = 0x0
    val FC3_HWORD   = 0x1
    val FC3_WORD    = 0x2
    val FC3_BYTEU   = 0x4
    val FC3_HWORDU  = 0x5

    //Interger R-R Func7 Bit-Field
    //Adder Operation-ID
    val FC7_ADD     = 0x00
    val FC7_SUB     = 0x02

    //Shifter Operation-ID
    val FC7_LGC     = 0x00
    val FC7_ART     = 0x02

    //ALU Sub-Route ID
    val UnitID_Add  = 0
    val UnitID_Lgc  = 1
    val UnitID_Sft  = 2

    //Address Spase
    val AddrWidth   = 32

    //Initial Value for Program Counter (PC)
    val InitPC      = 0x00000000

    //Immediate Pack Bit-Position
    val IMM_LSb     = 19
    val IMM_MSb     = 31

    //ADDI/SLTI[U]/ANDI/ORI/XORI
    //I-immediate[11:0]
    val ALU_IMM_LSb = 20 - IMM_LSb
    val ALU_IMM_MSb = 31 - IMM_LSb

    //SLLI/SRLI/SRAI
    //imm[11:5], shamt[4:0]
    val SFT_IMM_LSb = 20 - IMM_LSb
    val SFT_IMM_MSb = 31 - IMM_LSb

    //LUI/AUIPC
    //U-immediate[31:12]
    val LAU_IMM_LSb = 12
    val ALU_IMM_MSb = 31

    //JAL: OFFSET[20:1]
    val JAL_IMM0_LSb= 21 - IMM_LSb
    val JAL_IMM0_MSb= 30 - IMM_LSb
    val JAL_IMM1_LSb= 20 - IMM_LSb
    val JAL_IMM1_MSb= 20 - IMM_LSb
    val JAL_IMM2_LSb= 12
    val JAL_IMM2_MSb= 19 - IMM_LSb
    val JAL_IMM3_LSb= 31 - IMM_LSb
    val JAL_IMM3_MSb= 31 - IMM_LSb

    //JALR offset[11:0]
    val JALR_FC3_Val    = 0
    val JALR_IMM0_LSb   = 20 - IMM_LSb
    val JALR_IMM0_MSb   = 31 - IMM_LSb

    //BRANCH
    //offset[12:1]
    val BR_OFFSET0_LSb  = 25
    val BR_OFFSET0_MSb  = 30
    val BR_OFFSET1_LSb  = 7
    val BR_OFFSET1_MSb  = 7
    val BR_OFFSET2_LSb  = 31
    val BR_OFFSET2_MSb  = 31

    //LOAD
    //offset[11:0]
    val LD_OFFSET_LSb   = 20
    val LD_OFFSET_MSb   = 31

    //STORE
    //offset[11:0]
    val ST_OFFSET0_LSb  = 7
    val ST_OFFSET0_MSb  = 11
    val ST_OFFSET1_LSb  = 25
    val ST_OFFSET1_MSb  = 31
}