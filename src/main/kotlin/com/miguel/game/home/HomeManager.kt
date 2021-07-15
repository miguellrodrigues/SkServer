package com.miguel.game.home

import com.miguel.entities.SHome
import com.miguel.entities.SLocation
import com.miguel.game.manager.PlayerManager
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.round

object HomeManager {

    private fun generateLocationID(player: UUID, worldLocation: Location): String {
        return UUID.nameUUIDFromBytes("SLocation:${worldLocation.hashCode()}:$player".toByteArray()).toString().substring(0, 8)
    }

    fun getHome(player: Player, name: String): SHome? {
        val playerHomes = PlayerManager.getHomes(player.uniqueId)

        if (playerHomes.isNotEmpty()) {
            val filter =
                playerHomes.filter { it.name.lowercase(Locale.getDefault()) == name.lowercase(Locale.getDefault()) }

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
            PlayerManager.removeHome(player.uniqueId, home)

            player.sendMessage("§fHome §e${name} §cremovida §fcom sucesso !")
        }

        return ""
    }

    fun setHome(player: Player, name: String) {
        val playerHomes = PlayerManager.getHomes(player.uniqueId)

        val filter =
            playerHomes.filter { it.name.lowercase(Locale.getDefault()) == name.lowercase(Locale.getDefault()) }

        if (filter.isEmpty()) {
            PlayerManager.createHome(
                player.uniqueId, SLocation(
                    generateLocationID(player.uniqueId, player.location),
                    world = player.world.name,
                    x = round(player.location.x * 1000.0) / 1000.0,
                    y = round(player.location.y * 1000.0) / 1000.0,
                    z = round(player.location.z * 1000.0) / 1000.0
                ), name
            )

            player.sendMessage("Home setada com sucesso !")

            player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F)
        } else {
            player.sendMessage("Você já possui uma home com este nome !")
        }
    }
}