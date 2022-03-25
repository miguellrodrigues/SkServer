package com.miguel.commands.common

import com.miguel.game.manager.AccountManager
import com.miguel.game.manager.GameManager
import com.miguel.game.manager.PlayerManager
import com.miguel.values.Values
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Blocar : BukkitCommand("blocar") {

    private val directions = listOf("+x", "+y", "+z", "-x", "-y", "-z")
    private val pricePerBlock = .01

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true

        if (args.isEmpty()) {
            sender.sendMessage("§cUse: /blocar <quantidade> <direção>")
            return true
        }

        if (args.size != 2) {
            sender.sendMessage("§cUse: /blocar <quantidade> <direção>")
            return true
        }

        if (!directions.contains(args[1])) {
            sender.sendMessage("§cDireção inválida")
            return true
        }

        val quantity = args[0].toIntOrNull()
        if (quantity == null || quantity < 2) {
            sender.sendMessage("§cQuantidade inválida")
            return true
        }

        val direction = args[1]

        val item = sender.inventory.itemInMainHand

        if (item.type == Material.AIR) {
            sender.sendMessage("§cVocê precisa de um bloco para usar este comando")
            return true
        }

        val contents = sender.inventory.contents?.filterNotNull()

        val items = contents
            ?.filter { it.type == item.type }!!

        val amountInInventory = items.sumOf { it.amount }

        if (amountInInventory < quantity) {
            sender.sendMessage("§cVocê não possui essa quantidade de blocos")
            return true
        }

        val price = pricePerBlock * quantity

        if (PlayerManager.getBalance(sender.uniqueId) < price) {
            sender.sendMessage("§cVocê não possui dinheiro suficiente")
            return true
        }

        PlayerManager.changeBalance(sender.uniqueId, -price)
        AccountManager.changeBalance(Values.governmentID, price)

        sender.inventory.addItem(
            GameManager.createItem(
                Component.text("§bBlocão"),
                arrayOf(
                    Component.empty(),
                    Component.text(" §fDireção: §e$direction"),
                    Component.text(" §fQuantidade: §e$quantity"),
                    Component.empty()
                ),
                item.type
            )
        )

        sender.inventory.removeItem(ItemStack(item.type, quantity))

        sender.sendMessage("§aBloco mágico criado!")

        return false
    }
}