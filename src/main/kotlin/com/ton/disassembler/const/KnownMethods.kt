package com.ton.disassembler.const

object KnownMethods {
    val map: Map<Int, String> = mapOf(
        0 to "recv_internal",
        -1 to "recv_external",
        -2 to "run_ticktock",
        68445 to "get_nft_content",
        80293 to "get_owner",
        83229 to "owner",
        85143 to "seqno",
        85719 to "royalty_params",
        90228 to "get_editor",
        92067 to "get_nft_address_by_index",
        97026 to "get_wallet_data",
        102351 to "get_nft_data",
        102491 to "get_collection_data",
        103289 to "get_wallet_address",
        106029 to "get_jetton_data",
    )
}
