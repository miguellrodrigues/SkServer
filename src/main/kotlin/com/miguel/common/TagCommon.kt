package com.miguel.common

import org.bukkit.entity.Player
import java.util.*

enum class TagCommon(private var tagPrefix: String, var nameColor: String) {

    DONO("§4§lDONO", "§4"),
    DEV("§3§lDEV", "§3"),
    ADMIN("§e§lADMIN", "§e"),
    YT("§c§lYT", "§c"),
    VIP("§a§lVIP", "§a"),
    PLAYER("§7", "§7"),
    COMEDOR("§9§lCOMEDOR", "§9"),
    CORNO("§c§lCORNO", "§c§l"),
    LOLI("§5§lLOLI", "§5§l"),
    OTAKU("§b§lOTAKU", "§b§l"),
    PREFEITO("§6§lPREFEITO", "§6§l");

    val formattedName: String
        get() = tagPrefix + if (tagPrefix != nameColor) "§r $nameColor" else ""

    val tagPriority: Int
        get() {
            val hash = HashMap<TagCommon, Int>()

            val tagCommons: MutableList<TagCommon> = ArrayList()

            for (tagCommon in values()) {
                tagCommons.add(tagCommon)
            }
            for (i in tagCommons.indices) {
                hash[tagCommons[i]] = i + 1
            }
            return if (hash.containsKey(this)) {
                "-${hash[this]!! + 1000}".toInt()
            } else {
                "-${hash.size + 1000}".toInt()
            }
        }

    companion object {
        private var playersTags = HashMap<UUID, TagCommon>()

        fun getTag(p: Player): TagCommon {
            if (playersTags.containsKey(p.uniqueId)) {
                return playersTags[p.uniqueId]!!
            } else {
                for (tagCommon in values()) {
                    val perm = "tag." + tagCommon.name.lowercase(Locale.getDefault())
                    if (p.hasPermission(perm)) {
                        playersTags[p.uniqueId] = tagCommon
                        return playersTags[p.uniqueId]!!
                    }
                }
            }
            playersTags[p.uniqueId] = PLAYER
            return playersTags[p.uniqueId]!!
        }

        fun setTag(p: Player, tagCommon: TagCommon) {
            if (playersTags.containsKey(p.uniqueId)) {
                if (tagCommon.formattedName != playersTags[p.uniqueId]!!.formattedName) {
                    playersTags[p.uniqueId] = tagCommon
                }
            } else {
                playersTags[p.uniqueId] = tagCommon
            }
        }
    }
}