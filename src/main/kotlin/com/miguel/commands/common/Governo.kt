package com.miguel.commands.common

import com.miguel.common.command.Permission
import com.miguel.game.bank.BankManager
import com.miguel.game.manager.AccountManager
import com.miguel.game.manager.PlayerManager
import com.miguel.values.Strings
import com.miguel.values.Values
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
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

                                BankManager.transfer(credited, sender, value)
                            } else {
                                val creditedAccount = args[1]
                                val value: Double

                                try {
                                    value = args[2].toDouble()
                                } catch (e: java.lang.NumberFormatException) {
                                    sender.sendMessage("§cValor inválido(s) !")
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

                                BankManager.transfer(creditedAccount, sender, value)
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