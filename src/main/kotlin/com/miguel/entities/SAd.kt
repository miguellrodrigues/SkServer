package com.miguel.entities

import com.miguel.game.manager.GameManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

data class SAd(
    val id: Int = 0,
    val advertiserName: String,
    val name: String,
    val price: Double,
    val item: String,
    val account_id: Int,
    var delete: Boolean = false
) {

    fun icon(): ItemStack {
        val adStack = GameManager.deserializeItem(item)

        val stack = ItemStack(adStack.type)
        adStack.enchantments.forEach { (t, u) ->
            stack.addEnchantment(t, u)
        }

        val itemMeta = stack.itemMeta!!

        itemMeta.displayName(Component.text("name").color(TextColor.color(0, 255, 0)))

        val desc = arrayListOf(
            " ",
            " §fAnunciante ↣ §e${advertiserName}",
            " §fPreço ↣ §e${price} §aUkranianinho`s",
            " §fQuantidade ↣ §e${adStack.amount}",
            " ",
            " §aClique para comprar §f!",
            " ",
            " §fID §e${id}"
        )

        itemMeta.lore(desc.map { Component.text(it) })

        itemMeta.itemFlags.clear()
        itemMeta.itemFlags.addAll(listOf(ItemFlag.HIDE_ATTRIBUTES))
        stack.itemMeta = itemMeta

        return stack
    }

    override fun toString(): String {
        return "SAd(id=$id, advertiserName='$advertiserName', name='$name', item=$item, price=$price, account_id=$account_id, delete=$delete)"
    }
}
