package com.miguel.game.manager

import com.miguel.packets.NMSUtil
import net.minecraft.server.v1_16_R3.IChatBaseComponent
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerListHeaderFooter
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object GameManager {

    fun getPlayers(): List<Player> {
        return Bukkit.getOnlinePlayers().toList()
    }

    fun sendMessage(message: String) {
        getPlayers().forEach { it.sendMessage(message) }
    }

    fun sendTab(player: Player, header: String, footer: String) {
        val packetHeader = IChatBaseComponent.ChatSerializer.a("{\"text\": \"$header\"}")
        val packetFooter = IChatBaseComponent.ChatSerializer.a("{\"text\": \"$footer\"}")

        val packet = PacketPlayOutPlayerListHeaderFooter()

        NMSUtil.setValue(packet, "header", packetHeader!!)
        NMSUtil.setValue(packet, "footer", packetFooter!!)

        (player as CraftPlayer).handle.networkManager.sendPacket(packet)
    }

    fun formatTime(i: Int): String {
        val a: Int = i / 60
        val b: Int = i % 60

        val d: String = if (b >= 10) "$b" else "0$b"

        return String.format("%s %s", "${a}m", "${d}s")
    }

    fun createItem(name: String, description: Array<String>, type: Material): ItemStack {
        val stack = ItemStack(type)

        val itemMeta = stack.itemMeta!!
        itemMeta.setDisplayName(name)
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)

        if (description.isNotEmpty())
            itemMeta.lore = description.toMutableList()

        stack.itemMeta = itemMeta

        return stack
    }

    fun createItem(name: String, type: Material, data: Byte): ItemStack {
        val stack = ItemStack(type)

        if (data >= 0) {
            stack.data = type.getNewData(data)
            stack.durability = data.toShort()
        }

        val itemMeta = stack.itemMeta!!
        itemMeta.setDisplayName(name)
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)

        stack.itemMeta = itemMeta

        return stack
    }

    fun createItem(name: String, type: Material): ItemStack {
        val stack = ItemStack(type)

        val itemMeta = stack.itemMeta!!
        itemMeta.setDisplayName(name)
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)

        stack.itemMeta = itemMeta

        return stack
    }

}