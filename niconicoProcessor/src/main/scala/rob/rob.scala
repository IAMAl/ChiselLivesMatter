/* ElectronNest:    Graph Processor                         */
/* Module Class:    Utility                                 */
/* Buffer                                                   */
/* Right Holder:    Shigeyuki TAKANO                        */
package rob

import chisel3._
import chisel3.util._

import params._


class Entry (
        DatWidth:   Int,
        LogNumReg:  Int
    ) extends Bundle {
    val V       = RegInit(Bool(), false.B)
    val W       = RegInit(Bool(), false.B)
    val WBRN    = Reg(UInt(LogNumReg.W))
    val Data    = Reg(UInt(DatWidth.W))
}


class BuffCtrl (
        BUFFLENGTH: Int
    ) extends Module {

    val BUFFWIDTH = log2Ceil(BUFFLENGTH)
    val WIDTHAD = BUFFWIDTH + 1


    /* I/F                                                  */
    val io = IO(new Bundle {
        val I_We        = Input(Bool())                     // Write Request
        val I_Re        = Input(Bool())                     // Read Request
        val O_WP        = Output(UInt(BUFFWIDTH.W))         // Write-Pointer
        val O_RP        = Output(UInt(BUFFWIDTH.W))         // Read-Pointer
        val O_Full      = Output(Bool())                    // Full Flag
        val O_Empty     = Output(Bool())                    // Empty Flag
        val O_Thrshld   = Output(Bool())                    // Empty Flag
    })


    /* Register                                             */
    val R_WCNT      = RegInit(UInt(WIDTHAD.W), 0.U)         // Write Counter
    val R_RCNT      = RegInit(UInt(WIDTHAD.W), 0.U)         // Read Counter


    /* Wire                                                 */
    val W_CNT       = Wire(UInt(WIDTHAD.W))                 // Num Of Used Buffer


    /* Assign                                               */
    //// Buffer State
    W_CNT           := R_WCNT - R_RCNT

    // Check Full-State
    io.O_Full       := W_CNT(WIDTHAD-1).asBool()

    // Check Empty-State
    io.O_Empty      := (W_CNT === 0.U)

    //// Pointers
    // Write-Pointer
    io.O_WP         := R_WCNT(BUFFWIDTH-1, 0)

    // Read-Pointer
    io.O_RP         := R_RCNT(BUFFWIDTH-1, 0)


    //// Update Counters
    when (io.I_We && !W_CNT(WIDTHAD-1).asBool()) {
        when (R_WCNT === BUFFLENGTH.U) {
            R_WCNT  := 0.U
        }
        .otherwise {
            R_WCNT  := R_WCNT + 1.U
        }
    }
    .elsewhen (io.I_Re && (W_CNT =/= 0.U)) {
        when (R_RCNT === BUFFLENGTH.U) {
            R_RCNT  := 0.U
        }
        .otherwise {
            R_RCNT  := R_RCNT + 1.U
        }
    }
}


class ROB extends Module {

    val DatWidth    = params.Parameters.DatWidth
    val LogNumReg   = params.Parameters.LogNumReg
    val BUFFLENGTH  = params.Parameters.BUFFLENGTH
    val BUFFWIDTH   = log2Ceil(BUFFLENGTH)


    /* I/O                              */
    val io          = IO(new ROB_IO)


    /* Module                                       */
    val BFCTRL      = Module(new BuffCtrl(BUFFLENGTH))

    // Power of 2 Depth Circular Buffer Memory
    val BUFF        = Vec(BUFFLENGTH, new Entry(DatWidth, LogNumReg))

    val PostDat     = new Entry(DatWidth, LogNumReg)// Output from Memory

    val Valid       = RegInit(Bool(), false.B)      // Capture Valid
    val Full        = RegInit(Bool(), false.B)      // Capture Full


    /* Wire                                         */
    val WPtr        = Wire(UInt(BUFFWIDTH.W))       // Write Pointer
    val RPtr        = Wire(UInt(BUFFWIDTH.W))       // Read Pointer

    val We          = Wire(Bool())
    val Re          = Wire(Bool())


    /* Assign                                       */
    //Write-Enable
    We              := io.i_set

    //Read-Enable
    Re              := !BFCTRL.io.O_Empty && BUFF(RPtr).V && BUFF(RPtr).W

    //Controll
    BFCTRL.io.I_We  := We
    BFCTRL.io.I_Re  := Re

    //Write/Read Pointers
    WPtr            := BFCTRL.io.O_WP
    RPtr            := BFCTRL.io.O_RP

    //Check Full-State
    Full            := BFCTRL.io.O_Full
    io.o_full       := Full

    //Write/Read
    when (We) {
        BUFF(WPtr).V    := true.B
        BUFF(WPtr).WBRN := io.i_wrn
    }
    when (Re) {
        BUFF(RPtr).V    := false.B
        BUFF(RPtr).W    := false.B
    }
    for (index<-0 until BUFFLENGTH) {
        //Write Back
        when (io.i_wrb_c && (io.i_wrn_c === BUFF(index).WBRN)) {
            BUFF(index).Data:= io.i_wrb_c
        }
        when (io.i_wrb_a && (io.i_wrn_a === BUFF(index).WBRN)) {
            BUFF(index).Data:= io.i_wrb_a
        }
        when (io.i_wrb_b && (io.i_wrn_b === BUFF(index).WBRN)) {
            BUFF(index).Data:= io.i_wrb_b
        }
        when (io.i_wrb_m && (io.i_wrn_m === BUFF(index).WBRN)) {
            BUFF(index).Data:= io.i_wrb_m
        }

        //Bypassing
        when (io.i_vld && BUFF(index).V && BUFF(index).W) {
            when (io.i_rs1 === BUFF(index).WBRN) {
                io.o_dat1 := BUFF(index).Data
                io.o_bps1 := true.B
            }
            .otherwise {
                io.o_dat1 := 0.U
                io.o_bps1 := false.B
            }
            when (io.i_rs2 === BUFF(index).WBRN) {
                io.o_dat2 := BUFF(index).Data
                io.o_bps2 := true.B
            }
            .otherwise {
                io.o_dat2 := 0.U
                io.o_bps2 := false.B
            }
        }
    }

    //Read Data
    PostDat         := BUFF(RPtr)
    io.o_dat        := PostDat.Data
    io.o_wrn        := PostDat.WBRN
    io.o_wrb        := PostDat.V && PostDat.W
}
