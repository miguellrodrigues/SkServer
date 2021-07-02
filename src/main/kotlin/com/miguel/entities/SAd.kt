package com.miguel.entities

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.*

data class SAd(
    val id: Int = 0,
    val advertiserName: String,
    val name: String,
    val price: Double,
    val amount: Int,
    val material: String,
    val player_id: UUID,
    var delete: Boolean = false
) {

    fun icon(): ItemStack {
        val stack = ItemStack(Material.getMaterial(material)!!)

        val itemMeta = stack.itemMeta!!

        itemMeta.displayName(Component.text("§a${name}"))

        val desc = arrayListOf(
            " ",
            " §fAnunciante ↣ §e${advertiserName}",
            " §fPreço ↣ §e${price} §aUkranianinho`s",
            " §fQuantidade ↣ §e${amount}",
            " ",
            " §aClique para comprar §f!",
            " ",
            " §fID §e${id}"
        )

        itemMeta.lore(desc.map { Component.text(it) })

        itemMeta.itemFlags.addAll(ItemFlag.values())
        stack.itemMeta = itemMeta

        return stack
    }
}
