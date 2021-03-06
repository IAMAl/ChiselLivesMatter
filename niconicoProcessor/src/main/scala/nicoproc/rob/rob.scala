/* ElectronNest:		Graph Processor												 */
/* Module Class:		Utility																 */
/* Buffer																									 */
/* Right Holder:		Shigeyuki TAKANO												*/
package rob

import chisel3._
import chisel3.util._

import params._


class Entry extends Bundle {
	val DataWidth	= params.Parameters.DataWidth
	val LogNumReg	= params.Parameters.LogNumReg+1
		
	val V			= Bool()
	val W			= Bool()
	val WBRN		= UInt(LogNumReg.W)
	val Data		= UInt(DataWidth.W)
}


class BuffCtrl (
	BUFFLENGTH: Int
) extends Module {

	val BUFFWIDTH	= log2Ceil(BUFFLENGTH)
	val WIDTHAD		= BUFFWIDTH + 1

	/* I/F												*/
	val io = IO(new Bundle {
			val I_We		= Input(Bool())				// Write Request
			val I_Re		= Input(Bool())				// Read Request
			val O_WP		= Output(UInt(BUFFWIDTH.W))	// Write-Pointer
			val O_RP		= Output(UInt(BUFFWIDTH.W))	// Read-Pointer
			val O_Full		= Output(Bool())			// Full Flag
			val O_Empty		= Output(Bool())			// Empty Flag
		})


	/* Register																						 */
	val R_WCNT		= RegInit(UInt(WIDTHAD.W), 0.U)		// Write Counter
	val R_RCNT		= RegInit(UInt(WIDTHAD.W), 0.U)		// Read Counter


	/* Wire												*/
	val W_CNT		= Wire(UInt(WIDTHAD.W))				// Num Of Used Buffer


	/* Assign											*/
	//// Buffer State
	W_CNT			:= R_WCNT - R_RCNT

	// Check Full-State
	io.O_Full		:= W_CNT(WIDTHAD-1).asBool()

	// Check Empty-State
	io.O_Empty		:= (W_CNT === 0.U)

	//// Pointers
	// Write-Pointer
	io.O_WP			:= R_WCNT(BUFFWIDTH-1, 0)

	// Read-Pointer
	io.O_RP			:= R_RCNT(BUFFWIDTH-1, 0)


	//// Update Counters
	when (io.I_We && !W_CNT(WIDTHAD-1).asBool()) {
		when (R_WCNT === BUFFLENGTH.U) {
			R_WCNT	:= 0.U
		}
		.otherwise {
			R_WCNT	:= R_WCNT + 1.U
		}
	}
	.elsewhen (io.I_Re && (W_CNT =/= 0.U)) {
		when (R_RCNT === BUFFLENGTH.U) {
			R_RCNT	:= 0.U
		}
		.otherwise {
			R_RCNT	:= R_RCNT + 1.U
		}
	}
}


class ROB extends Module {

	val DataWidth	= params.Parameters.DataWidth
	val LogNumReg	= params.Parameters.LogNumReg+1
	val BUFFLENGTH	= params.Parameters.BUFFLENGTH
	val BUFFWIDTH	= log2Ceil(BUFFLENGTH)


	/* I/O															*/
	val io			= IO(new ROB_IO)


	/* Module														*/
	// Power of 2 Depth Circular Buffer Controller
	val BFCTRL		= Module(new BuffCtrl(BUFFLENGTH))

	// Power of 2 Depth Circular Buffer Memory
	val BUFF		= RegInit(0.U.asTypeOf((Vec(BUFFLENGTH, new Entry))))

	val PostDat		= Reg(new Entry)				// Output from Memory

	val Valid		= RegInit(Bool(), false.B)		// Capture Valid
	val Full		= RegInit(Bool(), false.B)		// Capture Full


	/* Wire															*/
	val WPtr		= Wire(UInt((BUFFWIDTH+1).W))	// Write Pointer
	val RPtr		= Wire(UInt((BUFFWIDTH+1).W))	// Read Pointer

	val We			= Wire(Bool())
	val Re			= Wire(Vec(BUFFLENGTH, Bool()))


	/* Assign														*/
	//Write-Enable
	We				:= io.i_set

	//Controll
	BFCTRL.io.I_We	:= We
	BFCTRL.io.I_Re	:= Re.asUInt.orR

	//Write/Read Pointers
	WPtr			:= BFCTRL.io.O_WP
	RPtr			:= BFCTRL.io.O_RP

	//Check Full-State
	Full			:= BFCTRL.io.O_Full
	io.o_full		:= Full

	//Write/Read
	Re	:= DontCare
	for (idx<-0 until BUFFLENGTH by 1) {
		when (We && (idx.U === WPtr) && (RPtr =/= WPtr)) {
			BUFF(idx).V		:= true.B
			BUFF(idx).WBRN	:= io.i_wrn
		}

		//Read-Enable
		when (idx.U === RPtr) {
			Re(idx) := BUFF(idx).V && BUFF(idx).W

			when (Re(idx)) {
				BUFF(idx).V := false.B
				BUFF(idx).W := false.B
			}

			PostDat.V		:= BUFF(idx).V
			PostDat.W		:= BUFF(idx).W
			PostDat.WBRN	:= BUFF(idx).WBRN
			PostDat.Data	:= BUFF(idx).Data
		}
	}

	for (index<-0 until BUFFLENGTH by 1) {
		//Write Back
		when (io.i_wrb_c && BUFF(index).V && (io.i_wrn_c === BUFF(index).WBRN)) {
			BUFF(index).Data	:= io.i_wrb_c
		}
		.elsewhen (io.i_wrb_a && BUFF(index).V && (io.i_wrn_a === BUFF(index).WBRN)) {
			BUFF(index).Data	:= io.i_wrb_a
		}
		.elsewhen (io.i_wrb_b && BUFF(index).V && (io.i_wrn_b === BUFF(index).WBRN)) {
			BUFF(index).Data	:= io.i_wrb_b
		}
		.elsewhen (io.i_wrb_m && BUFF(index).V && (io.i_wrn_m === BUFF(index).WBRN)) {
			BUFF(index).Data	:= io.i_wrb_m
		}

		//Bypassing
		io.o_dat1 := DontCare
		io.o_dat2 := DontCare
		io.o_bps1 := DontCare
		io.o_bps2 := DontCare
		when (BUFF(index).V && BUFF(index).W) {
			when (io.i_rs1 === BUFF(index).WBRN) {
				io.o_dat1 := BUFF(index).Data
				io.o_bps1 := true.B
			}
			when (io.i_rs2 === BUFF(index).WBRN) {
				io.o_dat2 := BUFF(index).Data
				io.o_bps2 := true.B
			}
		}
	}

	//Read Data
	io.o_dat	:= PostDat.Data
	io.o_wrn	:= PostDat.WBRN
	io.o_wrb	:= PostDat.V && PostDat.W
}
