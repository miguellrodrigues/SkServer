package com.miguel.game.manager

import com.miguel.common.TagCommon

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

                    if (!team.hasPlayer(it))
                        team.addPlayer(it)

                    val formattedName = "${tag.formattedName}§7"

                    if (team.prefix != formattedName) team.prefix = formattedName
                }

                if (sb != player.scoreboard) {
                    player.scoreboard = sb
                }
            }

            Thread.sleep(1000)
        }
    }
}