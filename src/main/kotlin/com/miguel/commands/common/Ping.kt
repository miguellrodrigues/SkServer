package com.miguel.commands.common

import com.miguel.values.Strings
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

class Ping : BukkitCommand("ping") {

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage("Comando apenas para jogadores")
            return true
        }

        sender.sendMessage(
            "${Strings.PREFIX} §fSeu ping é de §a${sender.ping} §ems"
        )

        return false
    }
}