package com.miguel.game.manager

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.miguel.Main
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
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
        val packet = PacketContainer(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER)

        packet.chatComponents.write(0, WrappedChatComponent.fromJson("{\"text\": \"$header\"}"))
        packet.chatComponents.write(1, WrappedChatComponent.fromJson("{\"text\": \"$footer\"}"))

        Main.PROTOCOL_MANAGER.sendServerPacket(player, packet)
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
        itemMeta.displayName(Component.text(name))
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)

        if (description.isNotEmpty())
            itemMeta.lore(description.map { Component.text(it) })

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