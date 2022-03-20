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
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.properties.Delegates

object MarketManager {

    private val ads = ArrayList<SAd>()

    private val sadController = SAdController(MysqlAdRepository())

    private var lastId by Delegates.notNull<Int>()

    const val taxPercentage = .05

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

    fun getById(id: Int): SAd {
        return ads.first { it.id == id }
    }

    fun getByOwner(owner: String): List<SAd> {
        return ads.filter { it.advertiserName.lowercase() == owner.lowercase() && !it.delete }
    }

    fun purchase(player: Player, ad: SAd) {
        if (ads[ads.indexOf(ad)].delete) {
            player.closeInventory()
            player.sendMessage("${Strings.MARKET_PREFIX} Este anuncio não está disponivel")
            return
        }

        if (BankManager.withDraw(player.uniqueId, ad.price)) {
            player.inventory.addItem(GameManager.deserializeItem(ad.item))

            ads[ads.indexOf(ad)].delete = true

            player.sendMessage("${Strings.MESSAGE_PREFIX} Compra realizada com sucesso !")

            var tax = (ad.price * taxPercentage)

            if (Bukkit.getPlayer(ad.advertiserName) != null) {
                val advertiser = Bukkit.getPlayer(ad.advertiserName)!!

                AccountManager.changeBalance(Values.governmentID, tax)

                PlayerManager.changeBalance(advertiser.uniqueId, ad.price - tax)
                advertiser.sendMessage("${Strings.MARKET_PREFIX} Você recebeu §e${ad.price} §aUkranianinho`s referente ao anúncio de ID §a${ad.id}")
            } else {
                if (ad.account_id == Values.governmentID) tax = .0

                PlayerManager.changeBalance(ad.account_id, ad.price - tax)
            }

            player.closeInventory()
        } else {
            player.sendMessage("§cSaldo insuficiente !")
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

        val filter = playerAds.filter { it.name.lowercase(Locale.getDefault()) == name.lowercase(Locale.getDefault()) }

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

            GameManager.sendMessage("${Strings.MARKET_PREFIX} O jogador §f$ownerName fez um anúncio")
            GameManager.sendMessage("${Strings.MARKET_PREFIX} Digite /mercado para mais informações")

            return true
        }

        return false
    }
}