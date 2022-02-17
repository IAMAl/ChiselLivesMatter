package test

import scala.io.Source

import org.scalatest._
import chiseltest._
import chisel3._

import lsu._


class Test_LdSt extends FlatSpec with ChiselScalatestTester with Matchers {
    it should "Test: Load/Store Unit" in {
        test(new LSU) { dut =>
            // Program Files
            //val filename = "./src/test/scala/program.txt"
            var cycle = 0
            for (cycle<-0 until 16) {
                if ((cycle > 9) && (cycle < 13)) {
                    dut.io.i_vld.poke(     true.B )
                }
                else if (cycle > 7) {
                    dut.io.i_vld.poke(     false.B )
                }
                else if (cycle > 3) {
                    dut.io.i_vld.poke(     true.B )
                }
                else {
                    dut.io.i_vld.poke(     false.B )
                }
                
                
                dut.io.i_rs1.poke( cycle.U )
                dut.io.i_rs2.poke( cycle.U )

                if ((cycle > 9) && (cycle < 13)) {
                    dut.io.i_opc.poke(     2.U )
                }
                else {
                    dut.io.i_opc.poke(     0.U )
                }

                if ((cycle > 3) && (cycle < 7)) {
                    dut.io.i_imm.poke(     cycle.U )
                }
                else if ((cycle > 9) && (cycle < 13)) {
                    dut.io.i_imm.poke(     cycle.U )
                }
                else {
                    dut.io.i_wrn.poke(     0.U )
                }

                if (cycle == 5) {
                    dut.io.i_fc3.poke(     0.U )
                    dut.io.i_dack.poke(    true.B )
                    dut.io.i_idat.poke(    0x44444444.U )
                }
                else if (cycle == 6) {
                    dut.io.i_fc3.poke(     1.U )
                    dut.io.i_dack.poke(    true.B )
                    dut.io.i_idat.poke(    0x22222222.U )
                }
                else if (cycle == 7) {
                    dut.io.i_fc3.poke(     2.U )
                    dut.io.i_dack.poke(    true.B )
                    dut.io.i_idat.poke(    0x11111111.U )
                }
                else {
                    dut.io.i_fc3.poke(     0.U )
                    dut.io.i_dack.poke(    false.B )
                    dut.io.i_idat.poke(    0.U )                    
                }

                dut.clock.step()

                var wrn = dut.io.o_wrn.peek()
                var wrb = dut.io.o_wrb.peek()
                var req = dut.io.o_dreq.peek()
                var str = dut.io.o_stor.peek()
                var mar = dut.io.o_dmar.peek()
                var dat = dut.io.o_odat.peek()
                var dst = dut.io.o_dst.peek()
                var sl0 = dut.io.o_csel(0).peek()
                var sl1 = dut.io.o_csel(1).peek()
                var sl2 = dut.io.o_csel(2).peek()
                var sl3 = dut.io.o_csel(3).peek()
            }
        }
    }
}