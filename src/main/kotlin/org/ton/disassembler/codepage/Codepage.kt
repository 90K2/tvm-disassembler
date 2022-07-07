package org.ton.disassembler.codepage

import org.ton.cell.CellSlice
import org.ton.disassembler.struct.Trie

open class Codepage {

    private val trie = Trie<Op>()

    fun insertHex(hex: String, len: Int, op: Op) {
        val prefixArray = Integer.parseInt(hex, 16).toString(2)
        var prefix = prefixArray.slice(0..(if (len < prefixArray.length) len else prefixArray.length-1))
        if (prefix.length < len)
           prefix = Array(len - prefix.length) { "0" }.joinToString("") + prefix
        trie.insert(prefix, op)
    }

    fun insertBin(bin: String, op: Op) {
        trie.insert(bin, op)
    }

    fun getOp(bitPrefix: String): Op? = trie.getValue(bitPrefix)

    fun find(prefix: String): MutableList<String> {
        return trie.find(prefix)
    }
}

sealed class Op {
    class OpCodeStr(val code: String): Op()
    class OpCodeFun(val fn: (CellSlice, Int?) -> String): Op()
}
