package com.miguel.commands.common

import com.miguel.common.command.Permission
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

class Invsee : BukkitCommand("invsee") {

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage("Comando apenas para jogadores")
            return true
        }

        if (!sender.hasPermission(Permission.INVSEE.node)) {
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("§c/invsee [player]")
        } else {
            when (args.size) {
                1 -> {
                    val target = Bukkit.getPlayer(args[0])

                    if (target != null) {
                        sender.openInventory(target.inventory)
                    } else {
                        sender.sendMessage("§cJogador Inválido !")
                    }

                }

                else -> {
                    sender.sendMessage("§c/invsee [player]")
                }
            }
        }

        return false
    }
}