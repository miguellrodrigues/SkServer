package com.miguel.commands.common

import com.miguel.common.command.Permission
import com.miguel.game.chunk.ChunkLoader
import com.miguel.game.chunk.ChunkLoaderManager
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import java.util.UUID

class ChunkLoader : BukkitCommand("chunk_loader") {

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            return true
        }

        if (!sender.hasPermission("cmd.chunk_loader")) {
            return true
        }

        if (args.size != 1) {
            sender.sendMessage("§c/chunk_loader [add | remove]")
            return true
        }

        val action = args[0]
        val chunk = sender.location.chunk

        when (action) {
            "add" -> {
                val id = UUID.nameUUIDFromBytes(
                    "${chunk.world.name}:${chunk.x}:${chunk.z}".toByteArray()
                )

                if (ChunkLoaderManager.exists(id)) {
                    sender.sendMessage("§cEsse chunk ja possui um ChunkLoader!")
                    return true
                }

                ChunkLoaderManager.add(id, ChunkLoader(chunk, ChunkLoaderManager.globalDelayTime))
                sender.sendMessage("§aChunkLoader adicionado com sucesso!")
            }
            "remove" -> {
                if (! ChunkLoaderManager.exists(sender.location)) {
                    sender.sendMessage("§cEste chunk nao possui um ChunkLoader!")
                    return true
                }

                ChunkLoaderManager.remove(sender.location)
                sender.sendMessage("§aChunkLoader removido com sucesso!")
            }
            else -> {
                sender.sendMessage("§c/chunk_loader [add | remove]")
            }
        }

        return false
    }
}