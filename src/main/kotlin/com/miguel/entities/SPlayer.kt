package com.miguel.entities

import java.util.*


data class SPlayer(
    val uuid: UUID,
    val account: SAccount,
    var homes: ArrayList<SHome>
) {
    override fun toString(): String {
        return "SPlayer(uuid=$uuid, account=$account, homes=$homes)"
    }
}