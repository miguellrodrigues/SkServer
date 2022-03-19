package com.miguel.commands.common

import com.miguel.game.market.MarketManager
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import java.util.*

class Anuncio : BukkitCommand("anuncio") {

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage("Comando apenas para jogadores")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("§c/anuncio [criar | remover] [nome] [preço]")
        } else {
            println(args[1])
            when (args.size) {
                2 -> {
                    if (args[0].lowercase(Locale.getDefault()) == "remover") {
                        val name = args[1]

                        MarketManager.removeAd(sender, name)
                    } else {
                        sender.sendMessage("§c/anuncio [remover] [nome] [preço]")
                    }
                }

                3 -> {
                    if (args[0].lowercase(Locale.getDefault()) == "criar") {
                        val name = args[1]

                        val price: Double

                        try {
                            price = args[2].toDouble()
                        } catch (e: NumberFormatException) {
                            sender.sendMessage("§cUtilize apenas números no preço !")
                            return true
                        }

                        if (price <= Double.MAX_VALUE) {
                            val type = sender.inventory.itemInMainHand.type

                            if (type == Material.AIR) {
                                sender.sendMessage("§cSegure um item válido na sua mão principal")
                                return true
                            }

                            var n = name.replace("-", " ")

                            MarketManager.advertise(sender, n, price)
                        }
                    } else {
                        sender.sendMessage("§c/anuncio [criar] [nome] [preço]")
                    }
                }

                else -> {
                    sender.sendMessage("§c/anuncio [criar | remover] [nome] [preço]")
                }
            }
        }

        return false
    }
}