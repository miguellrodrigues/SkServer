package com.miguel.commands.common

import com.miguel.Main
import com.miguel.structure.BinaryStructure
import com.miguel.structure.StructureManager
import org.bukkit.block.Block
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.concurrent.CompletableFuture

class Structure : BukkitCommand("structure") {

    private fun toInt(string: String): Int? {
        return string.toIntOrNull()
    }

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage("Comando apenas para jogadores")
            return true
        }

        if (!sender.hasPermission("command.structure")) return true

        if (args.isEmpty()) {
            return true
        }

        val name = args[0]

        if (name.lowercase() == "load") {
            CompletableFuture.runAsync() {
                if (StructureManager.placeStructure(args[1], sender.location).get()) {
                    sender.sendMessage("§aEstrutura carregada com sucesso")
                } else {
                    sender.sendMessage("§cNão foi possível carregar a estrutura")
                }
            }

            return true
        } else if (name.lowercase() == "unload") {
            if (StructureManager.isStructureLoaded(args[1])) {
                object : BukkitRunnable() {
                    override fun run() {
                        StructureManager.unloadStructure(args[1])
                    }
                }.runTask(Main.INSTANCE)
                sender.sendMessage("§aEstrutura descarregada com sucesso")
            } else {
                sender.sendMessage("§cNão foi possível descarregar a estrutura")
            }
        }

        if (args.size != 7) {
            sender.sendMessage("§c/structure \"name\" <x1> <y1> <z1> <x2> <y2> <z2>")
            return true
        }

        val x1 = toInt(args[1])
        val y1 = toInt(args[2])
        val z1 = toInt(args[3])

        val x2 = toInt(args[4])
        val y2 = toInt(args[5])
        val z2 = toInt(args[6])

        if (x1 == null || y1 == null || z1 == null || x2 == null || y2 == null || z2 == null) {
            sender.sendMessage("§c/structure <x1> <y1> <z1> <x2> <y2> <z2>")
            return true
        }

        val blocks = mutableListOf<Block>()

        val topBlockX = if (x1 < x2) x2 else x1
        val bottomBlockX = if (x1 < x2) x1 else x2

        val topBlockY = if (y1 < y2) y2 else y1
        val bottomBlockY = if (y1 < y2) y1 else y2

        val topBlockZ = if (z1 < z2) z2 else z1
        val bottomBlockZ = if (z1 < z2) z1 else z2

        /*val width = topBlockX - bottomBlockX + 1
        val height = topBlockY - bottomBlockY + 1
        val length = topBlockZ - bottomBlockZ + 1*/

        val centerX = (topBlockX + bottomBlockX) / 2
        val centerY = (topBlockY + bottomBlockY) / 2
        val centerZ = (topBlockZ + bottomBlockZ) / 2

        for (x in bottomBlockX .. topBlockX) {
            for (z in bottomBlockZ .. topBlockZ) {
                for (y in bottomBlockY .. topBlockY) {
                    val block = sender.world.getBlockAt(x, y, z)
                    blocks.add(block)
                }
            }
        }

        BinaryStructure.save(blocks, name, centerX, centerY, centerZ)

        sender.sendMessage("§aStructure §e$name §fsalva com sucesso!")

        return false
    }
}