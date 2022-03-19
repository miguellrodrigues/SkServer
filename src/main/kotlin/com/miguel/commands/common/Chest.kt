package com.miguel.commands.common

import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

class Chest : BukkitCommand("chest") {

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true

        sender.openInventory(sender.enderChest)

        return false
    }
}