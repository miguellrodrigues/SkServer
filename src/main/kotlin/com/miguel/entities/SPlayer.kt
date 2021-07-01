package com.miguel.entities

import java.util.*

data class SPlayer(
    val uuid: UUID,
    val account: SAccount,
    val homes: List<SHome>
)