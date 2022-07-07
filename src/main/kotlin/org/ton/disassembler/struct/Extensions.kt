package org.ton.disassembler.struct

import org.ton.bigint.BigInt
import org.ton.bitstring.BitString
import org.ton.cell.CellSlice
import kotlin.math.log2

object Extensions {

    fun <T> CellSlice.loadDict(keySize: Int, extractor: (CellSlice) -> T): Map<String, T> {
        this.refs.firstOrNull()?.let {
            this.refs.drop(1)
            return parseDict(it.beginParse(), keySize, extractor)
        } ?: throw RuntimeException("No ref")
    }

    private fun <T> parseDict(slice: CellSlice, keySize: Int, extractor: (CellSlice) -> T): Map<String, T> {
        val res = mapOf<String, T>()
        doParse("", slice, keySize.toDouble(), res.toMutableMap(), extractor)
        return res
    }

    private fun <T> doParse(prefix: String, slice: CellSlice, n: Double, res: MutableMap<String, T>, extractor: (CellSlice) -> T) {
        // Reading label
        val lb0 = if (slice.loadBit()) 1 else 0
        var prefixLength = 0
        var pp = prefix

        if (lb0 == 0) {
            // Short label detected

            // Read
            prefixLength = slice.bits.size

            // Read prefix
            for (i in 0 until prefixLength) {
                pp += if (slice.loadBit()) "1" else "0"
            }
        } else {
            val lb1 = if (slice.loadBit()) 1 else 0
            if (lb1 == 0) {
                // Long label detected
                prefixLength = slice.loadUInt(Math.ceil(log2(n + 1)).toInt()).toInt()
                for (i in 0 until prefixLength) {
                    pp += if (slice.loadBit()) "1" else "0"
                }
            } else {
                // Same label detected
                val bit = if (slice.loadBit()) "1" else "0";
                prefixLength = slice.loadUInt(Math.ceil(log2(n + 1)).toInt()).toInt()
                for (i in 0 until prefixLength) {
                    pp += bit;
                }
            }
        }

        if (n - prefixLength == 0.toDouble()) {
            res[BigInt(pp, 2).toString(10)] = extractor(slice)
        } else {
            val left = slice.loadRef()
            val right = slice.loadRef()
            // NOTE: Left and right branches are implicitly contain prefixes "0" and "1"
            if (!left.isExotic) {
                doParse(pp + "0", left.beginParse(), n - prefixLength - 1, res, extractor)
            }
            if (!right.isExotic) {
                doParse(pp + "1", right.beginParse(), n - prefixLength - 1, res, extractor)
            }
        }
    }

    fun CellSlice.readRemaining(): BitString {
        val bits = mutableListOf<Boolean>()
        while (this.bitsPosition < this.bits.size) {
            bits.add(this.loadBit())
        }
        return BitString(bits)
    }
}