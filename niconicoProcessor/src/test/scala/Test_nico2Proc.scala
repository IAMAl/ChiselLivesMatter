package test

import scala.io.Source

import org.scalatest._
import chiseltest._
import chisel3._

import niconico._


class Test_niconico extends FlatSpec with ChiselScalatestTester with Matchers {
    it should "Test: niconicoProc" in {
        test(new NicoNico) { dut =>
            // Program Files
            var program = new Array[String](500)
            val instr_filename = "rv32ui-p-add.hex"
            program = Source.fromFile("./src/test/scala/test_codes/" + instr_filename).getLines.toArray

            var cycle = 0
            var ireq = false
            var iadr = 0
            var inst = 0
            var dreq = false
            var dadr = 0
            var data = 0
            var stor = false
            for (cycle<-0 until 16) {

                //Boot: Kicking Start
                if (cycle == 8) {
                    dut.io.boot.poke(   true.B      )
                }
                else {
                    dut.io.boot.poke(   false.B     )
                }

                //Instr Fetch
                if (ireq) {
                    inst = Integer.parseInt(program(iadr), 16)
                    dut.io.inst.poke(   inst.U    )
                    dut.io.iack.poke(   true.B      )
                }
                else {
                    dut.io.iack.poke(   false.B     )
                }

                //Store/Load
                if (dreq & stor) {
                    dut.io.odat.peek()
                    dut.io.dack.poke(   true.B      )
                }
                else if (dreq) {
                    data = Integer.parseInt(program(dadr), 16)
                    dut.io.idat.poke(   true.B      )
                    dut.io.dack.poke(   true.B      )
                }
                else {
                    dut.io.dack.poke(   false.B     )
                }

                dut.clock.step()

                ireq = dut.io.ireq.peek().litToBoolean
                iadr = dut.io.iadr.peek().litValue().toInt

                dreq = dut.io.dreq.peek().litToBoolean
                stor = dut.io.stor.peek().litToBoolean
                dadr = dut.io.mar.peek().litValue().toInt
            }
        }
    }
}