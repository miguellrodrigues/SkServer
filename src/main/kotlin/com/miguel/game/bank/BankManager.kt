package com.miguel.game.bank

import com.miguel.game.manager.GameManager
import com.miguel.game.manager.PlayerManager
import com.miguel.values.Strings
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

object BankManager {

    private val currencies = ArrayList<Currency>()

    fun loadCurrencies() {
        currencies.add(Currency(Material.EMERALD, 250.0))
        currencies.add(Currency(Material.DIAMOND, 100.0))
        currencies.add(Currency(Material.GOLD_INGOT, 10.0))
        currencies.add(Currency(Material.GOLD_NUGGET, 1.0))
        currencies.add(Currency(Material.IRON_INGOT, .5))
        currencies.add(Currency(Material.IRON_NUGGET, .1))

        currencies.sortBy { it.value }
        currencies.reverse()
    }

    fun currencyExist(material: Material): Boolean {
        val currency = currencies.firstOrNull { it.material == material }
        return currency != null
    }

    fun deposit(player: Player, items: Array<ItemStack>) {
        var money = .0

        items.forEach { item ->
            val value = currencies.first { it.material == item.type }.value * item.amount

            money += value
        }

        PlayerManager.setBalance(player.uniqueId, money)
    }

    fun deposit(player: Player, item: ItemStack) {
        if (currencyExist(item.type)) {
            val value = currencies.first { it.material == item.type }.value * item.amount

            PlayerManager.increaseBalance(player.uniqueId, value)

            player.sendMessage("${Strings.MESSAGE_PREFIX} Você depositou §e${value} §aUkranianinhos")

            player.inventory.setItem(player.inventory.indexOf(item), ItemStack(Material.AIR))
        } else {
            player.sendMessage("§cMoeda inválida !")
        }
    }

    fun withDraw(player: Player, value: Double) {
        val balance = PlayerManager.getBalance(player.uniqueId)

        if (balance >= value) {
            val decompose = decompose(balance, player.uniqueId)

            decompose.forEach { amount ->
                val currencyValue = currencies.first { it.material == amount.material }.value

                val item = GameManager.createItem(
                    "§aUkranianinho",
                    arrayOf(" ", " §f- Valor §e${currencyValue} §fUkranianinho`s", " "),
                    amount.material
                )
                item.amount = amount.amount

                if (player.inventory.firstEmpty() != -1) {
                    player.inventory.addItem(
                        item
                    )
                } else {
                    if (player.enderChest.firstEmpty() != -1) {
                        player.enderChest.addItem(item)
                    } else {
                        player.world.dropItem(player.location, item)
                    }
                }
            }

            player.sendMessage("§aSaque realizado com sucesso !")
        } else {
            player.sendMessage("§cSaldo insuficiente !")
        }
    }

    fun withDraw(uuid: UUID, value: Double): Boolean {
        val balance = PlayerManager.getBalance(uuid)

        if (balance >= value) {
            PlayerManager.decreaseBalance(uuid, value)
            return true
        }

        return false
    }

    private fun decompose(value: Double, uuid: UUID): Array<Amount> {
        var v = value

        val quantities = ArrayList<Amount>()

        var wd = 0.0

        currencies.forEachIndexed { index, currency ->
            quantities.add(
                Amount(
                    currency.material,
                    (v / currency.value).toInt()
                )
            )

            val x = (quantities[index].amount * currency.value)

            v -= x
            wd += x
        }

        withDraw(uuid, wd)

        return quantities.toTypedArray()
    }
}