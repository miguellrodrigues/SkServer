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
import java.util.concurrent.CompletableFuture

class Banco : BukkitCommand("banco") {

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Comando apenas para jogadores")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("§f/banco §a[§binfo§a]")
            sender.sendMessage("§f/banco §a[§bsaldo§a] §a[§fvalor§a]")
            sender.sendMessage("§f/banco §a[§bsaldo§a] §a[§fcreditado/conta§a] §a[§fvalor§a]")
        } else {
            val inventory = sender.inventory

            when (args.size) {
                1 -> {
                    val option = args[0]

                    when (option.lowercase(Locale.getDefault())) {
                        "info" -> {
                            sender.sendMessage(" ")
                            sender.sendMessage("${Strings.MESSAGE_PREFIX} §fID da sua conta§e: §a${PlayerManager.getAccountId(sender)}")

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
                            val withdraw: Double

                            try {
                                withdraw = args[1].toDouble()
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("§cUtilize apenas números no valor de saque !")
                                return true
                            }

                            BankManager.withDraw(sender, withdraw)
                        }

                        "imprimir" -> {
                            if (sender.hasPermission(Permission.BANK_OWN.node)) {
                                val value: Double

                                try {
                                    value = args[1].toDouble()
                                } catch (e: NumberFormatException) {
                                    sender.sendMessage("§cUtilize apenas números no valor de impressão !")
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
                                        val filtered = inventory.contents
                                            .filterNotNull()
                                            .filter { it.type == itemInMainHand.type }

                                        if (filtered.isNotEmpty()) {
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
                                    val items = inventory.contents
                                        .filterNotNull()
                                        .filter { BankManager.currencyExist(it.type) }.toTypedArray()

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

                                BankManager.transfer(credited, sender, value)
                            } else {
                                val creditedAccount: Int
                                val value: Double

                                try {
                                    creditedAccount = args[1].toInt()
                                    value = args[2].toDouble()
                                }catch (e: java.lang.NumberFormatException) {
                                    sender.sendMessage("§cConta e/ou valor inválido(s) !")
                                    return true
                                }

                                if (creditedAccount == PlayerManager.getAccountId(sender)) {
                                    sender.sendMessage("§cVocê não pode realizar transferências para si mesmo !")
                                    return true
                                }

                                CompletableFuture.runAsync {
                                    if (PlayerManager.isValidAccount(creditedAccount).get()) {
                                        BankManager.transfer(creditedAccount, sender, value)
                                    } else {
                                        sender.sendMessage("§cConta inexistente !")
                                    }
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