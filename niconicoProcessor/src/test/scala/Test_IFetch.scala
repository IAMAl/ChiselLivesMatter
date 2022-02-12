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
                dut.io.i_boot.poke(     false.B )
                dut.io.i_stall.poke(    false.B )
                dut.io.i_brc.poke(      false.B )
                dut.io.i_iack.poke(     false.B )
                dut.io.i_ifch.poke(0x00000000.U )

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
