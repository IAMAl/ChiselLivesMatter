// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package rob

import chisel3._
import chisel3.util._

import params._

class ROB_IO extends Bundle {
    val DataWidth   = params.Parameters.DataWidth
    val LogNumReg   = params.Parameters.PLogNumReg

    val i_vld       = Input( Bool())            //
    val i_set       = Input( Bool())            //Set WB Reg No
    val i_wrn       = Input( UInt(LogNumReg.W)) //WB Reg No

    val i_wrb_c     = Input( Bool())            //WB Req from CSU
    val i_wrb_a     = Input( Bool())            //WB Req from ALU
    val i_wrb_b     = Input( Bool())            //WB Req from BRU
    val i_wrb_m     = Input( Bool())            //WB Req from LSU

    val i_wrn_c     = Input( UInt(LogNumReg.W)) //WB Reg No from CSU
    val i_wrn_a     = Input( UInt(LogNumReg.W)) //WB Reg No from ALU
    val i_wrn_b     = Input( UInt(LogNumReg.W)) //WB Reg No from BRU
    val i_wrn_m     = Input( UInt(LogNumReg.W)) //WB Reg No from LSU

    val i_dat_c     = Input( UInt(DataWidth.W)) //WB Data from CSU
    val i_dat_a     = Input( UInt(DataWidth.W)) //WB Data from ALU
    val i_dat_b     = Input( UInt(DataWidth.W)) //WB Data from BRU
    val i_dat_m     = Input( UInt(DataWidth.W)) //WB Data from LSU

    val i_rs1       = Input( UInt(LogNumReg.W)) //Bypass Reg No
    val i_rs2       = Input( UInt(LogNumReg.W)) //Bypass Reg No

    val o_dat1      = Output(UInt(DataWidth.W)) //Bypass Data
    val o_dat2      = Output(UInt(DataWidth.W)) //Bypass Data

    val o_bps1      = Output(Bool())            //Bypass Enable
    val o_bps2      = Output(Bool())            //Bypass Enable

    val o_full      = Output(Bool())
    val o_wrn       = Output(UInt(LogNumReg.W)) //WB Reg No
    val o_dat       = Output(UInt(DataWidth.W)) //WB Data
    val o_wrb       = Output(Bool())            //WB Req
}
