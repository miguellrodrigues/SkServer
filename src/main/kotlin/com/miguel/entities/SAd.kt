package com.miguel.entities

import com.miguel.game.manager.GameManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

data class SAd(
    val id: Int = 0,
    val advertiserName: String,
    val name: String,
    val price: Double,
    val item: ByteArray,
    val account_id: String,
    var delete: Boolean = false
) {

    fun icon(): ItemStack {
        val adStack = GameManager.deserializeItem(item)

        val stack = ItemStack(adStack.type)
        adStack.enchantments.forEach { (t, u) ->
            stack.addEnchantment(t, u)
        }

        val itemMeta = stack.itemMeta!!

        itemMeta.displayName(Component.text(name, NamedTextColor.WHITE))

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SAd

        if (id != other.id) return false
        if (advertiserName != other.advertiserName) return false
        if (name != other.name) return false
        if (price != other.price) return false
        if (!item.contentEquals(other.item)) return false
        if (account_id != other.account_id) return false
        if (delete != other.delete) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + advertiserName.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + item.contentHashCode()
        result = 31 * result + account_id.hashCode()
        result = 31 * result + delete.hashCode()
        return result
    }
}
