package com.miguel.entities

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

data class SAd(
    val id: Int = 0,
    val advertiserName: String,
    val name: String,
    val price: Double,
    val amount: Int,
    val material: String,
    val account_id: Int,
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

    override fun toString(): String {
        return "SAd(id=$id, advertiserName='$advertiserName', name='$name', price=$price, amount=$amount, material='$material', account_id=$account_id, delete=$delete)"
    }
}
