package com.miguel.commands.common

import com.miguel.game.manager.PlayerManager
import com.miguel.game.market.MarketManager
import com.miguel.values.Strings
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

class Anuncio : BukkitCommand("anuncio") {

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage("Comando apenas para jogadores")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("§c/anuncio [criar | remover] [preço] [nome]")
        } else {
            when (args.size) {
                2 -> {
                    if (args[0].lowercase(Locale.getDefault()) == "remover") {
                        val name = args[1]

                        val item = MarketManager.removeAd(sender.name, name)

                        if (item != null) {
                            sender.sendMessage("§fAnúncio §e${name} §fremovido com sucesso")
                            sender.inventory.addItem(item)
                        } else {
                            sender.sendMessage("§cAnúncio não encontrado !")
                        }
                    } else {
                        sender.sendMessage("§c/anuncio [remover] [nome]")
                        sender.sendMessage("§c/anuncio [criar] [preço] [nome]")
                    }
                }

                else -> {
                    if (args[0].lowercase(Locale.getDefault()) == "criar") {
                        val price: Double

                        try {
                            price = args[1].toDouble()
                        } catch (e: NumberFormatException) {
                            sender.sendMessage("§cUtilize apenas números no preço !")
                            return true
                        }

                        val name = args.drop(2).joinToString(" ")

                        if (MarketManager.getByOwner(sender.name).firstOrNull { it.name == name } != null) {
                            sender.sendMessage("§cJá existe um anúncio com esse nome !")
                            return true
                        }

                        if (price <= Double.MAX_VALUE) {
                            val type = sender.inventory.itemInMainHand.type

                            if (type == Material.AIR) {
                                sender.sendMessage("§cSegure um item válido na sua mão principal")
                                return true
                            }

                            val item = sender.inventory.itemInMainHand

                            MarketManager.advertise(
                                sender.name,
                                PlayerManager.getAccountId(sender.uniqueId),
                                name,
                                price,
                                item
                            )

                            sender.inventory.setItemInMainHand(ItemStack(Material.AIR))

                            sender.sendMessage(" ")
                            sender.sendMessage("${Strings.MARKET_PREFIX} Anúncio criado com sucesso !")
                            sender.sendMessage(" ")

                            sender.sendMessage(" ")
                            sender.sendMessage("${Strings.MARKET_PREFIX} Você irá receber ${price - MarketManager.taxPercentage * price} Ukranianinho's devido ao imposto de 5%")
                            sender.sendMessage(" ")
                        }
                    } else {
                        sender.sendMessage("§c/anuncio [remover] [nome]")
                        sender.sendMessage("§c/anuncio [criar] [preço] [nome]")
                    }
                }
            }
        }

        return false
    }
}