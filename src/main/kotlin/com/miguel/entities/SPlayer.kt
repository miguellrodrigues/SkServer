package com.miguel.entities

import java.util.*


data class SPlayer(
    val id: Int = 0,
    val uuid: UUID,
    val account: SAccount,
    var homes: ArrayList<SHome>
) {
    override fun toString(): String {
        return "SPlayer(id=$id, uuid=$uuid, account=$account, homes=$homes)"
    }
}