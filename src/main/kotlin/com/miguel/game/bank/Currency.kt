package com.miguel.game.bank

import org.bukkit.Material

interface ICurrency {
    val material: Material
    val value: Float
}

data class Currency(
    override val material: Material,
    override val value: Float
) : ICurrency

data class Amount(
    val material: Material,
    val amount: Int
)