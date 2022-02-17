package test

import scala.io.Source

import org.scalatest._
import chiseltest._
import chisel3._

import bru._


class Test_BRU extends FlatSpec with ChiselScalatestTester with Matchers {
    it should "Test: Instr. Fetch" in {
        test(new BRU) { dut =>
            // Program Files
            //val filename = "./src/test/scala/program.txt"
            var cycle = 0
            for (cycle<-0 until 16) {
                if (cycle > 3) {
                    dut.io.i_vld.poke(      true.B )
                }
                else {
                    dut.io.i_vld.poke(      false.B )
                }
                dut.io.i_rs1.poke( cycle.U )
                dut.io.i_rs2.poke( (16-cycle).U )

                if (cycle == 8) {
                    dut.io.i_jal.poke( 0x06.U )
                }
                else {
                    dut.io.i_jal.poke( 0x00.U )
                }
                dut.io.i_imm.poke( 0x44444444.U )
                dut.io.i_fc3.poke( 0x00.U )
                dut.io.i_wrn.poke( cycle.U )

                dut.clock.step()
                
                var wrn = dut.io.o_wrn.peek()
                var brc = dut.io.o_brc.peek()
                var wrb = dut.io.o_wrb.peek()
                var dst = dut.io.o_dst.peek()
                var pc  = dut.io.o_pc.peek()
            }
        }
    }
}