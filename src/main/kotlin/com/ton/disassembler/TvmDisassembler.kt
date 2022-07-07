package com.ton.disassembler

import com.ton.disassembler.codepage.Cp0Auto
import com.ton.disassembler.codepage.Op
import com.ton.disassembler.const.KnownMethods
import com.ton.disassembler.struct.Extensions.loadDict
import com.ton.disassembler.struct.Extensions.readRemaining
import com.ton.disassembler.struct.Extensions.toFiftHex
import org.ton.bigint.BigInt
import org.ton.bitstring.BitString
import org.ton.cell.Cell
import org.ton.cell.CellSlice
import kotlin.math.ceil
import kotlin.math.log2

object TVMDecompiler {

    private val codepage = Cp0Auto()

    private fun String.append(txt: String, indent: Int?): String {
        var buffer = this
        indent?.let {
            repeat(it) { buffer += "" }
        }
        return buffer + txt + "\n"
    }


    fun decompileMethodsMap(slice: CellSlice, keySize: Int, indent: Int? ): String {
        var indent_ = indent
        val methodsMap: Map<String, String> = slice.loadDict(19, fun(slice: CellSlice): String {
            return decompile(slice, (indent ?: 0) + 4)
        })
        var result = ""
        result = result.append("(:methods", indent_)
        indent_ = (indent ?: 0) + 2
        methodsMap.entries.forEach {
            val (key, code) = listOf(it.key, it.value)
            val cell = Cell()
            cell.bits.plus(BigInt(key, 19));
            val methodId = cell.beginParse().loadInt(19).toInt()
            result = result.append("${KnownMethods.map[methodId] ?: methodId}: \n${code}", indent_)
        }
        result = result.slice(0..result.length-2) // remove trailing newline
        indent_ -= 2
        result = result.append(")", indent_)
        result = result.slice(0..result.length-2) // remove trailing newline
        return result
    }

    fun decompile(slice: CellSlice, indent: Int? = null): String {

        var workSlice = slice
        var result = ""
        var opCode = ""

        while (workSlice.bits.size > workSlice.bitsPosition) {
            val opCodePart = workSlice.loadBit()
            opCode += if (opCodePart) "1" else "0"
            val matches = codepage.find(opCode)
            if (matches.size > 1) {
                continue
            }
            if (matches.size == 1 && opCode.length != matches.first().length) {
                continue
            }
            if (matches.isEmpty()) {
                val bitString = BitString(opCode.map { it.toString() != "0" })
                bitString.plus(workSlice.readRemaining())
                result = result.append(bitString.toFiftHex(), indent)
                continue
            }

            val op = codepage.getOp(opCode)
            opCode = ""
            if (op == null) {
                result = result.append("NULL", indent)
                continue
            }
            if (op is Op.OpCodeStr) {
                result = result.append(op.code, indent)
            } else if (op is Op.OpCodeFun){
                result = result.append(op.fn(workSlice, indent ?: 0), indent)
            }
            if ( workSlice.bits.size == workSlice.bitsPosition && workSlice.refs.isNotEmpty()) {
                val nextRef = workSlice.loadRef()
                workSlice = CellSlice.of(nextRef.bits, nextRef.refs)
            }
        }
        return result
    }

    fun fromCode(cell: Cell): String {
        val slice = cell.beginParse()
        val header = slice.loadUInt(16).toInt()
        if (header != 0xff00) throw RuntimeException("unsupported codepage")

        return "SETCP0\n" + decompile(slice)
    }
}

