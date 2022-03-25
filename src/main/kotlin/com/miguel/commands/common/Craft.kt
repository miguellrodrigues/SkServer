package com.miguel.commands.common

import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

class Craft : BukkitCommand("craft") {

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true

        sender.openWorkbench(null, true)

        return false
    }
}