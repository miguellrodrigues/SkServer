package com.miguel.commands.common

import com.miguel.values.Strings
import org.bukkit.FluidCollisionMode
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

class Ping : BukkitCommand("ping") {

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage("Comando apenas para jogadores")
            return true
        }

        val targetBlock = sender.getTargetBlockExact(
            50,
            FluidCollisionMode.ALWAYS
        )

        if (targetBlock == null) {
            sender.sendMessage("${Strings.MESSAGE_PREFIX}Você não está olhando para um bloco")
            return true
        }

        val distance = sender.location.distance(targetBlock.location)

        // create a fake glowstone block at the target block
        // for all players in a radius of (distance) blocks of the target block

        for (player in sender.world.getNearbyEntities(
            targetBlock.location,
            distance,
            distance,
            distance
        )) {
            if (player !is Player) continue

            player.sendBlockChange(
                targetBlock.location,
                org.bukkit.Material.GLOWSTONE.createBlockData()
            )
        }

        return false
    }
}