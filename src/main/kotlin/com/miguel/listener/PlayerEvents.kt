package com.miguel.listener

import com.miguel.common.TagCommon
import com.miguel.game.manager.GameManager
import com.miguel.game.manager.PlayerManager
import com.miguel.values.Strings
import com.miguel.values.Values
import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.time.Duration

class PlayerEvents : Listener {

    @EventHandler
    fun onPlayerLogin(event: PlayerLoginEvent) {
        PlayerManager.load(event.player)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        player.location.chunk.load()

        event.joinMessage(Component.empty())

        GameManager.sendTab(
            player,
            "${Strings.PREFIX} \n §7Servidor §fOficial \n\n",
            "\n                                          \n§7Tenha um bom jogo!"
        )

        player.showTitle(Title.title(
            Component.text(Strings.PREFIX),
            Component.text("Seja bem vindo, §f${player.name}"),
            Title.Times.times(Duration.ofMillis(2000), Duration.ofMillis(1500), Duration.ofMillis(1000))
        ))
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        event.quitMessage(Component.empty())
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onAsyncPlayerChat(event: AsyncChatEvent) {
        event.isCancelled = !Values.CHAT

        event.renderer(ChatRenderer.viewerUnaware { player: Player, _: Component, message: Component ->
            Component.text(
                TagCommon.getTag(player).formattedName +
                        "§7" + player.name + " §c→ §f"
            ).append(message)
        })
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val entity = event.player

        val deathLocation = entity.location
        var valueLocation = deathLocation.clone()

        while (valueLocation.block.type.name.contains("VOID")) {
            valueLocation = valueLocation.add(0.0, 1.0, 0.0)
        }

        valueLocation.block.type = Material.CHEST

        val chest = valueLocation.block.state as Chest

        var contents = entity.inventory.contents.filterNotNull()
        event.drops.removeAll(contents)

        while (chest.inventory.firstEmpty() != -1 && contents.isNotEmpty()) {
            chest.inventory.addItem(contents.first())
            contents = contents.drop(1)
        }

        // teleport to respawn location
        entity.sendMessage("§cVocê morreu e seus itens foram colocados em um baú no chão.")

        entity.sendMessage(
            "§c${valueLocation.x.toInt()} ${valueLocation.y.toInt()} ${valueLocation.z.toInt()}"
        )

        event.deathMessage(Component.empty())
    }
}