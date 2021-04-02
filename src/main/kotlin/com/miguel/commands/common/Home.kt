package com.miguel.commands.common

import com.miguel.game.home.HomeManager
import com.miguel.values.Strings
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

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
                val playerHomes = HomeManager.getPlayerHomes(sender)

                if (playerHomes.isEmpty()) {
                    sender.sendMessage("§cVocê ainda não setou nenhuma home !")
                } else {
                    sender.sendMessage(" ")
                    sender.sendMessage("§aSuas homes: ")
                    sender.sendMessage(" ")

                    playerHomes.forEach {
                        val component = TextComponent(
                            "§e- §fNome §b» §f§r${it.name}"
                        )

                        component.clickEvent = ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/home go ${it.name}"
                        )

                        component.hoverEvent = HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            Text("§eClique para ir a esta home!")
                        )

                        sender.spigot().sendMessage(component)

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
                            Location(
                                Bukkit.getWorld(home.location.world),
                                home.location.x,
                                home.location.y,
                                home.location.z
                            )
                        )

                        sender.sendMessage("§fTeleportado para a home §e${home.name.toUpperCase()}")
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