// GPL-3 License (see LICENSE file)
// https://github.com/IAMAl/niconicoProcessor
package csr

import chisel3._
import chisel3.util._

import params._

class Csr extends Module {


    /* I/O                              */
    val io  = IO(new CSR_IO)

    /* Register                         */
    //Register File
    val CSR     = RegInit(0.U.asTypeOf(Vec(2, Vec(NumReg, Vec(NumReg, UInt((params.Parameters.DatWidth).W)))))


     /* Wire                             */
    val csr     = Wire(UInt((params.Parameters.DatWidth).W))    //CSR-Output
    val imm_z   = Wire(UInt(5.W))                               //Immediate
    val idx     = Wire(UInt(12).W))                             //Index


    /* Assign                           */
    imm_z   := io.i_rn1.asUInt(DatWidth.W)
    idx     := io.i_imm(11, 0)

    csr     := CSR(idx(11, 10))(idx(9, 5))(idx(4, 0))

    when (io.i_vld) {
        switch (io.i_fc3) {
            is (FC3_CSRRW) {
                CSR(idx(11, 10))(idx(9, 5))(idx(4, 0))  := io.i_rs1
            }
            is (FC3_CSRRWI) {
                CSR(idx(11, 10))(idx(9, 5))(idx(4, 0))  := imm_z
            }
            is (FC3_CSRRS) {
                CSR(idx(11, 10))(idx(9, 5))(idx(4, 0))  := csr | io.i_rs1
            }
            is (FC3_CSRRW) {
                CSR(idx(11, 10))(idx(9, 5))(idx(4, 0))  := csr | imm_z
            }
            is (FC3_CSRRW) {
                CSR(idx(11, 10))(idx(9, 5))(idx(4, 0))  := csr & ~io.i_rs1
            }
            is (FC3_CSRRW) {
                CSR(idx(11, 10))(idx(9, 5))(idx(4, 0))  := csr & ~imm_z
            }
        }
    }

    io.o_dst    := csr

    io.o_wrb    := io.i_vld

    io.i_wrn    := io.i_wrn

}