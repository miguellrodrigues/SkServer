package com.miguel.common.command

import org.bukkit.command.CommandSender

enum class Permission(val node: String) {
  
  NONE(""),
  TAG("tag."),
  CLEAR_CHAT("clearchat"),
  CHAT("chat"),
  DAMAGE( "damage");
  
  companion object {
    private fun getPermission(permission: Permission): String {
      return "sk.command." + permission.node
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