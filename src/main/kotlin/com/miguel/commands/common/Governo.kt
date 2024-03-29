package com.miguel.commands.common

import com.miguel.common.command.Permission
import com.miguel.game.bank.BankManager
import com.miguel.game.manager.AccountManager
import com.miguel.game.manager.PlayerManager
import com.miguel.game.market.MarketManager
import com.miguel.values.Strings
import com.miguel.values.Values
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

class Governo : BukkitCommand("governo") {

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Comando apenas para jogadores")
            return true
        }

        if (!sender.hasPermission(Permission.GOVERNO.node)) {
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("§f/governo §ainfo")
            sender.sendMessage("§f/governo §asacar §evalor")
            sender.sendMessage("§f/governo §atransferir §enome§f/§econta §fvalor")
            sender.sendMessage("§f/governo §aanuncio §ecriar §fvalor §bnome")
            sender.sendMessage("§f/governo §aanuncio §eremover §bnome")
            sender.sendMessage("§f/governo §aenchant §eencantamento §bnivel")
        } else {
            when (args.size) {
                1 -> {
                    val option = args[0]

                    when (option.lowercase(Locale.getDefault())) {
                        "info" -> {
                            sender.sendMessage(" ")
                            sender.sendMessage(
                                "${Strings.MESSAGE_PREFIX} §fID da conta governamental§e: §a${
                                    Values.governmentID
                                }"
                            )

                            sender.sendMessage(
                                "${Strings.MESSAGE_PREFIX} O saldo da conta governamental é de §e${
                                    AccountManager.getBalance(
                                        Values.governmentID
                                    )
                                } §aUkranianinho`s"
                            )

                            sender.sendMessage(" ")
                        }

                        else -> {

                        }
                    }
                }

                2 -> {
                    val option = args[0]

                    when (option.lowercase(Locale.getDefault())) {
                        "sacar" -> {
                            val value: Double

                            try {
                                value = args[1].toDouble()
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("§cUtilize apenas números no valor de saque !")
                                return true
                            }

                            if (value <= 0 || value > Double.MAX_VALUE) {
                                sender.sendMessage("§cValor inválido !")
                                return true
                            }

                            BankManager.withDraw(sender, Values.governmentID, value)
                        }

                        "comprar" -> {
                            val id: Int

                            try {
                                id = args[1].toInt()
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("§cUtilize apenas números no ID do anúncio !")
                                return true
                            }

                            if (id <= 0) {
                                sender.sendMessage("§cID inválido !")
                                return true
                            }

                            sender.sendMessage(MarketManager.purchase(
                                Values.governmentID,
                                id,
                                sender.inventory
                            ))
                        }

                        else -> {
                        }
                    }
                }

                3 -> {
                    val option = args[0]

                    when (option.lowercase(Locale.getDefault())) {
                        "transferir" -> {
                            val credited = Bukkit.getPlayer(args[1])

                            if (credited != null) {
                                val value: Double

                                try {
                                    value = args[2].toDouble()
                                } catch (e: NumberFormatException) {
                                    sender.sendMessage("§cUtilize apenas números no valor de transferência !")
                                    return true
                                }

                                if (value <= 0 || value > Double.MAX_VALUE) {
                                    sender.sendMessage("§cValor inválido !")

                                    return true
                                }

                                if (BankManager.transfer(credited, Values.governmentID, value)) {
                                    credited.sendMessage("${Strings.PREFIX} §fVocê recebeu §e$value §aUkranianinhos §fdo §aGoverno")
                                    sender.sendMessage("${Strings.PREFIX} §fTransferência realizada com sucesso!")
                                } else {
                                    sender.sendMessage("${Strings.PREFIX} §cSaldo insuficiente!")
                                }

                            } else {
                                val creditedAccount = args[1]
                                val value: Double

                                try {
                                    value = args[2].toDouble()
                                } catch (e: java.lang.NumberFormatException) {
                                    sender.sendMessage("§cValor inválido(s) !")
                                    return true
                                }

                                if (creditedAccount == Values.governmentID) {
                                    sender.sendMessage("§cVocê não pode realizar transferências do Governo para o Governo !")
                                    return true
                                }

                                if (value <= 0) {
                                    sender.sendMessage("§cValor inválido !")
                                    return true
                                }

                                if (BankManager.transfer(creditedAccount, Values.governmentID, value)) {
                                    sender.sendMessage("${Strings.PREFIX} §fTransferência realizada com sucesso!")
                                }
                            }
                        }

                        "anuncio" -> {
                            if (args[1].lowercase(Locale.getDefault()) == "remover") {
                                val name = args[2]

                                val item = MarketManager.removeAd("Governo", name)

                                if (item != null) {
                                    sender.sendMessage("§fAnúncio §e${name} §fremovido com sucesso")
                                    sender.inventory.addItem(item)
                                } else {
                                    sender.sendMessage("§cAnúncio não encontrado !")
                                }
                            }
                        }

                        "enchant" -> {
                            val enchantmentName = args[1]
                            val enchantment = Enchantment.getByName(enchantmentName.uppercase(Locale.getDefault()))

                            if (enchantment == null) {
                                sender.sendMessage("§cEncantamento inválido !")
                                return true
                            }

                            val level: Int

                            try {
                                level = args[2].toInt()
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("§cUtilize apenas números no nível do encantamento !")
                                return true
                            }

                            if (level <= 0) {
                                sender.sendMessage("§cNível inválido !")
                                return true
                            }

                            val item = sender.inventory.itemInMainHand

                            if (item.type == Material.AIR) {
                                sender.sendMessage("§cVocê precisa estar segurando um item !")
                                return true
                            }

                            item.addUnsafeEnchantment(enchantment, level)
                            sender.inventory.setItemInMainHand(item)
                            sender.sendMessage("§fEncantamento §e${enchantmentName} §fadicionado com sucesso !")
                        }
                    }
                }

                else -> {
                    if (args[0].lowercase(Locale.getDefault()) == "anuncio") {
                        if (args[1].lowercase(Locale.getDefault()) == "criar") {
                            val price: Double

                            try {
                                price = args[2].toDouble()
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("§cUtilize apenas números no preço !")
                                return true
                            }

                            val name = args.drop(3).joinToString(" ")

                            if (MarketManager.getByOwner("Governo").firstOrNull { it.name == name } != null) {
                                sender.sendMessage("§cJá existe um anúncio com esse nome !")
                                return true
                            }

                            if (price <= Double.MAX_VALUE) {
                                val type = sender.inventory.itemInMainHand.type

                                if (type == Material.AIR) {
                                    sender.sendMessage("§cSegure um item válido na sua mão principal")
                                    return true
                                }

                                val item = sender.inventory.itemInMainHand.clone()

                                MarketManager.advertise(
                                    "Governo",
                                    Values.governmentID,
                                    name,
                                    price,
                                    item
                                )

                                sender.inventory.setItemInMainHand(ItemStack(Material.AIR))

                                sender.sendMessage(" ")
                                sender.sendMessage("${Strings.MARKET_PREFIX} Anúncio criado com sucesso !")
                                sender.sendMessage(" ")
                            }
                        } else if (args[1].lowercase(Locale.getDefault()) == "remover") {
                            val name = args.drop(2).joinToString(" ")

                            val item = MarketManager.removeAd("Governo", name)

                            if (item != null) {
                                sender.sendMessage("§fAnúncio §e${name} §fremovido com sucesso")
                                sender.inventory.addItem(item)
                            } else {
                                sender.sendMessage("§cAnúncio não encontrado !")
                            }
                        }
                    } else {
                        sender.sendMessage("§c/governo anuncio [remover] [nome]")
                        sender.sendMessage("§c/governo anuncio [criar] [preço] [nome]")
                    }
                }
            }
        }

        return false
    }
}