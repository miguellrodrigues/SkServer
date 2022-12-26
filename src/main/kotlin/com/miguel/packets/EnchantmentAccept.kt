package com.miguel.packets

import org.bukkit.enchantments.Enchantment

object EnchantmentAccept {

    fun accept() {
        val enchantment = Enchantment::class.java

        val filed = enchantment.getDeclaredField("acceptingNew")
        filed.isAccessible = true
        filed.set(null, true)
        filed.isAccessible = false
    }
}