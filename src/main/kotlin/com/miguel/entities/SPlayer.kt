package com.miguel.entities

import java.util.*

data class SPlayer(
    val id: Int = 0,
    val uuid: UUID,
    val account: SAccount,
    val homes: List<SHome>
)