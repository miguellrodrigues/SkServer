package com.miguel.commands.common

import com.miguel.game.home.HomeManager
import com.miguel.game.manager.PlayerManager
import com.miguel.values.Strings
import net.kyori.adventure.audience.MessageType
import net.kyori.adventure.identity.Identity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import java.util.*

class Home : BukkitCommand("home") {

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage("Comando apenas para jogadores")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("${Strings.PREFIX} Use §c/home [list] [set | go | delete] [nome]")
        } else if (args.size == 1) {
            if (args[0] == "list") {
                val playerHomes = PlayerManager.getHomes(sender.uniqueId)

                if (playerHomes.isEmpty()) {
                    sender.sendMessage("§cVocê ainda não setou nenhuma home !")
                } else {
                    sender.sendMessage(" ")
                    sender.sendMessage("§aSuas homes: ")
                    sender.sendMessage(" ")

                    playerHomes.forEach {
                        val component = Component.text("§e- §fNome §b» §f§r${it.name}")

                        component.clickEvent(ClickEvent.clickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/home go ${it.name}"
                        ))

                        component.hoverEvent(
                            net.kyori.adventure.text.event.HoverEvent.hoverEvent(
                                net.kyori.adventure.text.event.HoverEvent.Action.SHOW_TEXT,
                                Component.text("§eClique para ir a esta home!")
                            )
                        )

                        sender.sendMessage(
                            Identity.nil(),
                            component,
                            MessageType.CHAT
                        )

                        sender.sendMessage("§e- §fX '§e${it.location.x.toInt()}§f' §e| §fZ '§e${it.location.z.toInt()}§f'")
                        sender.sendMessage(" ")
                    }
                }
            }
        } else if (args.size == 2) {
            when (args[0]) {
                "set" -> {
                    val message = HomeManager.setHome(sender, args[1])

                    sender.sendMessage(message)
                }

                "go" -> {
                    val home = HomeManager.getHome(sender, args[1])

                    if (home == null) {
                        sender.sendMessage("Home não encontrada !")
                    } else {
                        sender.teleport(
                            home.location.toBukkitLocation()
                        )

                        sender.sendMessage("§fTeleportado para a home §e${home.name.uppercase(Locale.getDefault())}")
                        sender.playSound(sender.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F)
                    }
                }

                "delete" -> {
                    val message = HomeManager.removeHome(sender, args[1])

                    sender.sendMessage(message)

                    sender.playSound(sender.location, Sound.BLOCK_LEVER_CLICK, 1.0F, 1.0F)
                }
            }
        } else {
            sender.sendMessage("${Strings.PREFIX}Use §c/home [set | go | delete] [nome]")
        }

        return false
    }
}