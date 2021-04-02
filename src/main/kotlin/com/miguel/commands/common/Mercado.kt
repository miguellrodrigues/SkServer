package com.miguel.commands.common

import com.miguel.game.manager.InventoryManager
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

class Mercado : BukkitCommand("mercado") {

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Comando apenas para jogadores")
            return true
        }

        InventoryManager.createInventory(sender, InventoryManager.InventoryType.MARKET)

        return false
    }
}