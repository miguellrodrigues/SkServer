package com.miguel.game.market

import com.miguel.controller.SAdController
import com.miguel.entities.SAd
import com.miguel.game.bank.BankManager
import com.miguel.game.manager.GameManager
import com.miguel.game.manager.PlayerManager
import com.miguel.repository.impl.MysqlAdRepository
import com.miguel.values.Strings
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

object MarketManager {

    private val ads = ArrayList<SAd>()

    private val sadController = SAdController(MysqlAdRepository())

    fun init() {
        ads.addAll(sadController.getAll())
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
                    it.advertiserName.lowercase() == player.name.lowercase()
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
        if (BankManager.withDraw(player.uniqueId, ad.price)) {
            val item = ItemStack(Material.getMaterial(ad.material)!!)
            item.amount = ad.amount

            player.inventory.addItem(item)

            PlayerManager.increaseBalance(ad.player_id, ad.price)

            ads.remove(ad)

            player.sendMessage("${Strings.MESSAGE_PREFIX} Compra realizada com sucesso !")

            Bukkit.getPlayer(ad.player_id)
                ?.sendMessage("${Strings.MARKET_PREFIX} Você recebeu §e${ad.price} §aUkranianinho`s referente ao anúncio de ID §a${ad.id}")
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
        }
    }

    fun advertise(player: Player, name: String, price: Double) {
        val playerAds = getPlayerAds(player)

        val filter = playerAds.filter { it.name.lowercase(Locale.getDefault()) == name.lowercase(Locale.getDefault()) }

        if (filter.isEmpty()) {
            val itemInMainHand = player.inventory.itemInMainHand

            val ad = SAd(
                id = if (ads.isEmpty()) 1 else ads.last().id + 1,
                advertiserName = (player as CraftPlayer).name,
                name = name,
                price = price,
                amount = itemInMainHand.amount,
                material = itemInMainHand.type.name,
                player_id = player.uniqueId
            )

            player.inventory.remove(itemInMainHand)

            ads.add(ad)

            GameManager.sendMessage("${Strings.MARKET_PREFIX} O jogador §f${player.name} fez um anúncio")
            GameManager.sendMessage("${Strings.MARKET_PREFIX} Digite /mercado para mais informações")

        } else {
            player.sendMessage("§cVocê já possui um anúncio com esse nome !")
        }
    }
}