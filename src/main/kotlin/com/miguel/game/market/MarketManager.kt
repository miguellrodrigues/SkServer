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
import java.util.*
import kotlin.properties.Delegates

object MarketManager {

    private val ads = ArrayList<SAd>()

    private val sadController = SAdController(MysqlAdRepository())

    private var lastId by Delegates.notNull<Int>()

    private const val taxPercentage = .05

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

    private fun getPlayerAds(player: Player): List<SAd> {
        val playerAds = ArrayList<SAd>()

        if (ads.isNotEmpty()) {
            playerAds.addAll(
                ads.filter {
                    it.advertiserName.lowercase() == player.name.lowercase() && !it.delete
                }
            )
        }

        return playerAds
    }

    private fun getAd(player: Player, name: String): SAd? {
        val playerAds = getPlayerAds(player)

        if (playerAds.isNotEmpty()) {
            val filter =
                playerAds.filter {
                    it.name.lowercase(Locale.getDefault()) == name.lowercase(Locale.getDefault())
                }

            if (filter.isNotEmpty()) {
                return filter.first()
            }

            return null
        }

        return null
    }

    fun getById(id: Int): SAd {
        return ads.first { it.id == id }
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

            if (Bukkit.getPlayer(ad.advertiserName) != null) {
                val advertiser = Bukkit.getPlayer(ad.advertiserName)!!
                val tax = (ad.price * taxPercentage)

                AccountManager.setBalance(Values.governmentID, tax)

                PlayerManager.changeBalance(advertiser.uniqueId, ad.price - tax)
                advertiser.sendMessage("${Strings.MARKET_PREFIX} Você recebeu §e${ad.price} §aUkranianinho`s referente ao anúncio de ID §a${ad.id}")
            } else {
                PlayerManager.changeBalance(ad.account_id, ad.price)
            }

            player.closeInventory()
        } else {
            player.sendMessage("§cSaldo insuficiente !")
        }
    }

    fun removeAd(player: Player, name: String) {
        val ad = getAd(player, name)

        if (ad == null) {
            player.sendMessage("§cAnúncio não encontrado !")
        } else {
            ads[ads.indexOf(ad)].delete = true

            player.sendMessage("§fAnúncio §e${name} §fremovido com sucesso")

            player.inventory.addItem(GameManager.deserializeItem(ad.item))
        }
    }

    fun advertise(player: Player, name: String, price: Double) {
        val playerAds = getPlayerAds(player)

        val filter = playerAds.filter { it.name.lowercase(Locale.getDefault()) == name.lowercase(Locale.getDefault()) }

        if (filter.isEmpty()) {
            val itemInMainHand = player.inventory.itemInMainHand

            val ad = SAd(
                id = ++lastId,
                advertiserName = player.name,
                name = name,
                price = price,
                item = GameManager.serializeItem(itemInMainHand),
                account_id = PlayerManager.getAccountId(player.uniqueId)
            )

            player.inventory.remove(itemInMainHand)

            ads.add(ad)

            GameManager.sendMessage("${Strings.MARKET_PREFIX} O jogador §f${player.name} fez um anúncio")
            GameManager.sendMessage("${Strings.MARKET_PREFIX} Digite /mercado para mais informações")

            player.sendMessage(" ")
            player.sendMessage("${Strings.MARKET_PREFIX} Anúncio feito com sucesso !")
            player.sendMessage(" ")

            val tax = (price * taxPercentage)

            player.sendMessage(" ")
            player.sendMessage("${Strings.MARKET_PREFIX} Você irá receber ${price - tax} Ukranianinho's devido ao imposto de 2%")
            player.sendMessage(" ")
        } else {
            player.sendMessage("§cVocê já possui um anúncio com esse nome !")
        }
    }
}