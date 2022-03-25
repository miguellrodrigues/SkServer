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

        val name = args[0]

        if (name.lowercase() == "load") {
            CompletableFuture.runAsync {
                val structure = StructureManager.getStructure(args[1]).get()

                if (structure == null) {
                    sender.sendMessage("§cNão foi possível carregar a estrutura")
                } else {

                    object : BukkitRunnable() {
                        override fun run() {
                            structure.place(sender.location)
                        }
                    }.runTask(Main.INSTANCE)

                    sender.sendMessage("§aEstrutura carregada com sucesso")
                }
            }

            return true
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

        for (x in x1..x2) {
            for (y in y1..y2) {
                for (z in z1..z2) {
                    val block = sender.world.getBlockAt(x, y, z)
                    blocks.add(block)
                }
            }
        }

        BinaryStructure.save(blocks, name)

        sender.sendMessage("§aStructure §e$name §fsalva com sucesso!")

        return false
    }
}