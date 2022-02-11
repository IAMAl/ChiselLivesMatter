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

            for (cycle<-0 until 256) {
                dut.io.i_boot.poke(     false.B )
                dut.io.i_stall.poke(    false.B )
                dut.io.i_brc.poke(      false.B )
                dut.io.o_ireq.poke(     false.B )
                dut.io.i_iack.poke(     false.B )
                dut.io.i_ifch.poke(0x00000000.U )

                dut.clock.step()

                dut.io.o_exe.poke(      false.B )
                dut.io.o_ins.poke( 0x00000000.U )
            }
        }
    }
}
