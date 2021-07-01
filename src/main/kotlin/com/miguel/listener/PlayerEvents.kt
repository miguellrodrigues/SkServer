package com.miguel.listener

import com.miguel.common.TagCommon
import com.miguel.data.PlayerData
import com.miguel.game.manager.GameManager
import com.miguel.values.Strings
import com.miguel.values.Values
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerEvents : Listener {

    @EventHandler
    fun onPlayerLogin(event: PlayerLoginEvent) {
        val player = event.player

        PlayerData.createData(player.uniqueId)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        player.location.chunk

        event.joinMessage(Component.empty())

        GameManager.sendTab(
            player,
            "${Strings.PREFIX} \n §7Servidor §fOficial \n\n",
            "\n§7Discord: §fhttps://discord.gg/J33egdFkNx\n§7Tenha um bom jogo!"
        )

        player.sendTitle(Strings.PREFIX, "Seja bem vindo, §f${player.name}", 20, 100, 20)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        event.quitMessage(Component.empty())
    }

    @EventHandler
    fun onAsyncPlayerChat(event: AsyncPlayerChatEvent) {
        val player = event.player

        event.isCancelled = !Values.CHAT

        event.format =
            TagCommon.getTag(player).formattedName +
                    "§7" + player.name + " §c→ §f" + event.message
    }
}