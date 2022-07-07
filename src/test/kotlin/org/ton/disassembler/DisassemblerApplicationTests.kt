package org.ton.disassembler

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.ton.boc.BagOfCells
import org.ton.crypto.hex

@SpringBootTest
class DisassemblerApplicationTests {

	val mainNetNft = "EQB8zFOBBsgiERJ_MG7k_OPmw101t0ViOyuEry4AtQTLDqM6"

	@Test
	fun `Test fift revers`() {
		runBlocking {
//			val raw = tonClient.getAddress(mainNetNft)

//			println(TVMDecompiler.fromCode(
//				(raw.storage.state as AccountActive).init.code.value!!
//			))

			val code = BagOfCells(hex("b5ee9c7241010101005f0000baff0020dd2082014c97ba218201339cbab19c71b0ed44d0d31fd70bffe304e0a4f260810200d71820d70b1fed44d0d31fd3ffd15112baf2a122f901541044f910f2a2f80001d31f3120d74a96d307d402fb00ded1a4c8cb1fcbffc9ed54b5b86e42")).roots.first()
			println(TvmDisassembler.fromCode(code))
		}
	}

}
