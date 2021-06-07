package com.miguel.commands.common

import com.miguel.data.PlayerData
import com.miguel.game.bank.BankManager
import com.miguel.values.Strings
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
            sender.sendMessage("§c/banco [saldo | sacar | depositar] [valor]")
        } else {
            val data = PlayerData.getData(sender.uniqueId) ?: return true

            val inventory = sender.inventory
            when (args.size) {
                1 -> {
                    val option = args[0]

                    when (option.lowercase(Locale.getDefault())) {
                        "saldo" -> {
                            sender.sendMessage("${Strings.MESSAGE_PREFIX} Seu saldo é de §e${data.money} §aUkranianinho`s")
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
                            val withdraw: Float

                            try {
                                withdraw = args[1].toFloat()
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("§cUtilize apenas números no valor de saque !")
                                return true
                            }

                            BankManager.withDraw(sender, withdraw)
                        }

                        "depositar" -> {
                            val itemInMainHand = inventory.itemInMainHand

                            if (BankManager.currencyExist(itemInMainHand.type)) {
                                when (args[1]) {
                                    "." -> {
                                        val map = inventory.contents
                                            .filter { it != null && it.type == itemInMainHand.type }
                                            .map { it }

                                        if (map.isNotEmpty()) {
                                            map.forEach {
                                                inventory.setItem(inventory.indexOf(it), ItemStack(Material.AIR))
                                            }

                                            BankManager.deposit(sender, map.toTypedArray())

                                            sender.sendMessage("${Strings.MARKET_PREFIX} Deposito realizado com sucesso !")
                                        }
                                    }

                                    "*" -> {
                                        inventory.contents
                                            .filterNotNull()
                                            .forEach { inventoryItem ->
                                                if (BankManager.currencyExist(inventoryItem.type)) {
                                                    val map = inventory.contents
                                                        .filterNotNull()
                                                        .filter { it.type == inventoryItem.type }
                                                        .map { it }

                                                    if (map.isNotEmpty()) {
                                                        map.forEach {
                                                            inventory.setItem(
                                                                inventory.indexOf(it),
                                                                ItemStack(Material.AIR)
                                                            )
                                                        }

                                                        BankManager.deposit(sender, map.toTypedArray())
                                                    }
                                                }
                                            }

                                        sender.sendMessage("${Strings.MARKET_PREFIX} Deposito realizado com sucesso !")
                                    }
                                }
                            } else {
                                sender.sendMessage("§cMoeda inválida !")
                            }
                        }

                        else -> {
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