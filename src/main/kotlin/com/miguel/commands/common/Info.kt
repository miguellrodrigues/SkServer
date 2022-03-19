package com.miguel.commands.common

import com.miguel.common.command.Permission
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand

class Info : BukkitCommand("info") {

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission(Permission.INFO.node)) {
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("§c/info [player]")
        } else {
            when (args.size) {
                1 -> {
                    val player = Bukkit.getPlayer(args[0])

                    if (player != null) {
                        sender.sendMessage(" ")
                        sender.sendMessage("§e§l${player.name}§r§7:")
                        sender.sendMessage(" ")
                        sender.sendMessage("§7- §eUUID: §7${player.uniqueId}")
                        sender.sendMessage("§7- §ePing: §7${player.ping}")
                        sender.sendMessage("§7- §eGamemode: §7${player.gameMode.name}")
                        sender.sendMessage("§7- §eWorld: §7${player.world.name}")
                        sender.sendMessage("§7- §eLocation: §7${player.location.x.toInt()}, ${player.location.y.toInt()}, ${player.location.z.toInt()}")
                        sender.sendMessage(" ")
                    }
                }

                else -> {
                    sender.sendMessage("§c/info [player]")
                }
            }
        }

        return false
    }
}