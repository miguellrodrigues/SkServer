package com.miguel.game.manager

import com.miguel.common.TagCommon
import net.kyori.adventure.text.Component

class TagManager : Runnable {

    override fun run() {
        while (true) {
            GameManager.getPlayers().forEach { player ->
                val sb = player.scoreboard

                GameManager.getPlayers().forEach {
                    val tag = TagCommon.getTag(it)

                    val tagPriority = tag.tagPriority.toString()

                    var team = sb.getTeam(tagPriority)

                    if (team == null)
                        team = sb.registerNewTeam(tagPriority)

                    if (!team.hasEntry(it.name))
                        team.addEntry(it.name)

                    val formattedName = Component.text("${tag.formattedName}§7")

                    if (team.prefix() != formattedName) team.prefix(formattedName)
                }

                if (sb != player.scoreboard) {
                    player.scoreboard = sb
                }
            }

            Thread.sleep(1000)
        }
    }
}