package com.miguel.game.market

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.miguel.Main
import com.miguel.game.bank.BankManager
import com.miguel.game.manager.GameManager
import com.miguel.values.Strings
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.*

object MarketManager {

    private val adFile = File(Main.INSTANCE.dataFolder, "ads.json")

    private val gson = Gson()

    private val ads = ArrayList<MarketAd>()

    fun init() {
        if (!adFile.exists()) {
            adFile.createNewFile()

            write(JsonArray())
        }

        loadAllAds()
    }

    private fun loadAllAds() {
        val adArray = JsonParser().parse(FileUtils.readFileToString(adFile, "UTF-8")).asJsonArray

        adArray.forEach {
            ads.add(gson.fromJson(it, MarketAd::class.java))
        }
    }

    fun delete() {
        val adArray = JsonParser().parse(FileUtils.readFileToString(adFile, "UTF-8")).asJsonArray

        val copy = JsonArray()

        adArray.forEach {
            copy.add(it.asJsonObject)
        }

        copy.forEach {
            val ad = gson.fromJson(it, MarketAd::class.java)

            if (!ads.contains(ad))
                adArray.remove(it)
        }

        write(adArray)
    }

    private fun adExist(ad: MarketAd): Boolean {
        val adArray = JsonParser().parse(FileUtils.readFileToString(adFile, "UTF-8")).asJsonArray

        adArray.forEach {
            val adObject = gson.fromJson(it, MarketAd::class.java)

            return adObject == ad
        }

        return false
    }

    private fun createAd(ad: MarketAd) {
        Thread {
            val adArray = JsonParser().parse(FileUtils.readFileToString(adFile, "UTF-8")).asJsonArray

            if (!adExist(ad)) {
                val adObject = JsonParser().parse(gson.toJson(ad, MarketAd::class.java)).asJsonObject

                adArray.add(adObject)

                write(adArray)
            }
        }.start()
    }

    fun getAllAds(): List<MarketAd> {
        return ads
    }

    private fun getPlayerAds(player: Player): List<MarketAd> {
        val playerAds = ArrayList<MarketAd>()

        if (ads.isNotEmpty()) {
            playerAds.addAll(
                ads.filter { it.advertiser == player.uniqueId }
            )
        }

        return playerAds
    }

    private fun getAd(player: Player, name: String): MarketAd? {
        val playerAds = getPlayerAds(player)

        if (playerAds.isNotEmpty()) {
            val filter = playerAds.filter { it.name.lowercase(Locale.getDefault()) == name.lowercase(Locale.getDefault()) }

            if (filter.isNotEmpty()) {
                return filter.first()
            }

            return null
        }

        return null
    }

    fun getById(id: Int): MarketAd {
        return ads.first { it.id == id }
    }

    fun purchase(player: Player, ad: MarketAd) {
        if (BankManager.withDraw(player.uniqueId, ad.price)) {
            val item = ItemStack(Material.getMaterial(ad.material)!!)
            item.amount = ad.amount

            player.inventory.addItem(item)

            BankManager.deposit(ad.advertiser, ad.price)

            ads.remove(ad)

            player.sendMessage("${Strings.MESSAGE_PREFIX} Compra realizada com sucesso !")

            Bukkit.getPlayer(ad.advertiser)
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
            ads.remove(ad)

            player.sendMessage("§fAnúncio §e${name} §fremovido com sucesso")
        }
    }

    fun advertise(player: Player, name: String, price: Float) {
        val playerAds = getPlayerAds(player)

        val filter = playerAds.filter { it.name.lowercase(Locale.getDefault()) == name.lowercase(Locale.getDefault()) }

        if (filter.isEmpty()) {
            val itemInMainHand = player.inventory.itemInMainHand

            val ad = MarketAd(
                if (ads.isEmpty()) 1 else ads.last().id + 1,
                player.uniqueId,
                (player as CraftPlayer).name,
                name,
                price,
                itemInMainHand.amount,
                itemInMainHand.type.name
            )

            player.inventory.remove(itemInMainHand)

            ads.add(ad)

            createAd(ad)

            GameManager.sendMessage("${Strings.MARKET_PREFIX} O jogador §f${player.name} fez um anúncio")
            GameManager.sendMessage("${Strings.MARKET_PREFIX} Digite /mercado para mais informações")

        } else {
            player.sendMessage("§cVocê já possui um anúncio com esse nome !")
        }
    }

    private fun write(element: JsonElement) {
        FileUtils.writeStringToFile(
            adFile,
            gson.toJson(element),
            "UTF-8"
        )
    }
}