package com.miguel.packets

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.WrappedGameProfile
import com.miguel.Main
import com.miguel.values.Strings
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class CustomPing(plugin: JavaPlugin, lines: Array<String>) {

    private val profiles: MutableList<WrappedGameProfile> = ArrayList()

    init {
        lines.forEach { line ->
            profiles.add(
                WrappedGameProfile(UUID.randomUUID(), line)
            )
        }

        Main.PROTOCOL_MANAGER.addPacketListener(
            object : PacketAdapter(plugin, ListenerPriority.HIGH, PacketType.Status.Server.SERVER_INFO) {
                override fun onPacketSending(event: PacketEvent) {
                    if (event.packetType == PacketType.Status.Server.SERVER_INFO) {
                        val packet = event.packet

                        val serverPing = packet.serverPings.read(0)

                        profiles[profiles.size - 1] =
                            WrappedGameProfile(UUID.randomUUID(), "Online §a${Bukkit.getOnlinePlayers().size}")

                        serverPing.setPlayers(
                            profiles
                        )

                        serverPing.versionProtocol = 1
                        serverPing.versionName = Strings.PREFIX

                        packet?.serverPings?.write(0, serverPing)
                    }
                }
            }
        )
    }
}