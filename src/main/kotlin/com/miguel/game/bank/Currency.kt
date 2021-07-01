package com.miguel.game.bank

import org.bukkit.Material

interface ICurrency {
    val material: Material
    val value: Double
}

data class Currency(
    override val material: Material,
    override val value: Double
) : ICurrency

data class Amount(
    val material: Material,
    val amount: Int
)