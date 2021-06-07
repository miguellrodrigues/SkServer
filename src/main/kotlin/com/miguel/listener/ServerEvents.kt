package com.miguel.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent

class ServerEvents : Listener {

    @EventHandler
    fun onServerListPing(event: ServerListPingEvent) {
        event.motd = "                 §a§r§fQuase uma §eSk§a\n            §e§nVenha jogar conosco !"
    }
}