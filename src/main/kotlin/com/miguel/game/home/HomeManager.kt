package com.miguel.game.home

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.miguel.Main
import org.apache.commons.io.FileUtils
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

object HomeManager {

    private val homesFile = File(Main.INSTANCE.dataFolder, "homes.json")

    private val gson = Gson()

    private val homes = ArrayList<Home>()

    fun init() {
        if (!homesFile.exists()) {
            homesFile.createNewFile()

            write(JsonArray())
        }

        loadAllHomes()
    }

    private fun loadAllHomes() {
        val homesArray = JsonParser().parse(FileUtils.readFileToString(homesFile, "UTF-8")).asJsonArray

        homesArray.forEach {
            homes.add(gson.fromJson(it, Home::class.java))
        }
    }

    fun delete() {
        val homesArray = JsonParser().parse(FileUtils.readFileToString(homesFile, "UTF-8")).asJsonArray

        val copy = JsonArray()

        homesArray.forEach {
            copy.add(it.asJsonObject)
        }

        copy.forEach {
            val home = gson.fromJson(it, Home::class.java)

            if (!homes.contains(home))
                homesArray.remove(it)
        }

        write(homesArray)
    }

    private fun createHome(home: Home) {
        Thread {
            val homesArray = JsonParser().parse(FileUtils.readFileToString(homesFile, "UTF-8")).asJsonArray

            if (!homeExist(home)) {
                val homeObject = JsonParser().parse(gson.toJson(home, Home::class.java)).asJsonObject

                homesArray.add(homeObject)

                write(homesArray)
            }
        }.start()
    }

    private fun homeExist(home: Home): Boolean {
        val homesArray = JsonParser().parse(FileUtils.readFileToString(homesFile, "UTF-8")).asJsonArray

        homesArray.forEach {
            val homeObject = gson.fromJson(it, Home::class.java)

            return homeObject == home
        }

        return false
    }

    fun getPlayerHomes(player: Player): List<Home> {
        val playerHomes = ArrayList<Home>()

        if (homes.isNotEmpty()) {
            playerHomes.addAll(
                homes.filter { it.owner == player.uniqueId }
            )
        }

        return playerHomes
    }

    fun getHome(player: Player, name: String): Home? {
        val playerHomes = getPlayerHomes(player)

        if (playerHomes.isNotEmpty()) {
            val filter = playerHomes.filter { it.name.lowercase(Locale.getDefault()) == name.lowercase(Locale.getDefault()) }

            if (filter.isNotEmpty()) {
                return filter.first()
            }

            return null
        }

        return null
    }

    fun removeHome(player: Player, name: String): String {
        val home = getHome(player, name)

        if (home == null) {
            return "§cHome não encontrada !"
        } else {
            homes.remove(home)

            player.sendMessage("§fHome §e${name} §cfremovida com sucesso !")
        }

        return ""
    }

    fun setHome(player: Player, name: String): String {
        val message: String

        val playerHomes = getPlayerHomes(player)

        val filter = playerHomes.filter { it.name.lowercase(Locale.getDefault()) == name.lowercase(Locale.getDefault()) }

        if (filter.isEmpty()) {
            val home = Home(
                name,
                HomeLocation(
                    player.location.x,
                    player.location.y,
                    player.location.z,
                    player.world.name
                ),
                player.uniqueId
            )

            homes.add(home)

            createHome(home)

            message = "Home setada com sucesso !"

            player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F)
        } else {
            message = "Você já possui uma home com este nome !"
        }

        return message
    }

    private fun write(element: JsonElement) {
        FileUtils.writeStringToFile(
            homesFile,
            gson.toJson(element),
            "UTF-8"
        )
    }
}