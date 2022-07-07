package org.ton.disassembler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DisassemblerApplication

fun main(args: Array<String>) {
    runApplication<DisassemblerApplication>(*args)
}