package com.miguel.game.home

import com.miguel.entities.SHome
import com.miguel.entities.SLocation
import com.miguel.game.manager.PlayerManager
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.math.round

object HomeManager {

    private fun generateLocationID(player: UUID, worldLocation: Location): String {
        return UUID.nameUUIDFromBytes("SLocation:${worldLocation.hashCode()}:$player".toByteArray()).toString().substring(0, 8)
    }

    fun getHome(player: Player, name: String): CompletableFuture<SHome?> {
        return PlayerManager.getHomes(player.uniqueId).thenApplyAsync { playerHomes ->
            val filter =
                playerHomes.filter { it.name.lowercase(Locale.getDefault()) == name.lowercase(Locale.getDefault()) }

            filter.firstOrNull()
        }
    }

    fun removeHome(player: Player, name: String) {
        getHome(player, name).thenAcceptAsync { home ->
            if (home == null) {
                player.sendMessage("§cHome não encontrada !")
            } else {
                PlayerManager.removeHome(player.uniqueId, home)

                player.sendMessage("§fHome §e${name} §cremovida §fcom sucesso !")
                player.playSound(player.location, Sound.BLOCK_LEVER_CLICK, 1.0F, 1.0F)
            }
        }
    }

    fun setHome(player: Player, name: String) {
        PlayerManager.getHomes(player.uniqueId).thenAcceptAsync { playerHomes ->
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
}