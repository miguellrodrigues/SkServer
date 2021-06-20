package com.miguel.entities

import java.util.*

class SPlayer(
    val uuid: UUID,
    var account: SAccount,
    var homes: List<SHome>,
) {
}