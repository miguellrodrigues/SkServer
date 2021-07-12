package com.miguel.common.command

import org.bukkit.command.CommandSender

enum class Permission(val node: String) {

    NONE(""),
    TAG("tag."),
    CLEAR_CHAT("clearchat"),
    CHAT("chat"),
    DAMAGE("damage"),
    BANK_OWN("bank.own"),
    INVSEE("invsee"),
    SUMO("sumo");

    companion object {
        private fun getPermission(permission: Permission): String {
            return "sk." + permission.node
        }

        fun has(permission: Permission, target: CommandSender): Boolean {
            return target.hasPermission(
                getPermission(
                    permission
                )
            )
        }
    }
}