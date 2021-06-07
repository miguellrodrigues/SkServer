package com.miguel.commands

import com.miguel.common.TagCommon
import com.miguel.common.command.Command
import com.miguel.common.command.Permission
import com.miguel.game.manager.GameManager
import com.miguel.values.Strings
import com.miguel.values.Values
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.ArrayList

class GameCommands {

    @Command(
        aliases = ["tag", "tg"],
        description = "Selecione uma tag",
        permission = Permission.NONE,
        usage = "/sk tag [tag]",
        min = 0,
        max = 1,
        console = false
    )
    fun tagCommand(sender: CommandSender, strings: Array<String>) {
        if (strings.isEmpty()) {
            val tags: MutableList<TagCommon> = ArrayList()

            TagCommon.values().forEach { tagCommon ->
                if (sender.hasPermission("${Permission.TAG.node}${tagCommon.name.lowercase(Locale.getDefault())}")) {
                    tags.add(tagCommon)
                }
            }

            if (tags.isEmpty()) {
                sender.sendMessage("§cVocê não possui nenhuma tag!")
                return
            }

            sender.sendMessage(" ")

            tags.forEach { tagCommon ->
                val component = TextComponent(
                    tagCommon.nameColor
                            + tagCommon.name.replace("_", "").uppercase(Locale.getDefault())
                )

                component.clickEvent = ClickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    "/sk tag " + tagCommon.name.replace("_", "").lowercase(Locale.getDefault())
                )
                component.hoverEvent = HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    Text("§eClique para selecionar esta tag!")
                )

                (sender as Player).spigot().sendMessage(component)
            }

            sender.sendMessage(" ")
            sender.sendMessage("§fUse §7/sk §ftag §a<§eNome da Tag§e> §fOu clique na §eTag §fDesejada")
        } else if (strings.size == 1) {
            val player = sender as Player

            val using = TagCommon.getTag(player)
            var toUse = TagCommon.PLAYER
            val str = strings[0]

            TagCommon.values().forEach { tagCommon ->
                if (tagCommon.name.replace("_".toRegex(), "").lowercase(Locale.getDefault()).startsWith(str)) {
                    toUse = tagCommon
                    return@forEach
                }
            }

            if (using.formattedName.equals(toUse.formattedName)) {
                player.sendMessage("§cVocê já está usando esta tag!")
                return
            }

            if (player.hasPermission("${Permission.TAG.node}${toUse.name.lowercase(Locale.getDefault())}")) {
                player.sendMessage(" ")
                player.sendMessage(
                    "Agora você está usando a tag: §7'" + toUse.nameColor + toUse.name.lowercase(Locale.getDefault())
                                        .replace("_".toRegex(), "").uppercase(Locale.getDefault()) + "§7'"
                )

                TagCommon.setTag(sender, toUse)
            } else {
                if (toUse === TagCommon.PLAYER) {
                    player.sendMessage(" ")
                    player.sendMessage(
                        "Agora você está usando a tag: §7'" + toUse.nameColor + toUse.name.lowercase(Locale.getDefault())
                                                .replace("_".toRegex(), "").uppercase(Locale.getDefault()) + "§7'"
                    )
                    TagCommon.setTag(player, toUse)
                } else player.sendMessage("§cDesculpe, mas você não possui permissão para usar esta tag.")
            }
        }
    }

    @Command(
        aliases = ["clearchat", "cc"],
        description = "Selecione uma tag",
        permission = Permission.CLEAR_CHAT,
        usage = "/sk cc",
        min = 0,
        max = 0,
        console = false
    )
    fun clearChatCommand(sender: CommandSender, strings: Array<String>) {
        for (i in 0..100) {
            GameManager.sendMessage(" ")
        }

        GameManager.sendMessage("${Strings.MESSAGE_PREFIX}O Chat foi limpo")
    }

    @Command(
        aliases = ["chat", "ch"],
        description = "Ativa ou desativa o chat",
        permission = Permission.CHAT,
        usage = "/sk chat",
        min = 0,
        max = 0,
        console = false
    )
    fun chatCommand(sender: CommandSender, strings: Array<String>) {
        Values.CHAT = !Values.CHAT

        if (Values.CHAT) {
            GameManager.sendMessage("${Strings.MESSAGE_PREFIX}O Chat foi §aativado")
        } else {
            GameManager.sendMessage("${Strings.MESSAGE_PREFIX}O Chat foi §edesativado")
        }
    }

    @Command(
        aliases = ["dano", "dn"],
        description = "Ativa ou desativa o dano",
        permission = Permission.DAMAGE,
        usage = "/sk damage",
        min = 0,
        max = 0,
        console = false
    )
    fun damageCommand(sender: CommandSender, strings: Array<String>) {
        Values.DAMAGE = !Values.DAMAGE

        if (Values.DAMAGE) {
            GameManager.sendMessage("${Strings.MESSAGE_PREFIX}O Dano global foi §aativado")
        } else {
            GameManager.sendMessage("${Strings.MESSAGE_PREFIX}O Dano global foi §edesativado")
        }
    }
}
