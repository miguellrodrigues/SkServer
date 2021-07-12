package com.miguel.commands.common

import com.miguel.common.command.Permission
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

class Sumo : BukkitCommand("sumo") {

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage("Comando apenas para jogadores")
            return true
        }

        if (!sender.hasPermission(Permission.SUMO.node)) {
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("§c/sumo [player] [args]")
        } else {
            when (args.size) {
                2 -> {
                    val target = Bukkit.getPlayer(args[0])

                    if (target != null) {
                        sender.performCommand(args[1])
                    }
                }

                else -> {
                    sender.sendMessage("§c/sumo [player] [args]")
                }
            }
        }

        return false
    }
}