package com.miguel.game.market

import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.*

interface IMarketAd {
    val id: Int

    val advertiser: UUID
    val advertiserName: String

    val name: String
    val price: Float
    val amount: Int

    val material: String
}

data class MarketAd(
    override val id: Int,
    override val advertiser: UUID,
    override val advertiserName: String,
    override val name: String,
    override val price: Float,
    override val amount: Int,
    override val material: String
) : IMarketAd {

    override fun equals(other: Any?): Boolean {
        if (other is MarketAd) {
            return other.id == this.id &&
                    other.advertiser == this.advertiser &&
                    other.advertiserName == this.advertiserName &&
                    other.name == this.name &&
                    other.price == this.price &&
                    other.amount == this.amount &&
                    other.material == this.material
        }

        return super.equals(other)
    }

    fun icon(): ItemStack {
        val stack = ItemStack(Material.getMaterial(material)!!)

        val itemMeta = stack.itemMeta!!

        itemMeta.setDisplayName("§a${name}")
        itemMeta.lore = arrayListOf(
            " ",
            " §fAnunciante ↣ §e${advertiserName}",
            " §fPreço ↣ §e${price} §aUkranianinho`s",
            " §fQuantidade ↣ §e${amount}",
            " ",
            " §aClique para comprar §f!",
            " ",
            " §fID §e${id}"
        )

        itemMeta.itemFlags.addAll(ItemFlag.values())
        stack.itemMeta = itemMeta

        return stack
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + advertiser.hashCode()
        result = 31 * result + advertiserName.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + amount
        result = 31 * result + material.hashCode()
        return result
    }
}