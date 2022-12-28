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

        if (!sender.hasPermission(Permission.INFO.node)) {
            return true
        }

        if (args.size != 1) {
            sender.sendMessage("§c/chunk_loader [add | remove]")
            return true
        }

        val action = args[0]
        val chunk = sender.location.chunk
        val id = UUID.fromString(
            sender.location.toString()
        )

        when (action) {
            "add" -> {
                if (ChunkLoaderManager.exists(id)) {
                    sender.sendMessage("§cVocê já possui um chunk loader!")
                    return true
                }

                if (ChunkLoaderManager.exists(sender.location)) {
                    sender.sendMessage("§cJá existe um chunk loader nesse chunk!")
                    return true
                }

                ChunkLoaderManager.add(id, ChunkLoader(chunk, ChunkLoaderManager.globalDelayTime))

                sender.sendMessage("§aChunk loader adicionado com sucesso!")
            }
            "remove" -> {
                if (!ChunkLoaderManager.exists(id)) {
                    sender.sendMessage("§cVocê não possui um chunk loader!")
                    return true
                }

                ChunkLoaderManager.remove(id)

                sender.sendMessage("§aChunk loader removido com sucesso!")
            }
            else -> {
                sender.sendMessage("§c/chunk_loader [add | remove]")
            }
        }

        return false
    }
}