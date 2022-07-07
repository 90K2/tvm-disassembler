package org.ton.disassembler.const

object KnownMethods {
    fun get(code: Int): String? {
        return when(code) {
            0 -> "recv_internal"
            -1 -> "recv_external"
            -2 -> "run_ticktock"
            68445 -> "get_nft_content"
            80293 -> "get_owner"
            83229 -> "owner"
            85143 -> "seqno"
            85719 -> "royalty_params"
            90228 -> "get_editor"
            92067 -> "get_nft_address_by_index"
            97026 -> "get_wallet_data"
            102351 -> "get_nft_data"
            102491 -> "get_collection_data"
            103289 -> "get_wallet_address"
            106029 -> "get_jetton_data"
            else -> null
        }
    }
}
