package com.miguel.game.bank

import com.miguel.entities.SAccount
import com.miguel.game.manager.AccountManager
import com.miguel.game.manager.GameManager
import com.miguel.game.manager.PlayerManager
import com.miguel.values.Strings
import com.miguel.values.Values
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.concurrent.CompletableFuture

object BankManager {

    private val currencies = ArrayList<Currency>()

    private val coinDisplayName = Component.text("Ukranianinho").color(TextColor.color(0, 255, 0))

    fun init() {
        CompletableFuture.runAsync {
            if (!AccountManager.exist(Values.governmentID).get()) {
                AccountManager.create(
                    SAccount(
                        Values.governmentID,
                        10e3
                    )
                )
            }
        }

        AccountManager.load(Values.governmentID)
    }

    fun loadCurrencies() {
        currencies.add(Currency(Material.NETHERITE_INGOT, 100.0))
        currencies.add(Currency(Material.EMERALD, 10.0))
        currencies.add(Currency(Material.DIAMOND, 5.0))
        currencies.add(Currency(Material.GOLD_INGOT, 1.0))
        currencies.add(Currency(Material.GOLD_NUGGET, .5))
        currencies.add(Currency(Material.IRON_INGOT, .25))
        currencies.add(Currency(Material.IRON_NUGGET, .1))
        currencies.add(Currency(Material.STICK, .01))

        currencies.sortBy { it.value }
        currencies.reverse()
    }

    fun currencyExist(material: Material): Boolean {
        val currency = currencies.firstOrNull { it.material == material }
        return currency != null
    }

    fun deposit(player: Player, items: Array<ItemStack>): Double {
        var amount = .0

        items.forEach { item ->
            val value = currencies.first { it.material == item.type }.value * item.amount

            amount += value
        }

        PlayerManager.changeBalance(player.uniqueId, amount)

        return amount
    }

    fun deposit(player: Player, item: ItemStack) {
        if (currencyExist(item.type) && isValidCurrencyItem(item)) {
            val value = currencies.first { it.material == item.type }.value * item.amount

            PlayerManager.changeBalance(player.uniqueId, value)

            player.sendMessage("${Strings.MESSAGE_PREFIX} Você depositou §e${value} §aUkranianinhos")

            player.inventory.setItemInMainHand(ItemStack(Material.AIR))
        } else {
            player.sendMessage("§cMoeda inválida !")
        }
    }

    fun deposit(uuid: UUID, amount: Double) {
        PlayerManager.changeBalance(uuid, amount)
    }

    /*fun withdraw(uuid: UUID, amount: Double) {
        PlayerManager.changeBalance(uuid, -amount)
    }*/

    private fun deposit(account: String, amount: Double) {
        val player = PlayerManager.isPlayerOnline(account)

        if (player != null) {
            deposit(player.uuid, amount)

            Bukkit.getPlayer(player.uuid)
                ?.sendMessage("${Strings.PREFIX} §fVocê recebeu uma transferência no valor de §e$amount §aUkranianinhos")
        } else {
            PlayerManager.changeBalance(account, amount)
        }
    }

    /*fun withdraw(account: Int, amount: Double) {
        PlayerManager.changeBalance(account, -amount)
    }*/

    fun isValidCurrencyItem(item: ItemStack): Boolean {
        if (!item.hasItemMeta()) return false

        val name = ChatColor.stripColor(
            PlainTextComponentSerializer.plainText().serialize(
                item.itemMeta.displayName() as TextComponent
            )
        )

        return name.equals("Ukranianinho")
    }

    fun withDraw(uuid: UUID, value: Double): Boolean {
        val balance = PlayerManager.getBalance(uuid)

        if (balance >= value) {
            PlayerManager.changeBalance(uuid, -value)
            return true
        }

        return false
    }

    fun withDraw(player: Player, value: Double) {
        val balance = PlayerManager.getBalance(player.uniqueId)

        if (balance >= value) {
            val decompose = decompose(value)

            var recharge = .0

            decompose.forEach { amount ->
                val currencyValue = currencies.first { it.material == amount.material }.value

                val item = GameManager.createItem(
                    coinDisplayName,
                    arrayOf(
                        Component.text(" "),
                        Component.text(" §f- Valor §e$currencyValue §fUkranianinho`s"),
                        Component.text(" ")
                    ),
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
                        recharge += currencyValue * item.amount
                    }
                }
            }

            player.sendMessage("§aSaque realizado com sucesso !")

            if (recharge != .0) {
                player.sendMessage("§e$recharge §aUkranianinho's §fRetidos: Você está sem espaço em seu inventário/ender chest")
            }

            withDraw(player.uniqueId, value - recharge)
        } else {
            player.sendMessage("§cSaldo insuficiente !")
        }
    }

    fun withDraw(player: Player, account: String, value: Double) {
        val balance = AccountManager.getBalance(account)

        if (balance >= value) {
            val decompose = decompose(value)

            var recharge = .0

            decompose.forEach { amount ->
                val currencyValue = currencies.first { it.material == amount.material }.value

                val item = GameManager.createItem(
                    coinDisplayName,
                    arrayOf(
                        Component.text(" "),
                        Component.text(" §f- Valor §e$currencyValue §fUkranianinho`s"),
                        Component.text(" ")
                    ),
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
                        recharge += currencyValue * item.amount
                    }
                }
            }

            player.sendMessage("§aSaque realizado com sucesso !")

            if (recharge != .0) {
                player.sendMessage("§e$recharge §aUkranianinho's §fRetidos: Você está sem espaço em seu inventário/ender chest")
            }

            withDraw(player.uniqueId, value - recharge)
        } else {
            player.sendMessage("§cSaldo insuficiente !")
        }
    }

    fun print(player: Player, value: Double) {
        val decompose = decompose(value)

        var recharge = .0

        decompose.forEach { amount ->
            val currencyValue = currencies.first { it.material == amount.material }.value

            val item = GameManager.createItem(
                coinDisplayName,
                arrayOf(
                    Component.text(" "),
                    Component.text(" §f- Valor §e${currencyValue} §fUkranianinho`s"),
                    Component.text(" ")
                ),
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
                    recharge += currencyValue * item.amount
                }
            }
        }

        player.sendMessage("§aImpressão realizada com sucesso !")

        if (recharge != .0) {
            player.sendMessage("§e$recharge §aUkranianinho's §fRetidos: Você está sem espaço em seu inventário")
        }
    }

    fun transfer(credited: String, debited: String, value: Double): Boolean {
        if (!AccountManager.isValidAccount(credited)) return false
        if (!AccountManager.isValidAccount(debited)) return false

        val balance = AccountManager.getBalance(debited)

        if (balance >= value) {
            AccountManager.changeBalance(credited, value)
            AccountManager.changeBalance(debited, -value)

            return true
        }

        return false
    }

    fun transfer(credited: Player, debited: String, value: Double): Boolean {
        return transfer(PlayerManager.getAccountId(credited.uniqueId), debited, value)
    }

    fun transfer(credited: Player, debited: Player, value: Double): Boolean {
        return transfer(
            PlayerManager.getAccountId(credited.uniqueId),
            PlayerManager.getAccountId(debited.uniqueId),
            value
        )
    }

    fun transfer(credited: String, debited: Player, value: Double): Boolean {
        return transfer(
            credited,
            PlayerManager.getAccountId(debited.uniqueId),
            value
        )
    }

    fun inject(creditedAccount: String, value: Double, bankManager: Player?) {
        PlayerManager.isValidAccount(creditedAccount).thenAcceptAsync { valid ->
            if (valid) {
                deposit(creditedAccount, value)

                bankManager?.sendMessage("${Strings.PREFIX} §fVocê injetou §e$value §aUkranianinhos §fna conta §b${creditedAccount}")
            } else {
                bankManager?.sendMessage("§cConta inválida !")
            }
        }
    }

    private fun decompose(value: Double): Array<Amount> {
        var v = value

        val quantities = ArrayList<Amount>()

        var wd = .000

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

        return quantities.toTypedArray()
    }
}