package com.miguel.commands.common

import com.miguel.common.command.Permission
import com.miguel.game.bank.BankManager
import com.miguel.game.manager.PlayerManager
import com.miguel.values.Strings
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

class Banco : BukkitCommand("banco") {

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Comando apenas para jogadores")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("§f/banco §ainfo")
            sender.sendMessage("§f/banco §asacar §evalor")
            sender.sendMessage("§f/banco §adepositar")
            sender.sendMessage("§f/banco §atransferir §enome§f/§econta §fvalor")
        } else {
            val inventory = sender.inventory

            when (args.size) {
                1 -> {
                    val option = args[0]

                    when (option.lowercase(Locale.getDefault())) {
                        "info" -> {
                            sender.sendMessage(" ")
                            sender.sendMessage(
                                "${Strings.MESSAGE_PREFIX} §fID da sua conta§e: §a${
                                    PlayerManager.getAccountId(
                                        sender.uniqueId
                                    )
                                }"
                            )

                            sender.sendMessage(
                                "${Strings.MESSAGE_PREFIX} Seu saldo é de §e${
                                    PlayerManager.getBalance(
                                        sender.uniqueId
                                    )
                                } §aUkranianinho`s"
                            )

                            sender.sendMessage(" ")
                        }

                        "depositar" -> {
                            BankManager.deposit(sender, inventory.itemInMainHand)
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

                            BankManager.withDraw(sender, value)
                        }

                        "imprimir" -> {
                            if (Permission.has(Permission.BANK_OWN, sender)) {
                                val value: Double

                                try {
                                    value = args[1].toDouble()
                                } catch (e: NumberFormatException) {
                                    sender.sendMessage("§cUtilize apenas números no valor de impressão !")
                                    return true
                                }

                                if (value <= 0 || value > Double.MAX_VALUE) {
                                    sender.sendMessage("§cValor inválido !")
                                    return true
                                }

                                BankManager.print(sender, value)
                            }
                        }

                        "depositar" -> {
                            val itemInMainHand = inventory.itemInMainHand

                            when (args[1]) {
                                "." -> {
                                    if (BankManager.currencyExist(itemInMainHand.type)) {
                                        val contents = inventory.contents?.filterNotNull()

                                        val filtered = contents
                                            ?.filter { it.type == itemInMainHand.type }

                                        if (filtered != null && filtered.isNotEmpty()) {
                                            filtered.forEach {
                                                inventory.setItem(inventory.indexOf(it), ItemStack(Material.AIR))
                                            }

                                            val deposit = BankManager.deposit(sender, filtered.toTypedArray())

                                            sender.sendMessage("${Strings.PREFIX} §fVocê depositou §e$deposit §aUkranianinho's")
                                        }
                                    } else {
                                        sender.sendMessage("§cMoeda inválida !")
                                    }
                                }

                                "*" -> {
                                    val contents = inventory.contents?.filterNotNull()

                                    val items = contents
                                        ?.filter { BankManager.currencyExist(it.type) }?.toTypedArray()

                                    if (items != null) {
                                        val deposit = BankManager.deposit(
                                            sender, items
                                        )

                                        items.forEach { inventory.setItem(inventory.indexOf(it), ItemStack(Material.AIR)) }

                                        if (deposit != .0) {
                                            sender.sendMessage("${Strings.PREFIX} §fVocê depositou §e$deposit §aUkranianinho's")
                                        }
                                    }
                                }
                            }
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
                                if (credited == sender) {
                                    sender.sendMessage("§cVocê não pode realizar transferências para si mesmo !")
                                    return true
                                }

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

                                if (BankManager.transfer(credited, sender, value)) {
                                    sender.sendMessage("${Strings.PREFIX} §fVocê transferiu §e$value §aUkranianinhos §fpara a conta §b${credited}")
                                    credited.sendMessage("${Strings.PREFIX} §fVocê recebeu §e$value §aUkranianinhos §fdo jogador §b${sender.name}")
                                } else {
                                    sender.sendMessage("§cSaldo insuficiente !")
                                }
                            } else {
                                val creditedAccount = args[1]
                                val value: Double

                                try {
                                    value = args[2].toDouble()
                                } catch (e: java.lang.NumberFormatException) {
                                    sender.sendMessage("§cValor inválido !")
                                    return true
                                }

                                if (creditedAccount == PlayerManager.getAccountId(sender.uniqueId)) {
                                    sender.sendMessage("§cVocê não pode realizar transferências para si mesmo !")
                                    return true
                                }

                                if (value <= 0) {
                                    sender.sendMessage("§cValor inválido !")
                                    return true
                                }

                                if (BankManager.transfer(creditedAccount, sender, value)) {
                                    sender.sendMessage("${Strings.PREFIX} §fVocê transferiu §e$value §aUkranianinhos §fpara a conta §b${creditedAccount}")
                                }
                            }
                        }

                        "injetar" -> {
                            if (Permission.has(Permission.BANK_OWN, sender)) {
                                val toInject = Bukkit.getPlayer(args[1])

                                if (toInject != null) {
                                    val value: Double

                                    try {
                                        value = args[2].toDouble()
                                    } catch (e: NumberFormatException) {
                                        sender.sendMessage("§cUtilize apenas números no valor de transferência !")
                                        return true
                                    }

                                    if (value > Double.MAX_VALUE) {
                                        sender.sendMessage("§cValor inválido !")
                                        return true
                                    }

                                    BankManager.deposit(toInject.uniqueId, value)
                                    sender.sendMessage("${Strings.PREFIX} §fVocê injetou §e$value §aUkranianinhos §fna conta do jogador §b${toInject.name}")
                                } else {
                                    val creditedAccount = args[1]
                                    val value: Double

                                    try {
                                        value = args[2].toDouble()
                                    } catch (e: java.lang.NumberFormatException) {
                                        sender.sendMessage("§cValor inválido(s) !")
                                        return true
                                    }

                                    if (value > Double.MAX_VALUE) {
                                        sender.sendMessage("§cValor inválido !")
                                        return true
                                    }

                                    BankManager.inject(creditedAccount, value, sender)
                                }
                            }
                        }
                    }
                }

                else -> {

                }
            }
        }

        return false
    }
}