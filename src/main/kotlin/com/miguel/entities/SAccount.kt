package com.miguel.entities

data class SAccount(
    var id: String,
    var balance: Double
) {
    override fun toString(): String {
        return "SAccount(id=$id, balance=$balance)"
    }
}