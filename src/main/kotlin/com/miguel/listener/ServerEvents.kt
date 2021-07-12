package com.miguel.listener

import com.miguel.values.Strings
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent

class ServerEvents : Listener {

    @EventHandler
    fun onServerListPing(event: ServerListPingEvent) {
        event.motd(Component.text("                  §a§r${Strings.PREFIX}§a\n            §f§nVenha jogar conosco !"))
    }
}