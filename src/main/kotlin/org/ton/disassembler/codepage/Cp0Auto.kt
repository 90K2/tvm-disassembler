package org.ton.disassembler.codepage

import org.ton.bigint.BigInt
import org.ton.bitstring.BitString
import org.ton.cell.Cell
import org.ton.cell.CellSlice
import org.ton.disassembler.TvmDisassembler
import java.math.BigInteger


class Cp0Auto: Codepage() {
    
    private fun fetchSubslice(slice: CellSlice, bits: BigInt, refs: BigInt? = null): CellSlice {
        val newBits = BitString()
        for (i in 0 until bits.toInt()) {
            newBits.plus(slice.loadBit());
        }
        val newRefs = mutableListOf<Cell>()
        for (i in 0 until (refs?.toInt() ?: 0)) {
            newRefs.add(slice.loadRef())
        }
        return CellSlice.of(newBits, newRefs)
    }

    private fun args(slice: CellSlice): List<BigInteger> {
        val args = slice.loadUInt(12)
        val i = args shr 8.and(0xf)
        val j = args shr 4.and(0xf)
        val k = args.and(BigInt(0xf))
        return listOf(i, j, k)
    }
    
    private fun typeRef(slice: CellSlice): Pair<String, Boolean> {
        val sls = slice.loadBit()
        val sign = slice.loadBit()
        val ref = slice.loadBit()
        val type = if (sls && !sign) "I" else if (sls && sign) "U" else ""
        return Pair(type, ref)
    }

    init {
        this.insertHex("0", 4, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val n = slice.loadUInt(4)
                if (n == BigInt(0)) return "NOP"
                return "s0 s$n XCHG"
            }
        ))
        this.insertHex("1", 4, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val n = slice.loadUInt(4)
                return "s1 s$n XCHG"
            })
        )
        this.insertHex("2", 4, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val n = slice.loadUInt(4)
                return "s$n PUSH"
            })
        )
        this.insertHex("3", 4, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val value = slice.loadUInt(4)
                return "s${value} POP"
            })
        )
        this.insertHex("4", 4, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val i = slice.loadUInt(4)
                val j = slice.loadUInt(4)
                val k = slice.loadUInt(4)
                return "s$i s$j s$k XCHG3"
            })
        )
        this.insertHex("50", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val i = slice.loadUInt(4)
                val j = slice.loadUInt(4)
                return "s$i s$j XCHG2"
            })
        )
        this.insertHex("51", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val i = slice.loadUInt(4)
                val j = slice.loadUInt(4)
                return "s$i s$j XCPU"
            })
        )
        this.insertHex("52", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val i = slice.loadUInt(4)
                val j = slice.loadUInt(4)
                return "s$i s${j - BigInt(1)} PUXC"
            })
        )
        this.insertHex("53", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val args = slice.loadUInt(8)
                val first = (args shr 4).and(BigInt(0xf))
                val second = args.and(BigInt(0xf))
                return "s$first s$second PUSH2"
            })
        )
        this.insertHex("540", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val (i, j, k) = args(slice)
                return "s$i s$j s$k XCHG3"
            })
        )
        this.insertHex("541", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val (i, j, k) = args(slice)
                return "$i $j $k XC2PU"
            })
        )
        this.insertHex("542", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val (i, j, k) = args(slice)
                return "$i $j ${k - BigInt(1)} XCPUXC"
            })
        )
        this.insertHex("543", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val (i, j, k) = args(slice)

                return "$i $j $k XCPU2"
            })
        )
        this.insertHex("544", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val (i, j, k) = args(slice)

                return "$i ${j - BigInt(1)} ${k - BigInt(1)} PUXC2"
            })
        )
        this.insertHex("545", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val (i, j, k) = args(slice)

                return "$i ${j - BigInt(1)} ${k - BigInt(1)} PUXCPU"
            })
        )
        this.insertHex("546", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val (i, j, k) = args(slice)

                return "$i ${j - BigInt(1)} ${k - BigInt(2)} PU2XC"
            })
        )
        this.insertHex("547", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val (i, j, k) = args(slice)

                return "$i $j $k PUSH3"
            })
        )
// 5537792 (DUMMY)
        this.insertHex("55", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val args = slice.loadUInt(8)
                val i = (args shr 4).and(BigInt(0xf))
                val j = args.and(BigInt(0xf))
                return "${i + BigInt(1)} ${j + BigInt(1)} BLKSWAP"
            })
        )
        this.insertHex("56", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val args = slice.loadUInt(8)
                return "s$args PUSH"
            })
        )
        this.insertHex("57", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val args = slice.loadUInt(8)
                return "s$args POP"
            })
        )
        this.insertHex("58", 8, Op.OpCodeStr("ROT"))
        this.insertHex("59", 8, Op.OpCodeStr("ROTREV"))
        this.insertHex("5a", 8, Op.OpCodeStr("2SWAP"))
        this.insertHex("5b", 8, Op.OpCodeStr("2DROP"))
        this.insertHex("5c", 8, Op.OpCodeStr("2DUP"))
        this.insertHex("5d", 8, Op.OpCodeStr("2OVER"))
        this.insertHex("5e", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val args = slice.loadUInt(8)
                val i = (args shr 4).and(BigInt(0xf))
                val j = args.and(BigInt(0xf))
                return "${i + BigInt(2)} $j REVERSE"
            })
        )
        this.insertHex("5f", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val i = slice.loadUInt(4)
                val j = slice.loadUInt(4)
                if (i == BigInt(0)) {
                    return "$j BLKDROP"
                }
                return "$i $j BLKPUSH"
            })
        )
        this.insertHex("60", 8, Op.OpCodeStr("PICK"))
        this.insertHex("61", 8, Op.OpCodeStr("ROLL"))
        this.insertHex("62", 8, Op.OpCodeStr("ROLLREV"))
        this.insertHex("63", 8, Op.OpCodeStr("BLKSWX"))
        this.insertHex("64", 8, Op.OpCodeStr("REVX"))
        this.insertHex("65", 8, Op.OpCodeStr("DROPX"))
        this.insertHex("66", 8, Op.OpCodeStr("TUCK"))
        this.insertHex("67", 8, Op.OpCodeStr("XCHGX"))
        this.insertHex("68", 8, Op.OpCodeStr("DEPTH"))
        this.insertHex("69", 8, Op.OpCodeStr("CHKDEPTH"))
        this.insertHex("6a", 8, Op.OpCodeStr("ONLYTOPX"))
        this.insertHex("6b", 8, Op.OpCodeStr("ONLYX"))
// 7077888 (DUMMY)
        this.insertHex("6c", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val i = slice.loadUInt(4)
                val j = slice.loadUInt(4)
                return "$i $j BLKDROP2"
            })
        )
        this.insertHex("6d", 8, Op.OpCodeStr("PUSHNULL"))
        this.insertHex("6e", 8, Op.OpCodeStr("ISNULL"))
        this.insertHex("6f0", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val n = slice.loadUInt(4)
                if (n == BigInt(0)) {
                    return "NIL"
                }
                if (n == BigInt(1)) {
                    return "SINGLE"
                }
                if (n == BigInt(2)) {
                    return "PAIR"
                }
                if (n == BigInt(3)) {
                    return "TRIPLE"
                }
                return "$n TUPLE"
            })
        )
        this.insertHex("6f1", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val k = slice.loadUInt(4)
                return "$k INDEX"
            })
        )
        this.insertHex("6f2", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val k = slice.loadUInt(4)
                return "$k UNTUPLE"
            })
        )
        this.insertHex("6f3", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val k = slice.loadUInt(4)
                if (k == BigInt(0)) {
                    return "CHKTUPLE"
                }
                return "$k UNPACKFIRST"
            })
        )
        this.insertHex("6f4", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val k = slice.loadUInt(4)
                return "$k EXPLODE"
            })
        )
        this.insertHex("6f5", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val k = slice.loadUInt(4)
                return "$k SETINDEX"
            })
        )
        this.insertHex("6f6", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val k = slice.loadUInt(4)
                return "$k INDEXQ"
            })
        )
        this.insertHex("6f7", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val k = slice.loadUInt(4)
                return "$k SETINDEXQ"
            })
        )
        this.insertHex("6f80", 16, Op.OpCodeStr("TUPLEVAR"))
        this.insertHex("6f81", 16, Op.OpCodeStr("INDEXVAR"))
        this.insertHex("6f82", 16, Op.OpCodeStr("UNTUPLEVAR"))
        this.insertHex("6f83", 16, Op.OpCodeStr("UNPACKFIRSTVAR"))
        this.insertHex("6f84", 16, Op.OpCodeStr("EXPLODEVAR"))
        this.insertHex("6f85", 16, Op.OpCodeStr("SETINDEXVAR"))
        this.insertHex("6f86", 16, Op.OpCodeStr("INDEXVARQ"))
        this.insertHex("6f87", 16, Op.OpCodeStr("SETINDEXVARQ"))
        this.insertHex("6f88", 16, Op.OpCodeStr("TLEN"))
        this.insertHex("6f89", 16, Op.OpCodeStr("QTLEN"))
        this.insertHex("6f8a", 16, Op.OpCodeStr("ISTUPLE"))
        this.insertHex("6f8b", 16, Op.OpCodeStr("LAST"))
        this.insertHex("6f8c", 16, Op.OpCodeStr("TPUSH"))
        this.insertHex("6f8d", 16, Op.OpCodeStr("TPOP"))
// 7310848 (DUMMY)
        this.insertHex("6fa0", 16, Op.OpCodeStr("NULLSWAPIF"))
        this.insertHex("6fa1", 16, Op.OpCodeStr("NULLSWAPIFNOT"))
        this.insertHex("6fa2", 16, Op.OpCodeStr("NULLROTRIF"))
        this.insertHex("6fa3", 16, Op.OpCodeStr("NULLROTRIFNOT"))
        this.insertHex("6fa4", 16, Op.OpCodeStr("NULLSWAPIF2"))
        this.insertHex("6fa5", 16, Op.OpCodeStr("NULLSWAPIFNOT2"))
        this.insertHex("6fa6", 16, Op.OpCodeStr("NULLROTRIF2"))
        this.insertHex("6fa7", 16, Op.OpCodeStr("NULLROTRIFNOT2"))
// 7317504 (DUMMY)
        this.insertHex("6fb", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val i = slice.loadUInt(2)
                val j = slice.loadUInt(2)
                return "$i $j INDEX2"
            })
        )

        this.insertHex("7", 4, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val args = slice.loadInt(4)
                return "$args PUSHINT"
            })
        )
        this.insertHex("80", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val x = slice.loadInt(8)
                return "$x PUSHINT"
            })
        )
        this.insertHex("81", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val x = slice.loadInt(16)
                return "$x PUSHINT"
            })
        )
        this.insertHex("82", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val len = slice.loadUInt(5)
                val n = BigInt(8) * len + BigInt(19)
                val x = slice.loadInt(n.toInt())
                return "${x.toString(10)} PUSHINT"
            })
        )
        this.insertHex("83", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val x = slice.loadUInt(8) + BigInt(1)
                return "$x PUSHPOW2"
            })
        )
        this.insertHex("84", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val x = slice.loadUInt(8) + BigInt(1)
                return "$x PUSHPOW2DEC"
            })
        )
        this.insertHex("850000", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val x = slice.loadUInt(8) + BigInt(1)
                return "$x PUSHNEGPOW2"
            })
        )
// 8781824 (DUMMY)
        this.insertHex("88", 8, Op.OpCodeStr("PUSHREF"))
        this.insertHex("89", 8, Op.OpCodeStr("PUSHREFSLICE"))
        this.insertHex("8a", 8, Op.OpCodeStr("PUSHREFCONT"))
        this.insertHex("8b", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val x = slice.loadUInt(4)
                val len = BigInt(8) * x + BigInt(4)
                val subslice = fetchSubslice(slice, len)
                return "PUSHSLICE"
            })
        )
        this.insertHex("8c0000", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val r = slice.loadUInt(2) + BigInt(1)
                val xx = slice.loadUInt(5)
                val subslice = fetchSubslice(slice, BigInt(8) * xx + BigInt(1), r)
                return "PUSHSLICE"
            })
        )
        this.insertHex("8d", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val r = slice.loadUInt(3)
                val xx = slice.loadUInt(7)
                val subslice = fetchSubslice(slice, BigInt(8) * xx + BigInt(6), r)
                return "PUSHSLICE"
            })
        )
// 9281536 (DUMMY)
        this.insertHex("8E", 7, Op.OpCodeFun(
            fun(slice: CellSlice, indent: Int?): String {
                val args = slice.loadUInt(9)
                val refs = (args shr 7).and(BigInt(3))
                val dataBytes = (args.and(BigInt(127))) * BigInt(8)

                val subslice = fetchSubslice(slice, dataBytes, refs)
                return "<{\n${
                    TvmDisassembler.decompile(
                        subslice,
                        indent!! + 2
                    )
                }${Array(indent) { " " }.joinToString("")}}> PUSHCONT"
            })
        )
        this.insertHex("9", 4, Op.OpCodeFun(
            fun(slice: CellSlice, indent: Int?): String {
                val len = slice.loadUInt(4) * BigInt(8)
                val subslice = fetchSubslice(slice, len)
                return "<{\n${
                    TvmDisassembler.decompile(
                        subslice,
                        indent!! + 2
                    )
                }${Array(indent) { " " }.joinToString("")}}> PUSHCONT"
            })
        )

        this.insertHex("a00000", 8, Op.OpCodeStr("ADD"))
        this.insertHex("a10000", 8, Op.OpCodeStr("SUB"))
        this.insertHex("a20000", 8, Op.OpCodeStr("SUBR"))
        this.insertHex("a30000", 8, Op.OpCodeStr("NEGATE"))
        this.insertHex("a40000", 8, Op.OpCodeStr("INC"))
        this.insertHex("a50000", 8, Op.OpCodeStr("DEC"))
        this.insertHex("a60000", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val x = slice.loadInt(8)
                return "$x ADDCONST"
            })
        )
        this.insertHex("a70000", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val x = slice.loadInt(8)
                return "$x MULCONST"
            })
        )
        this.insertHex("a80000", 8, Op.OpCodeStr("MUL"))
        this.insertHex("A9", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val m = slice.loadBit()
                val s = slice.loadUInt(2)
                val c = slice.loadBit()
                val d = slice.loadUInt(2)
                val f = slice.loadUInt(2)
                var opName = ""
                if (m) {
                    opName += "MUL"
                }
                if (s == BigInt(0)) {
                    opName += "DIV"
                } else {
                    if (s == BigInt(1)) {
                        opName = "RSHIFT"
                    } else {
                        opName = "LSHIFT"
                    }
                    if (!c) {
                        opName += " s0"
                    } else {
                        val shift = slice.loadUInt(8) + BigInt(1)
                        opName += " $shift"
                    }
                }
                if (d == BigInt(1)) {
                    opName += " QOUT"
                } else if (d == BigInt(2)) {
                    opName += " REM"
                } else if (d == BigInt(3)) {
                    opName += " BOTH"
                }
                if (f == BigInt(1)) {
                    opName += " R"
                } else if (f == BigInt(2)) {
                    opName += " C"
                }
                return opName
            })
        )
// 11079680 (DUMMY)
// 11132928 (DUMMY)
        this.insertHex("aa", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val cc = slice.loadUInt(8)
                return "${cc + BigInt(1)} LSHIFT"
            })
        )
        this.insertHex("ab", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val cc = slice.loadUInt(8)
                return "${cc + BigInt(1)} RSHIFT"
            })
        )
        this.insertHex("ac", 8, Op.OpCodeStr("LSHIFT"))
        this.insertHex("ad", 8, Op.OpCodeStr("RSHIFT"))
        this.insertHex("ae", 8, Op.OpCodeStr("POW2"))
// 11468800 (DUMMY)
        this.insertHex("b0", 8, Op.OpCodeStr("AND"))
        this.insertHex("b1", 8, Op.OpCodeStr("OR"))
        this.insertHex("b2", 8, Op.OpCodeStr("XOR"))
        this.insertHex("b3", 8, Op.OpCodeStr("NOT"))
        this.insertHex("b4", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val cc = slice.loadUInt(8)
                return "${cc + BigInt(1)} FITS"
            })
        )
        this.insertHex("b5", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val cc = slice.loadUInt(8)
                return "${cc + BigInt(1)} UFITS"
            })
        )
        this.insertHex("b600", 16, Op.OpCodeStr("FITSX"))
        this.insertHex("b601", 16, Op.OpCodeStr("UFITSX"))
        this.insertHex("b602", 16, Op.OpCodeStr("BITSIZE"))
        this.insertHex("b603", 16, Op.OpCodeStr("UBITSIZE"))
// 11928576 (DUMMY)
        this.insertHex("b608", 16, Op.OpCodeStr("MIN"))
        this.insertHex("b609", 16, Op.OpCodeStr("MAX"))
        this.insertHex("b60a", 16, Op.OpCodeStr("MINMAX"))
        this.insertHex("b60b", 16, Op.OpCodeStr("ABS"))
// 11930624 (DUMMY)
        this.insertHex("b7a0", 16, Op.OpCodeStr("QADD"))
        this.insertHex("b7a1", 16, Op.OpCodeStr("QSUB"))
        this.insertHex("b7a2", 16, Op.OpCodeStr("QSUBR"))
        this.insertHex("b7a3", 16, Op.OpCodeStr("QNEGATE"))
        this.insertHex("b7a4", 16, Op.OpCodeStr("QINC"))
        this.insertHex("b7a5", 16, Op.OpCodeStr("QDEC"))
        this.insertHex("b7a6", 16, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val x = slice.loadInt(8)
                return "$x QADDCONST"
            })
        )
        this.insertHex("b7a7", 16, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val x = slice.loadInt(8)
                return "$x QMULCONST"
            })
        )
        this.insertHex("b7a8", 16, Op.OpCodeStr("QMUL"))
        this.insertHex("b7a9", 16, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val m = slice.loadBit()
                val s = slice.loadUInt(2)
                val c = slice.loadBit()
                val d = slice.loadUInt(2)
                val f = slice.loadUInt(2)
                var opName = "Q"
                if (m) {
                    opName += "MUL"
                }
                if (s == BigInt(0)) {
                    opName += "DIV"
                } else {
                    if (s == BigInt(1)) {
                        opName = "RSHIFT"
                    } else {
                        opName = "LSHIFT"
                    }
                    if (!c) {
                        opName += " s0"
                    } else {
                        val shift = slice.loadUInt(8) + BigInt(1)
                        opName += " $shift"
                    }
                }
                if (d == BigInt(1)) {
                    opName += " QOUT"
                } else if (d == BigInt(2)) {
                    opName += " REM"
                } else if (d == BigInt(3)) {
                    opName += " BOTH"
                }
                if (f == BigInt(1)) {
                    opName += " R"
                } else if (f == BigInt(2)) {
                    opName += " C"
                }
                return opName
            })
        )
// 12036560 (DUMMY)
        this.insertHex("b7aa", 16, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val cc = slice.loadUInt(8)
                return "${cc + BigInt(1)} QLSHIFT"
            })
        )
        this.insertHex("b7ab", 16, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val cc = slice.loadUInt(8)
                return "${cc + BigInt(1)} QLSHIFT"
            })
        )
        this.insertHex("b7ac", 16, Op.OpCodeStr("QLSHIFT"))
        this.insertHex("b7ad", 16, Op.OpCodeStr("QRSHIFT"))
        this.insertHex("b7ae", 16, Op.OpCodeStr("QPOW2"))
// 12037888 (DUMMY)
        this.insertHex("b7b0", 16, Op.OpCodeStr("QAND"))
        this.insertHex("b7b1", 16, Op.OpCodeStr("QOR"))
        this.insertHex("b7b2", 16, Op.OpCodeStr("QXOR"))
        this.insertHex("b7b3", 16, Op.OpCodeStr("QNOT"))
        this.insertHex("b7b4", 16, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val cc = slice.loadUInt(8)
                return "${cc + BigInt(1)} QFITS"
            })
        )
        this.insertHex("b7b5", 16, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val cc = slice.loadUInt(8)
                return "${cc + BigInt(1)} QUFITS"
            })
        )
        this.insertHex("b7b600", 24, Op.OpCodeStr("QFITSX"))
        this.insertHex("b7b601", 24, Op.OpCodeStr("QUFITSX"))
        this.insertHex("b7b602", 24, Op.OpCodeStr("QBITSIZE"))
        this.insertHex("b7b603", 24, Op.OpCodeStr("QUBITSIZE"))
// 12039684 (DUMMY)
        this.insertHex("b7b608", 24, Op.OpCodeStr("QMIN"))
        this.insertHex("b7b609", 24, Op.OpCodeStr("QMAX"))
        this.insertHex("b7b60a", 24, Op.OpCodeStr("QMINMAX"))
        this.insertHex("b7b60b", 24, Op.OpCodeStr("QABS"))
// 12039692 (DUMMY)
        this.insertHex("b7b8", 16, Op.OpCodeStr("QSGN"))
        this.insertHex("b7b9", 16, Op.OpCodeStr("QLESS"))
        this.insertHex("b7ba", 16, Op.OpCodeStr("QEQUAL"))
        this.insertHex("b7bb", 16, Op.OpCodeStr("QLEQ"))
        this.insertHex("b7bc", 16, Op.OpCodeStr("QGREATER"))
        this.insertHex("b7bd", 16, Op.OpCodeStr("QNEQ"))
        this.insertHex("b7be", 16, Op.OpCodeStr("QGEQ"))
        this.insertHex("b7bf", 16, Op.OpCodeStr("QCMP"))
        this.insertHex("b7c0", 16, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val x = slice.loadInt(8)
                return "$x QEQINT"
            })
        )
        this.insertHex("b7c1", 16, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val x = slice.loadInt(8)
                return "$x QLESSINT"
            })
        )
        this.insertHex("b7c2", 16, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val x = slice.loadInt(8)
                return "$x QGTINT"
            })
        )
        this.insertHex("b7c3", 16, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val x = slice.loadInt(8)
                return "$x QNEQINT"
            })
        )
// 12043264 (DUMMY)
        this.insertHex("b8", 8, Op.OpCodeStr("SGN"))
        this.insertHex("b9", 8, Op.OpCodeStr("LESS"))
        this.insertHex("ba", 8, Op.OpCodeStr("EQUAL"))
        this.insertHex("bb", 8, Op.OpCodeStr("LEQ"))
        this.insertHex("bc", 8, Op.OpCodeStr("GREATER"))
        this.insertHex("bd", 8, Op.OpCodeStr("NEQ"))
        this.insertHex("be", 8, Op.OpCodeStr("GEQ"))
        this.insertHex("bf", 8, Op.OpCodeStr("CMP"))
        this.insertHex("c0", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val x = slice.loadInt(8)
                return "$x EQINT"
            })
        )
        this.insertHex("c1", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val x = slice.loadInt(8)
                return "$x LESSINT"
            })
        )
        this.insertHex("c2", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val x = slice.loadInt(8)
                return "$x GTINT"
            })
        )
        this.insertHex("c3", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val x = slice.loadInt(8)
                return "$x NEQINT"
            })
        )
        this.insertHex("c4", 8, Op.OpCodeStr("ISNAN"))
        this.insertHex("c5", 8, Op.OpCodeStr("CHKNAN"))
// 12976128 (DUMMY)
        this.insertHex("c700", 16, Op.OpCodeStr("SEMPTY"))
        this.insertHex("c701", 16, Op.OpCodeStr("SDEMPTY"))
        this.insertHex("c702", 16, Op.OpCodeStr("SREMPTY"))
        this.insertHex("c703", 16, Op.OpCodeStr("SDFIRST"))
        this.insertHex("c704", 16, Op.OpCodeStr("SDLEXCMP"))
        this.insertHex("c705", 16, Op.OpCodeStr("SDEQ"))
// 13043200 (DUMMY)
        this.insertHex("c708", 16, Op.OpCodeStr("SDPFX"))
        this.insertHex("c709", 16, Op.OpCodeStr("SDPFXREV"))
        this.insertHex("c70a", 16, Op.OpCodeStr("SDPPFX"))
        this.insertHex("c70b", 16, Op.OpCodeStr("SDPPFXREV"))
        this.insertHex("c70c", 16, Op.OpCodeStr("SDSFX"))
        this.insertHex("c70d", 16, Op.OpCodeStr("SDSFXREV"))
        this.insertHex("c70e", 16, Op.OpCodeStr("SDPSFX"))
        this.insertHex("c70f", 16, Op.OpCodeStr("SDPSFXREV"))
        this.insertHex("c710", 16, Op.OpCodeStr("SDCNTLEAD0"))
        this.insertHex("c711", 16, Op.OpCodeStr("SDCNTLEAD1"))
        this.insertHex("c712", 16, Op.OpCodeStr("SDCNTTRAIL0"))
        this.insertHex("c713", 16, Op.OpCodeStr("SDCNTTRAIL1"))
// 13046784 (DUMMY)
        this.insertHex("c8", 8, Op.OpCodeStr("NEWC"))
        this.insertHex("c9", 8, Op.OpCodeStr("ENDC"))
        this.insertHex("ca", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val cc = slice.loadUInt(8) + BigInt(1)
                return "$cc STI"
            })
        )
        this.insertHex("cb", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val cc = slice.loadUInt(8) + BigInt(1)
                return "$cc STU"
            })
        )
        this.insertHex("cc", 8, Op.OpCodeStr("STREF"))
        this.insertHex("cd", 8, Op.OpCodeStr("ENDCST"))
        this.insertHex("ce", 8, Op.OpCodeStr("STSLICE"))
        this.insertHex("cf00", 13, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val args = slice.loadUInt(3)
                val sgnd = !(args.and(BigInt(1)) == args)
                var s = "ST"
                s += if (sgnd) "I" else "U"
                s += "X"
                if (args.and(BigInt(2)) == args) {
                    s += "R"
                }
                if (args.and(BigInt(4)) == args) {
                    s += "Q"
                }
                return s
            })
        )
        this.insertHex("cf08", 13, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val args = slice.loadUInt(11)
                val bits = args.and(BigInt(0xff)) + BigInt(1)
                val sgnd = !(args.and(BigInt(0x100)) == args)
                var s = "ST"
                s += if (sgnd) "I" else "U"
                if (args.and(BigInt(0x200)) == args) {
                    s += "R"
                }
                if (args.and(BigInt(0x400)) == args) {
                    s += "Q"
                }
                return "$bits $s"
            })
        )
        this.insertHex("cf10", 16, Op.OpCodeStr("STREF"))
        this.insertHex("cf11", 16, Op.OpCodeStr("STBREF"))
        this.insertHex("cf12", 16, Op.OpCodeStr("STSLICE"))
        this.insertHex("cf13", 16, Op.OpCodeStr("STB"))
        this.insertHex("cf14", 16, Op.OpCodeStr("STREFR"))
        this.insertHex("cf15", 16, Op.OpCodeStr("STBREFR"))
        this.insertHex("cf16", 16, Op.OpCodeStr("STSLICER"))
        this.insertHex("cf17", 16, Op.OpCodeStr("STBR"))
        this.insertHex("cf18", 16, Op.OpCodeStr("STREFQ"))
        this.insertHex("cf19", 16, Op.OpCodeStr("STBREFQ"))
        this.insertHex("cf1a", 16, Op.OpCodeStr("STSLICEQ"))
        this.insertHex("cf1b", 16, Op.OpCodeStr("STBQ"))
        this.insertHex("cf1c", 16, Op.OpCodeStr("STREFRQ"))
        this.insertHex("cf1d", 16, Op.OpCodeStr("STBREFRQ"))
        this.insertHex("cf1e", 16, Op.OpCodeStr("STSLICERQ"))
        this.insertHex("cf1f", 16, Op.OpCodeStr("STBRQ"))
        this.insertHex("cf20", 15, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val flag = slice.loadUInt(1)
                if (flag == BigInt(0)) {
                    return "STREFCONST"
                } else {
                    return "STREF2CONST"
                }
            })
        )
// 13574656 (DUMMY)
        this.insertHex("cf23", 16, Op.OpCodeStr("ENDXC"))
// 13575168 (DUMMY)
        this.insertHex("cf28", 14, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val args = slice.loadUInt(2)
                val sgnd = !(args.and(BigInt(1)) == args)
                return "ST${if (sgnd) "I" else "U"}LE${if (args.and(BigInt(2)) == args) "8" else "4"}"
            })
        )
// 13577216 (DUMMY)
        this.insertHex("cf30", 16, Op.OpCodeStr("BDEPTH"))
        this.insertHex("cf31", 16, Op.OpCodeStr("BBITS"))
        this.insertHex("cf32", 16, Op.OpCodeStr("BREFS"))
        this.insertHex("cf33", 16, Op.OpCodeStr("BBITREFS"))
// 13579264 (DUMMY)
        this.insertHex("cf35", 16, Op.OpCodeStr("BREMBITS"))
        this.insertHex("cf36", 16, Op.OpCodeStr("BREMREFS"))
        this.insertHex("cf37", 16, Op.OpCodeStr("BREMBITREFS"))
        this.insertHex("cf38", 16, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val cc = slice.loadUInt(8)
                return "${cc + BigInt(1)} BCHKBITS"
            })
        )
        this.insertHex("cf39", 16, Op.OpCodeStr("BCHKBITS"))
        this.insertHex("cf3a", 16, Op.OpCodeStr("BCHKREFS"))
        this.insertHex("cf3b", 16, Op.OpCodeStr("BCHKBITREFS"))
        this.insertHex("cf3c", 16, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val cc = slice.loadUInt(8)
                return "${cc + BigInt(1)} BCHKBITSQ"
            })
        )
        this.insertHex("cf3d", 16, Op.OpCodeStr("BCHKBITSQ"))
        this.insertHex("cf3e", 16, Op.OpCodeStr("BCHKREFSQ"))
        this.insertHex("cf3f", 16, Op.OpCodeStr("BCHKBITREFSQ"))
        this.insertHex("cf40", 16, Op.OpCodeStr("STZEROES"))
        this.insertHex("cf41", 16, Op.OpCodeStr("STONES"))
        this.insertHex("cf42", 16, Op.OpCodeStr("STSAME"))
// 13583104 (DUMMY)
        this.insertHex("cf8", 9, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val refs = slice.loadUInt(2)
                val dataBits = slice.loadUInt(3) * BigInt(8) + BigInt(1)
                val subslice = fetchSubslice(slice, dataBits, refs)
                return "STSLICECONST"
            })
        )
        this.insertHex("d0", 8, Op.OpCodeStr("CTOS"))
        this.insertHex("d1", 8, Op.OpCodeStr("ENDS"))
        this.insertHex("d2", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val cc = slice.loadUInt(8)
                return "${cc + BigInt(1)} LDI"
            })
        )
        this.insertHex("d3", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val cc = slice.loadUInt(8)
                return "${cc + BigInt(1)} LDU"
            })
        )
        this.insertHex("d4", 8, Op.OpCodeStr("LDREF"))
        this.insertHex("d5", 8, Op.OpCodeStr("LDREFRTOS"))
        this.insertHex("d6", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val cc = slice.loadUInt(8)
                return "${cc + BigInt(1)} LDSLICE"
            })
        )
        this.insertHex("d70", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val longerVersion = slice.loadBit()
                val quiet = slice.loadBit()
                val preload = slice.loadBit()
                val sign = slice.loadBit()

                return "${if (longerVersion) "${(slice.loadUInt(8) + BigInt(1))} " else ""}${if (preload) "PLD" else "LD"}${if (sign) "U" else "I"}${if (quiet) "Q" else ""}"
            })
        )
        this.insertHex("d710", 13, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val c = slice.loadUInt(3) + BigInt(1)
                return "${BigInt(32) * (c + BigInt(1))} PLDUZ"
            })
        )
        this.insertHex("d718", 14, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val quiet = slice.loadBit()
                val preload = slice.loadBit()
                return "${if (preload) "PLD" else "LD"}SLICEX${if (quiet) "Q" else ""}"
            })
        )
        this.insertHex("d71c", 14, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val quiet = slice.loadBit()
                val preload = slice.loadBit()
                val cc = slice.loadUInt(8)
                return "${cc + BigInt(1)} ${if (preload) "PLD" else "LD"}SLICEX${if (quiet) "Q" else ""}"
            })
        )
        this.insertHex("d720", 16, Op.OpCodeStr("SDCUTFIRST"))
        this.insertHex("d721", 16, Op.OpCodeStr("SDSKIPFIRST"))
        this.insertHex("d722", 16, Op.OpCodeStr("SDCUTLAST"))
        this.insertHex("d723", 16, Op.OpCodeStr("SDSKIPLAST"))
        this.insertHex("d724", 16, Op.OpCodeStr("SDSUBSTR"))
// 14099712 (DUMMY)
        this.insertHex("d726", 16, Op.OpCodeStr("SDBEGINSX"))
        this.insertHex("d727", 16, Op.OpCodeStr("SDBEGINSXQ"))
        this.insertHex("d728", 13, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val args = slice.loadUInt(8)
                return "SDBEGINS"
            })
        )
        this.insertHex("d730", 16, Op.OpCodeStr("SCUTFIRST"))
        this.insertHex("d731", 16, Op.OpCodeStr("SSKIPFIRST"))
        this.insertHex("d732", 16, Op.OpCodeStr("SCUTLAST"))
        this.insertHex("d733", 16, Op.OpCodeStr("SSKIPLAST"))
        this.insertHex("d734", 16, Op.OpCodeStr("SUBSLICE"))
// 14103808 (DUMMY)
        this.insertHex("d736", 16, Op.OpCodeStr("SPLIT"))
        this.insertHex("d737", 16, Op.OpCodeStr("SPLITQ"))
// 14104576 (DUMMY)
        this.insertHex("d739", 16, Op.OpCodeStr("XCTOS"))
        this.insertHex("d73a", 16, Op.OpCodeStr("XLOAD"))
        this.insertHex("d73b", 16, Op.OpCodeStr("XLOADQ"))
// 14105600 (DUMMY)
        this.insertHex("d741", 16, Op.OpCodeStr("SCHKBITS"))
        this.insertHex("d742", 16, Op.OpCodeStr("SCHKREFS"))
        this.insertHex("d743", 16, Op.OpCodeStr("SCHKBITREFS"))
// 14107648 (DUMMY)
        this.insertHex("d745", 16, Op.OpCodeStr("SCHKBITSQ"))
        this.insertHex("d746", 16, Op.OpCodeStr("SCHKREFSQ"))
        this.insertHex("d747", 16, Op.OpCodeStr("SCHKBITREFSQ"))
        this.insertHex("d748", 16, Op.OpCodeStr("PLDREFVAR"))
        this.insertHex("d749", 16, Op.OpCodeStr("SBITS"))
        this.insertHex("d74a", 16, Op.OpCodeStr("SREFS"))
        this.insertHex("d74b", 16, Op.OpCodeStr("SBITREFS"))
        this.insertHex("d74c", 14, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val n = slice.loadUInt(2)
                return "$n PLDREFIDX"
            })
        )
        this.insertHex("d750", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val quiet = slice.loadBit()
                val preload = slice.loadBit()
                val bit64 = slice.loadBit()
                val unsigned = slice.loadBit()
                return "${if (preload) "PLD" else "LD"}${if (unsigned) "U" else "I"}LE${if (bit64) "8" else "4"}${if (quiet) "Q" else ""}"
            })
        )
        this.insertHex("d760", 16, Op.OpCodeStr("LDZEROES"))
        this.insertHex("d761", 16, Op.OpCodeStr("LDONES"))
        this.insertHex("d762", 16, Op.OpCodeStr("LDSAME"))
// 14115584 (DUMMY)
        this.insertHex("d764", 16, Op.OpCodeStr("SDEPTH"))
        this.insertHex("d765", 16, Op.OpCodeStr("CDEPTH"))
// 14116352 (DUMMY)
        this.insertHex("d8", 8, Op.OpCodeStr("EXECUTE"))
        this.insertHex("d9", 8, Op.OpCodeStr("JMPX"))
        this.insertHex("da", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val p = slice.loadUInt(4)
                val r = slice.loadUInt(4)
                return "$p $r CALLXARGS"
            })
        )
        this.insertHex("db0", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val p = slice.loadUInt(4)
                return "$p CALLXARGS"
            })
        )
        this.insertHex("db1", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val p = slice.loadUInt(4)
                return "$p JMPXARGS"
            })
        )
        this.insertHex("db2", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val r = slice.loadUInt(4)
                return "$r RETARGS"
            })
        )
        this.insertHex("db30", 16, Op.OpCodeStr("RET"))
        this.insertHex("db31", 16, Op.OpCodeStr("RETALT"))
        this.insertHex("db32", 16, Op.OpCodeStr("RETBOOL"))
// 14365440 (DUMMY)
        this.insertHex("db34", 16, Op.OpCodeStr("CALLCC"))
        this.insertHex("db35", 16, Op.OpCodeStr("JMPXDATA"))
        this.insertHex("db36", 16, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val p = slice.loadUInt(4)
                val r = slice.loadUInt(4)
                return "$p $r CALLCCARGS"
            })
        )
// 14366464 (DUMMY)
        this.insertHex("db38", 16, Op.OpCodeStr("CALLXVARARGS"))
        this.insertHex("db39", 16, Op.OpCodeStr("RETVARARGS"))
        this.insertHex("db3a", 16, Op.OpCodeStr("JMPXVARARGS"))
        this.insertHex("db3b", 16, Op.OpCodeStr("CALLCCVARARGS"))
        this.insertHex("db3c", 16, Op.OpCodeFun(
            fun(slice: CellSlice, indent: Int?): String {
                val subslice = slice.loadRef().beginParse()
                return "<{\n${
                    TvmDisassembler.decompile(
                        subslice,
                        indent!! + 2
                    )
                }${Array(indent) { " " }.joinToString("")}}> CALLREF"
            })
        )
        this.insertHex("db3d", 16, Op.OpCodeFun(
            fun(slice: CellSlice, indent: Int?): String {
                val subslice = slice.loadRef().beginParse()
                return "<{\n${
                    TvmDisassembler.decompile(
                        subslice,
                        indent!! + 2
                    )
                }${Array(indent) { " " }.joinToString("")}}> JMPREF"
            })
        )
        this.insertHex("db3e", 16, Op.OpCodeFun(
            fun(slice: CellSlice, indent: Int?): String {
                val subslice = slice.loadRef().beginParse()
                return "<{\n${
                    TvmDisassembler.decompile(
                        subslice,
                        indent!! + 2
                    )
                }${Array(indent) { " " }.joinToString("")}}> JMPREFDATA"
            })
        )
        this.insertHex("db3f", 16, Op.OpCodeStr("RETDATA"))
// 14368768 (DUMMY)
        this.insertHex("dc", 8, Op.OpCodeStr("IFRET"))
        this.insertHex("dd", 8, Op.OpCodeStr("IFNOTRET"))
        this.insertHex("de", 8, Op.OpCodeStr("IF"))
        this.insertHex("df", 8, Op.OpCodeStr("IFNOT"))
        this.insertHex("e0", 8, Op.OpCodeStr("IFJMP"))
        this.insertHex("e1", 8, Op.OpCodeStr("IFNOTJMP"))
        this.insertHex("e2", 8, Op.OpCodeStr("IFELSE"))
        this.insertHex("e300", 16, Op.OpCodeFun(
            fun(slice: CellSlice, indent: Int?): String {
                val subslice = slice.loadRef().beginParse()
                return "<{\n${
                    TvmDisassembler.decompile(
                        subslice,
                        indent!! + 2
                    )
                }${Array(indent) { " " }.joinToString("")}}> IFREF"
            })
        )
        this.insertHex("e301", 16, Op.OpCodeFun(
            fun(slice: CellSlice, indent: Int?): String {
                val subslice = slice.loadRef().beginParse()
                return "<{\n${
                    TvmDisassembler.decompile(
                        subslice,
                        indent!! + 2
                    )
                }${Array(indent) { " " }.joinToString("")}}> IFNOTREF"
            })
        )
        this.insertHex("e302", 16, Op.OpCodeFun(
            fun(slice: CellSlice, indent: Int?): String {
                val subslice = slice.loadRef().beginParse()
                return "<{\n${
                    TvmDisassembler.decompile(
                        subslice,
                        indent!! + 2
                    )
                }${Array(indent) { " " }.joinToString("")}}> IFJMPREF"
            })
        )
        this.insertHex("e303", 16, Op.OpCodeFun(
            fun(slice: CellSlice, indent: Int?): String {
                val subslice = slice.loadRef().beginParse()
                return "<{\n${
                    TvmDisassembler.decompile(
                        subslice,
                        indent!! + 2
                    )
                }${Array(indent) { " " }.joinToString("")}}> IFNOTJMPREF"
            })
        )
        this.insertHex("e304", 16, Op.OpCodeStr("CONDSEL"))
        this.insertHex("e305", 16, Op.OpCodeStr("CONDSELCHK"))
// 14878208 (DUMMY)
        this.insertHex("e308", 16, Op.OpCodeStr("IFRETALT"))
        this.insertHex("e309", 16, Op.OpCodeStr("IFNOTRETALT"))
// 14879232 (DUMMY)
        this.insertHex("e30d", 16, Op.OpCodeFun(
            fun(slice: CellSlice, indent: Int?): String {
                val subslice = slice.loadRef().beginParse()
                return "<{\n${
                    TvmDisassembler.decompile(
                        subslice,
                        indent!! + 2
                    )
                }${Array(indent) { " " }.joinToString("")}}> IFREFELSE"
            })
        )
        this.insertHex("e30e", 16, Op.OpCodeFun(
            fun(slice: CellSlice, indent: Int?): String {
                val subslice = slice.loadRef().beginParse()
                return "<{\n${
                    TvmDisassembler.decompile(
                        subslice,
                        indent!! + 2
                    )
                }${Array(indent) { " " }.joinToString("")}}> IFELSEREF"
            })
        )
        this.insertHex("e30f", 16, Op.OpCodeFun(
            fun(slice: CellSlice, indent: Int?): String {
                val subslice = slice.loadRef().beginParse()
                return "<{\n${
                    TvmDisassembler.decompile(
                        subslice,
                        indent!! + 2
                    )
                }${Array(indent) { " " }.joinToString("")}}> IFREFELSEREF"
            })
        )
// 14880768 (DUMMY)
        this.insertHex("e314", 16, Op.OpCodeStr("REPEATBRK"))
        this.insertHex("e315", 16, Op.OpCodeStr("REPEATENDBRK"))
        this.insertHex("e316", 16, Op.OpCodeStr("UNTILBRK"))
        this.insertHex("e317", 16, Op.OpCodeStr("UNTILENDBRK"))
        this.insertHex("e318", 16, Op.OpCodeStr("WHILEBRK"))
        this.insertHex("e319", 16, Op.OpCodeStr("WHILEENDBRK"))
        this.insertHex("e31a", 16, Op.OpCodeStr("AGAINBRK"))
        this.insertHex("e31b", 16, Op.OpCodeStr("AGAINENDBRK"))
// 14883840 (DUMMY)
        this.insertHex("e38", 10, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val args = slice.loadUInt(6)
                return "(FIXED 879)"
            })
        )
        this.insertHex("e3c", 10, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val args = slice.loadUInt(6)
                return "(EXT)"
            })
        )
        this.insertHex("e4", 8, Op.OpCodeStr("REPEAT"))
        this.insertHex("e5", 8, Op.OpCodeStr("REPEATEND"))
        this.insertHex("e6", 8, Op.OpCodeStr("UNTIL"))
        this.insertHex("e7", 8, Op.OpCodeStr("UNTILEND"))
        this.insertHex("e8", 8, Op.OpCodeStr("WHILE"))
        this.insertHex("e9", 8, Op.OpCodeStr("WHILEEND"))
        this.insertHex("ea", 8, Op.OpCodeStr("AGAIN"))
        this.insertHex("eb", 8, Op.OpCodeStr("AGAINEND"))
        this.insertHex("ec", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val r = slice.loadUInt(4)
                val n = slice.loadUInt(4)
                return "$r, $n SETCONTARGS"
            })
        )
        this.insertHex("ed0", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val p = slice.loadUInt(4)
                return "$p RETURNARGS"
            })
        )
        this.insertHex("ed10", 16, Op.OpCodeStr("RETURNVARARGS"))
        this.insertHex("ed11", 16, Op.OpCodeStr("SETCONTVARARGS"))
        this.insertHex("ed12", 16, Op.OpCodeStr("SETNUMVARARGS"))
// 15536896 (DUMMY)
        this.insertHex("ed1e", 16, Op.OpCodeStr("BLESS"))
        this.insertHex("ed1f", 16, Op.OpCodeStr("BLESSVARARGS"))
// 15540224 (DUMMY)
        this.insertHex("ed4", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val n = slice.loadUInt(4)
                return "c$n PUSH"
            })
        )
        this.insertHex("ed5", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val x = slice.loadUInt(4)
                return "c$x POP"
            })
        )
// 15554560 (DUMMY)
        this.insertHex("ed6", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val i = slice.loadUInt(4)
                return "c$i SETCONT"
            })
        )
// 15558656 (DUMMY)
        this.insertHex("ed7", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val i = slice.loadUInt(4)
                return "c$i SETRETCTR"
            })
        )
// 15562752 (DUMMY)
        this.insertHex("ed8", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val i = slice.loadUInt(4)
                return "c$i SETALTCTR"
            })
        )
// 15566848 (DUMMY)
        this.insertHex("ed9", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val i = slice.loadUInt(4)
                return "c$i POPSAVE"
            })
        )
// 15570944 (DUMMY)
        this.insertHex("eda", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val i = slice.loadUInt(4)
                return "c$i SAVE"
            })
        )
        this.insertHex("edb", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val i = slice.loadUInt(4)
                return "c$i SAVEALT"
            })
        )
        this.insertHex("edc", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val i = slice.loadUInt(4)
                return "c$i SAVEBOTH"
            })
        )
        this.insertHex("ede0", 16, Op.OpCodeStr("PUSHCTRX"))
        this.insertHex("ede1", 16, Op.OpCodeStr("POPCTRX"))
        this.insertHex("ede2", 16, Op.OpCodeStr("SETCONTCTRX"))
        this.insertHex("edf0", 16, Op.OpCodeStr("BOOLAND"))
        this.insertHex("edf1", 16, Op.OpCodeStr("BOOLOR"))
        this.insertHex("edf2", 16, Op.OpCodeStr("COMPOSBOTH"))
        this.insertHex("edf3", 16, Op.OpCodeStr("ATEXIT"))
        this.insertHex("edf4", 16, Op.OpCodeStr("ATEXITALT"))
        this.insertHex("edf5", 16, Op.OpCodeStr("SETEXITALT"))
        this.insertHex("edf6", 16, Op.OpCodeStr("THENRET"))
        this.insertHex("edf7", 16, Op.OpCodeStr("THENRETALT"))
        this.insertHex("edf8", 16, Op.OpCodeStr("INVERT"))
        this.insertHex("edf9", 16, Op.OpCodeStr("BOOLEVAL"))
        this.insertHex("edfa", 16, Op.OpCodeStr("SAMEALT"))
        this.insertHex("edfb", 16, Op.OpCodeStr("SAMEALTSAVE"))
// 15596544 (DUMMY)
        this.insertHex("ee", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val r = slice.loadUInt(4)
                val n = slice.loadUInt(4)
                return "$r,$n BLESSARGS"
            })
        )
// 15663104 (DUMMY)
        this.insertHex("f0", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val n = slice.loadUInt(8)
                return "$n CALLDICT"
            })
        )
        this.insertHex("f10", 10, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val n = slice.loadUInt(14)
                return "$n CALL"
            })
        )
// 15843328 (DUMMY)
        this.insertHex("f20", 10, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val nn = slice.loadUInt(6)
                return "$nn THROW"
            })
        )
        this.insertHex("F24", 10, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val eCode = slice.loadUInt(6)
                return "$eCode THROWIF"
            })
        )
        this.insertHex("F28", 10, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val eCode = slice.loadUInt(6)
                return "$eCode THROWIFNOT"
            })
        )
        this.insertHex("f2c0", 13, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val args = slice.loadUInt(11)
                return "$args THROW"
            })
        )
        this.insertHex("f2c8", 13, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val x = slice.loadUInt(11)
                return "$x THROWARG"
            })
        )
        this.insertHex("f2d0", 13, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val x = slice.loadUInt(11)
                return "$x THROWIF"
            })
        )
        this.insertHex("f2e0", 13, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val x = slice.loadUInt(11)
                return "$x THROWIFNOT"
            })
        )
        this.insertHex("f2f0", 13, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val inverse = slice.loadBit()
                val cond = slice.loadBit()
                val arg = slice.loadBit()
                return "THROW${if (arg) "ARG" else ""}ANY${if (cond || inverse) "IF" else ""}${if (inverse) "NOT" else ""}"
            })
        )
// 15922688 (DUMMY)
        this.insertHex("f2ff", 16, Op.OpCodeStr("TRY"))
        this.insertHex("f3", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val p = slice.loadUInt(4)
                val r = slice.loadUInt(4)
                return "$p,$r TRYARGS"
            })
        )
        this.insertHex("f400", 16, Op.OpCodeStr("STDICT"))
        this.insertHex("f401", 16, Op.OpCodeStr("SKIPDICT"))
        this.insertHex("f402", 16, Op.OpCodeStr("LDDICTS"))
        this.insertHex("f403", 16, Op.OpCodeStr("PLDDICTS"))
        this.insertHex("f404", 16, Op.OpCodeStr("LDDICT"))
        this.insertHex("f405", 16, Op.OpCodeStr("PLDDICT"))
        this.insertHex("f406", 16, Op.OpCodeStr("LDDICTQ"))
        this.insertHex("f407", 16, Op.OpCodeStr("PLDDICTQ"))
// 15992832 (DUMMY)

        this.insertHex("f40a", 16, Op.OpCodeStr("DICTGET"))
        this.insertHex("f40b", 16, Op.OpCodeStr("DICTGETREF"))
        this.insertHex("f40c", 16, Op.OpCodeStr("DICTIGET"))
        this.insertHex("f40d", 16, Op.OpCodeStr("DICTIGETREF"))
        this.insertHex("f40e", 16, Op.OpCodeStr("DICTUGET"))
        this.insertHex("f40f", 16, Op.OpCodeStr("DICTUGETREF"))

// 15994880 (DUMMY)

// TODO: refactor to conditionals
        this.insertHex("f412", 16, Op.OpCodeStr("DICTSET"))
        this.insertHex("f413", 16, Op.OpCodeStr("DICTSETREF"))
        this.insertHex("f414", 16, Op.OpCodeStr("DICTISET"))
        this.insertHex("f415", 16, Op.OpCodeStr("DICTISETREF"))
        this.insertHex("f416", 16, Op.OpCodeStr("DICTUSET"))
        this.insertHex("f417", 16, Op.OpCodeStr("DICTUSETREF"))

        this.insertHex("f41a", 16, Op.OpCodeStr("DICTSETGET"))
        this.insertHex("F41B", 16, Op.OpCodeStr("DICTSETGETREF"))
        this.insertHex("F41C", 16, Op.OpCodeStr("DICTISETGET"))
        this.insertHex("F41D", 16, Op.OpCodeStr("DICTISETGETREF"))
        this.insertHex("F41E", 16, Op.OpCodeStr("DICTUSETGET"))
        this.insertHex("F41F", 16, Op.OpCodeStr("DICTUSETGETREF"))

// 15998976 (DUMMY)
        this.insertHex("f420", 13, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val (type, ref) = typeRef(slice)
                return "DICT${type}REPLACE${if (ref) "REF" else ""}"
            })
        )
// 16001024 (DUMMY)
        this.insertHex("f42a", 13, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val (type, ref) = typeRef(slice)
                return "DICT${type}REPLACEGET${if (ref) "REF" else ""}"
            })
        )
// 16003072 (DUMMY)
        this.insertHex("f432", 13, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val (type, ref) = typeRef(slice)
                return "DICT${type}ADD${if (ref) "REF" else ""}"
            })
        )
// 16005120 (DUMMY)
        this.insertHex("f43a", 13, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val (type, ref) = typeRef(slice)
                return "DICT${type}ADDGET${if (ref) "REF" else ""}"
            })
        )
// 16007168 (DUMMY)
        this.insertHex("f441", 14, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val int = slice.loadBit()
                val usign = slice.loadBit()
                return "DICT${if (int) if (usign) "U" else "I" else ""}SETB"
            })
        )
// 16008192 (DUMMY)
        this.insertHex("f445", 14, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val int = slice.loadBit()
                val usign = slice.loadBit()
                return "DICT${if (int) if (usign) "U" else "I" else ""}SETGETB"
            })
        )
// 16009216 (DUMMY)
        this.insertHex("f449", 14, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val int = slice.loadBit()
                val usign = slice.loadBit()
                return "DICT${if (int) if (usign) "U" else "I" else ""}REPLACEB"
            })
        )
// 16010240 (DUMMY)
        this.insertHex("f44d", 14, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val int = slice.loadBit()
                val usign = slice.loadBit()
                return "DICT${if (int) if (usign) "U" else "I" else ""}REPLACEGETB"
            })
        )
// 16011264 (DUMMY)
        this.insertHex("f451", 14, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val int = slice.loadBit()
                val usign = slice.loadBit()
                return "DICT${if (int) if (usign) "U" else "I" else ""}ADDB"
            })
        )
// 16012288 (DUMMY)
        this.insertHex("f455", 14, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val int = slice.loadBit()
                val usign = slice.loadBit()
                return "DICT${if (int) if (usign) "U" else "I" else ""}ADDGETB"
            })
        )
// 16013312 (DUMMY)
        this.insertHex("f459", 16, Op.OpCodeStr("DICTDEL"))
        this.insertHex("f45A", 16, Op.OpCodeStr("DICTIDEL"))
        this.insertHex("f45B", 16, Op.OpCodeStr("DICTUDEL"))

// 16014336 (DUMMY)
        this.insertHex("f462", 13, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val (type, ref) = typeRef(slice)
                return "DICT${type}DELGET${if (ref) "REF" else ""}"
            })
        )
// 16017408 (DUMMY)
        this.insertHex("f469", 16, Op.OpCodeStr("DICTGETOPTREF"))
        this.insertHex("f46A", 16, Op.OpCodeStr("DICTIGETOPTREF"))
        this.insertHex("f46B", 16, Op.OpCodeStr("DICTUGETOPTREF"))

        this.insertHex("f46d", 16, Op.OpCodeStr("DICTSETGETOPTREF"))
        this.insertHex("f46e", 16, Op.OpCodeStr("DICTISETGETOPTREF"))
        this.insertHex("f46f", 16, Op.OpCodeStr("DICTUSETGETOPTREF"))

        this.insertHex("f47", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val args = slice.loadUInt(4)
                if (args == BigInt(0)) {
                    return "PFXDICTSET"
                } else if (args == BigInt(1)) {
                    return "PFXDICTREPLACE"
                } else if (args == BigInt(2)) {
                    return "PFXDICTADD"
                } else if (args == BigInt(3)) {
                    return "PFXDICTDEL"
                }
                var res = "DICT"
                if (args.and(BigInt(8)) != BigInt(0)) {
                    res += if (args.and(BigInt(4)) != BigInt(0)) "U" else "I"
                }
                return "DICT${if (args.and(BigInt(4)) != BigInt(0)) "U" else "I"}GET${
                    if (args.and(BigInt(2)) != BigInt(
                            0
                        )
                    ) "PREV" else "NEXT"
                }${if (args.and(BigInt(1)) != BigInt(0)) "EQ" else ""}"
            })
        )
        this.insertHex("f48", 11, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val remove = slice.loadBit()
                val max = slice.loadBit()
                val (type, ref) = typeRef(slice)
                return "DICT${type}${if (remove) "REM" else ""}${if (max) "MAX" else "MIN"}${if (ref) "REF" else ""}"
            })
        )
        this.insertHex("f4a0", 13, Op.OpCodeFun(
            fun(slice: CellSlice, indent: Int?): String {
                val push = slice.loadBit()
                if (push) { // f4a4
                    val subslice = fetchSubslice(slice, BigInt(0), BigInt(1))
                    val keyLen = slice.loadUInt(10)
                    val decompiled: String = try {
                        TvmDisassembler.decompileMethodsMap(subslice, keyLen.toInt(), indent!!)
                    } catch (e: Exception) {
                        subslice.bits.toString()
                    }
                    return "$decompiled $keyLen DICTPUSHCONST"
                }
                val exec = slice.loadBit()
                val usign = slice.loadBit()
                return "DICT${if (usign) "U" else "I"}GET${if (exec) "EXEC" else "JMP"}"
            })
        )
        this.insertHex("f4a8", 16, Op.OpCodeStr("PFXDICTGETQ"))
        this.insertHex("f4a9", 16, Op.OpCodeStr("PFXDICTGET"))
        this.insertHex("f4aa", 16, Op.OpCodeStr("PFXDICTGETJMP"))
        this.insertHex("f4ab", 16, Op.OpCodeStr("PFXDICTGETEXEC"))

// 16035840 (DUMMY)
        this.insertHex("f4b1", 13, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val (type, ref) = typeRef(slice)
                return "SUBDICT${type}GET${if (ref) "REF" else ""}"
            })
        )
// 16036864 (DUMMY)
        this.insertHex("f4b5", 13, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val (type, ref) = typeRef(slice)
                return "SUBDICT${type}RPGET${if (ref) "REF" else ""}"
            })
        )
// 16037888 (DUMMY)
        this.insertHex("f4bc", 14, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val exec = slice.loadBit()
                val unsigned = slice.loadBit()
                return "DICT${if (unsigned) "U" else "I"}GET${if (exec) "EXEC" else "JMP"}Z"
            })
        )
// 16039936 (DUMMY)
        this.insertHex("f800", 16, Op.OpCodeStr("ACCEPT"))
        this.insertHex("f801", 16, Op.OpCodeStr("SETGASLIMIT"))
// 16253440 (DUMMY)
        this.insertHex("f80f", 16, Op.OpCodeStr("COMMIT"))
        this.insertHex("f810", 16, Op.OpCodeStr("RANDU256"))
        this.insertHex("f811", 16, Op.OpCodeStr("RAND"))
// 16257536 (DUMMY)
        this.insertHex("f814", 16, Op.OpCodeStr("SETRAND"))
        this.insertHex("f815", 16, Op.OpCodeStr("ADDRAND"))
        this.insertHex("f82", 12, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val i = slice.loadUInt(4)
                if (i == BigInt(0x3)) {
                    return "NOW"
                } else if (i == BigInt(0x4)) {
                    return "BLOCKLT"
                } else if (i == BigInt(0x5)) {
                    return "LTIME"
                } else if (i == BigInt(0x6)) {
                    return "RANDSEED"
                } else if (i == BigInt(0x7)) {
                    return "BALANCE"
                } else if (i == BigInt(0x8)) {
                    return "MYADDR"
                } else if (i == BigInt(0x9)) {
                    return "CONFIGROOT"
                }
                return "$i GETPARAM"
            })
        )
        this.insertHex("f830", 16, Op.OpCodeStr("CONFIGDICT"))
// 16265472 (DUMMY)
        this.insertHex("f832", 16, Op.OpCodeStr("CONFIGPARAM"))
        this.insertHex("f833", 16, Op.OpCodeStr("CONFIGOPTPARAM"))
// 16266240 (DUMMY)
        this.insertHex("f841", 11, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val args = slice.loadUInt(5)
                return "$args GETGLOBVAR"
            })
        )
        this.insertHex("f861", 11, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val args = slice.loadUInt(5)
                return "$args SETGLOBVAR"
            })
        )
// 16285696 (DUMMY)
        this.insertHex("f900", 16, Op.OpCodeStr("HASHCU"))
        this.insertHex("f901", 16, Op.OpCodeStr("HASHSU"))
        this.insertHex("f902", 16, Op.OpCodeStr("SHA256U"))
// 16319232 (DUMMY)
        this.insertHex("f910", 16, Op.OpCodeStr("CHKSIGNU"))
        this.insertHex("f911", 16, Op.OpCodeStr("CHKSIGNS"))
// 16323072 (DUMMY)
        this.insertHex("f940", 16, Op.OpCodeStr("CDATASIZEQ"))
        this.insertHex("f941", 16, Op.OpCodeStr("CDATASIZE"))
        this.insertHex("f942", 16, Op.OpCodeStr("SDATASIZEQ"))
        this.insertHex("f943", 16, Op.OpCodeStr("SDATASIZE"))
// 16335872 (DUMMY)
        this.insertHex("fa00", 16, Op.OpCodeStr("LDGRAMS"))
        this.insertHex("fa01", 16, Op.OpCodeStr("LDVARINT16"))
        this.insertHex("fa02", 16, Op.OpCodeStr("STGRAMS"))
        this.insertHex("fa03", 16, Op.OpCodeStr("STVARINT16"))
        this.insertHex("fa04", 16, Op.OpCodeStr("LDVARUINT32"))
        this.insertHex("fa05", 16, Op.OpCodeStr("LDVARINT32"))
        this.insertHex("fa06", 16, Op.OpCodeStr("STVARUINT32"))
        this.insertHex("fa07", 16, Op.OpCodeStr("STVARINT32"))
// 16386048 (DUMMY)
        this.insertHex("fa40", 16, Op.OpCodeStr("LDMSGADDR"))
        this.insertHex("fa41", 16, Op.OpCodeStr("LDMSGADDRQ"))
        this.insertHex("fa42", 16, Op.OpCodeStr("PARSEMSGADDR"))
        this.insertHex("fa43", 16, Op.OpCodeStr("PARSEMSGADDRQ"))
        this.insertHex("fa44", 16, Op.OpCodeStr("REWRITESTDADDR"))
        this.insertHex("fa45", 16, Op.OpCodeStr("REWRITESTDADDRQ"))
        this.insertHex("fa46", 16, Op.OpCodeStr("REWRITEVARADDR"))
        this.insertHex("fa47", 16, Op.OpCodeStr("REWRITEVARADDRQ"))
// 16402432 (DUMMY)
        this.insertHex("fb00", 16, Op.OpCodeStr("SENDRAWMSG"))
// 16449792 (DUMMY)
        this.insertHex("fb02", 16, Op.OpCodeStr("RAWRESERVE"))
        this.insertHex("fb03", 16, Op.OpCodeStr("RAWRESERVEX"))
        this.insertHex("fb04", 16, Op.OpCodeStr("SETCODE"))
// 16450816 (DUMMY)
        this.insertHex("fb06", 16, Op.OpCodeStr("SETLIBCODE"))
        this.insertHex("fb07", 16, Op.OpCodeStr("CHANGELIB"))
// 16451584 (DUMMY)
        this.insertHex("fe", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                val nn = slice.loadUInt(8)
                if (nn.and(BigInt(0xf0)) == BigInt(0xf0)) {
                    val n = nn.and(BigInt(0x0f))
                    val str = String(slice.loadBits((n + BigInt(1)).toInt()).toByteArray())
                    return "$str DEBUGSTR"
                }
                return "$nn DEBUG"
            })
        )
        this.insertHex("ff", 8, Op.OpCodeFun(
            fun(slice: CellSlice, _: Int?): String {
                var nn = slice.loadUInt(8)
                if (nn.and(BigInt(0xf0)) == BigInt(0xf0)) {
                    val z = nn.and(BigInt(0x0f))
                    if (z == BigInt(0)) {
                        return "SETCPX"
                    }
                    nn = z - BigInt(16)
                }
                return "$nn SETCP"
            })
        )
    }
}
