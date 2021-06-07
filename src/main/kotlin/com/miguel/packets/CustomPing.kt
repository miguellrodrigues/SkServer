package com.miguel.packets

import com.miguel.packets.protocol.NMSUtil
import com.miguel.packets.protocol.TinyProtocol
import com.mojang.authlib.GameProfile
import io.netty.channel.Channel
import net.minecraft.server.v1_16_R3.PacketStatusOutServerInfo
import net.minecraft.server.v1_16_R3.ServerPing
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class CustomPing(plugin: JavaPlugin, lines: Array<String>) {

    private val sample = ServerPing.ServerPingPlayerSample(0, 0)

    private val profiles: MutableList<GameProfile> = ArrayList()

    init {
        lines.forEach { line ->
            profiles.add(
                GameProfile(UUID.randomUUID(), line)
            )
        }

        object : TinyProtocol(plugin) {
            override fun onPacketOutAsync(receiver: Player?, channel: Channel?, packet: Any?): Any {
                if (packet is PacketStatusOutServerInfo) {

                    val serverPing = NMSUtil.getValue(packet, "b") as ServerPing

                    NMSUtil.setValue(serverPing, "c", ServerPing.ServerData("§f§lQuase uma §eSk", 1))

                    profiles[profiles.size - 1] =
                        GameProfile(UUID.randomUUID(), "Online §a${Bukkit.getOnlinePlayers().size}")

                    sample.a(profiles.toTypedArray())

                    serverPing.setPlayerSample(sample)

                    NMSUtil.setValue(packet, "b", serverPing)
                }

                return super.onPacketOutAsync(receiver, channel, packet)
            }
        }
    }
}