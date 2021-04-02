package com.miguel.game.bank

import com.miguel.data.PlayerData
import com.miguel.game.manager.GameManager
import com.miguel.values.Strings
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList

object BankManager {

    private val currencies = ArrayList<Currency>()

    fun loadCurrencies() {
        currencies.add(Currency(Material.EMERALD, 250.0F))
        currencies.add(Currency(Material.DIAMOND, 100.0F))
        currencies.add(Currency(Material.GOLD_INGOT, 10.0F))
        currencies.add(Currency(Material.GOLD_NUGGET, 1.0F))
        currencies.add(Currency(Material.IRON_INGOT, .5F))
        currencies.add(Currency(Material.IRON_NUGGET, .1F))

        currencies.sortBy { it.value }
        currencies.reverse()
    }

    fun currencyExist(material: Material): Boolean {
        val currency = currencies.firstOrNull { it.material == material }
        return currency != null
    }

    fun deposit(player: Player, items: Array<ItemStack>) {
        val data = PlayerData.getData(player.uniqueId) ?: return

        items.forEach { item ->
            val value = currencies.first { it.material == item.type }.value * item.amount

            data.money += value
        }
    }

    fun deposit(player: Player, item: ItemStack) {
        if (currencyExist(item.type)) {
            val value = currencies.first { it.material == item.type }.value * item.amount

            val data = PlayerData.getData(player.uniqueId) ?: return
            data.money += value

            player.sendMessage("${Strings.MESSAGE_PREFIX} Você depositou §e${value} §aUkranianinhos")

            player.inventory.setItem(player.inventory.indexOf(item), ItemStack(Material.AIR))
        } else {
            player.sendMessage("§cMoeda inválida !")
        }
    }

    fun withDraw(player: Player, value: Float) {
        val data = PlayerData.getData(player.uniqueId) ?: return

        if (data.money >= value) {
            val decompose = decompose(value, player.uniqueId)

            decompose.forEach { amount ->
                val currencyValue = currencies.first { it.material == amount.material }.value

                val item = GameManager.createItem("§aUkranianinho", arrayOf(" ", " §f- Valor §e${currencyValue} §fUkranianinho`s", " "), amount.material)
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

    fun deposit(uuid: UUID, value: Float) {
        val data = PlayerData.getData(uuid) ?: return
        data.money += value
    }

    fun withDraw(uuid: UUID, value: Float): Boolean {
        val data = PlayerData.getData(uuid) ?: return false

        if (data.money >= value) {
            data.money -= value
            return true
        }

        return false
    }

    private fun decompose(value: Float, uuid: UUID): Array<Amount> {
        var v = value

        val quantities = ArrayList<Amount>()

        var wd = 0.0F

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