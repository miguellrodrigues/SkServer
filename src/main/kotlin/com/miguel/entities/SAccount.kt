package com.miguel.entities

data class SAccount(
    val id: Int = 0,
    var balance: Double = .0
) {
    override fun toString(): String {
        return "SAccount(id=$id, balance=$balance)"
    }
}