package com.miguel.game.market

import com.miguel.controller.SAdController
import com.miguel.entities.SAd
import com.miguel.game.bank.BankManager
import com.miguel.game.manager.AccountManager
import com.miguel.game.manager.GameManager
import com.miguel.game.manager.PlayerManager
import com.miguel.repository.impl.MysqlAdRepository
import com.miguel.values.Strings
import com.miguel.values.Values
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import kotlin.properties.Delegates

object MarketManager {

    private val ads = ArrayList<SAd>()

    private val sadController = SAdController(MysqlAdRepository())

    private var lastId by Delegates.notNull<Int>()

    const val taxPercentage = 5.0 / 100.0

    fun init() {
        ads.addAll(sadController.getAll())

        lastId = if (ads.isEmpty()) 0 else ads.last().id
    }

    fun save() {
        ads.forEach {
            if (it.delete) {
                sadController.delete(it)
            } else {
                sadController.save(it)
            }
        }
    }

    fun getAllAds(): List<SAd> {
        return ads.filter { !it.delete }
    }

    fun getOwners(): List<String> {
        return ads.filter { !it.delete }.map { it.advertiserName }.distinct()
    }

    fun getById(id: Int): SAd? {
        return ads.firstOrNull { it.id == id }
    }

    fun getByOwner(owner: String): List<SAd> {
        return ads.filter { it.advertiserName.lowercase() == owner.lowercase() && !it.delete }
    }

    fun purchase(accountID: String, id: Int, inventory: Inventory): String {
        val ad = getById(id) ?: return "${Strings.MARKET_PREFIX} Este anuncio não está disponivel"
        val account = AccountManager.get(accountID)

        if (ad.delete) return "${Strings.MARKET_PREFIX} Este anuncio não está disponivel"

        if (account.balance < ad.price) return "§cSaldo insuficiente!"

        if (ad.account_id == accountID) return "§cVocê não pode comprar seu próprio anúncio!"

        if (inventory.firstEmpty() == -1) return "${Strings.MARKET_PREFIX} Você não tem espaço no inventario"

        inventory.addItem(GameManager.deserializeItem(ad.item))

        ad.delete = true

        var tax = (ad.price * taxPercentage)

        if (Bukkit.getPlayer(ad.advertiserName) != null) {
            val advertiser = Bukkit.getPlayer(ad.advertiserName)!!

            AccountManager.changeBalance(Values.governmentID, tax)

            PlayerManager.changeBalance(advertiser.uniqueId, (ad.price - tax))
            advertiser.sendMessage("${Strings.MARKET_PREFIX} Você recebeu §e${ad.price - tax} §aUkranianinho`s §freferente ao anúncio de ID §a${ad.id} §f- \"§e${ad.name}\"")
        } else {
            if (ad.account_id == Values.governmentID) tax = .0

            PlayerManager.changeBalance(ad.account_id, (ad.price - tax))
        }

        return "${Strings.MESSAGE_PREFIX} Compra realizada com sucesso !"
    }

    fun purchase(player: Player, ad: SAd) {
        if (ad.delete) {
            player.closeInventory()
            player.sendMessage("${Strings.MARKET_PREFIX} Este anuncio não está disponivel")
            return
        }

        if (BankManager.withDraw(player.uniqueId, ad.price)) {
            if (player.inventory.firstEmpty() == -1) {
                player.sendMessage("${Strings.MARKET_PREFIX} Você não tem espaço no inventario")
                return
            }

            player.inventory.addItem(GameManager.deserializeItem(ad.item))

            ad.delete = true

            player.sendMessage("${Strings.MESSAGE_PREFIX} Compra realizada com sucesso !")

            var tax = (ad.price * taxPercentage)

            if (Bukkit.getPlayer(ad.advertiserName) != null) {
                val advertiser = Bukkit.getPlayer(ad.advertiserName)!!

                AccountManager.changeBalance(Values.governmentID, tax)

                PlayerManager.changeBalance(advertiser.uniqueId, (ad.price - tax))
                advertiser.sendMessage("${Strings.MARKET_PREFIX} Você recebeu §e${ad.price - tax} §aUkranianinho`s §freferente ao anúncio de ID §a${ad.id}  §e\"§f${ad.name}§e\"")
            } else {
                if (ad.account_id == Values.governmentID) tax = .0

                PlayerManager.changeBalance(ad.account_id, (ad.price - tax))
            }

            player.closeInventory()
        } else {
            player.sendMessage("§cSaldo insuficiente!")
        }
    }

    fun removeAd(owner: String, name: String): ItemStack? {
        val ads = getByOwner(owner)

        val ad = ads.firstOrNull { it.name.lowercase() == name.lowercase() }

        return if (ad == null) {
            null
        } else {
            ads[ads.indexOf(ad)].delete = true

            GameManager.deserializeItem(ad.item)
        }
    }

    fun advertise(ownerName: String, ownerAccount: String, name: String, price: Double, item: ItemStack): Boolean {
        val playerAds = getByOwner(ownerName)

        val filter = playerAds.filter { it.name.lowercase() == name.lowercase() }

        if (filter.isEmpty()) {
            val ad = SAd(
                id = ++lastId,
                advertiserName = ownerName,
                name = name,
                price = price,
                item = GameManager.serializeItem(item),
                account_id = ownerAccount
            )

            ads.add(ad)

            if (ownerName == "Governo") {
                GameManager.sendMessage("${Strings.MARKET_PREFIX} O Governo fez um anúncio")
            } else {
                GameManager.sendMessage("${Strings.MARKET_PREFIX} O jogador §f$ownerName fez um anúncio")
            }

            GameManager.sendMessage("${Strings.MARKET_PREFIX} Digite /mercado para mais informações")

            return true
        }

        return false
    }
}