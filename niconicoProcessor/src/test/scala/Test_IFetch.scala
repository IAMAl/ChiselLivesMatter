package test

import scala.io.Source

import org.scalatest._
import chiseltest._
import chisel3._

import fch._


class Test_IFetch extends FlatSpec with ChiselScalatestTester with Matchers {
	it should "Test: Instr. Fetch" in {
		test(new FCH) { dut =>
			// Program Files
			//val filename = "./src/test/scala/program.txt"
			var cycle = 0
			for (cycle<-0 until 16) {
				if (cycle == 4) {
					dut.io.i_boot.poke(		true.B )
				}
				else {
					dut.io.i_boot.poke(		false.B )
				}
				if (cycle == 10) {
					dut.io.i_stall.poke(	true.B )
				}
				else {
					dut.io.i_stall.poke(	false.B )
				}
				dut.io.i_brc.poke(		false.B )


				if (cycle == 7) {
					dut.io.i_iack.poke(		true.B )
				}
				else if ((cycle < 13) && (cycle > 9)) {
					dut.io.i_iack.poke(		true.B )
				}
				else {
					dut.io.i_iack.poke(		false.B )
				}
				if (cycle < 6) {
					dut.io.i_ifch.poke(0x00000000.U )
				}
				if (cycle == 7) {
					dut.io.i_ifch.poke(0x48888888.U )
				}
				else if (cycle == 11) {
					dut.io.i_ifch.poke(0x22222222.U )
				}
				else {
					dut.io.i_ifch.poke(0x00000000.U )
				}

				dut.clock.step()

				var req = dut.io.o_ireq.peek()
				var exe = dut.io.o_exe.peek()
				var ins = dut.io.o_ins.peek()
				println("req: "+req)
				println("exe: "+exe)
				println("ins: "+ins)
			}
		}
	}
}
