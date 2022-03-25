package com.miguel.listener

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class BlockEvents : Listener {

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player

        if (player.inventory.itemInMainHand.type == Material.WOODEN_AXE && player.gameMode == GameMode.CREATIVE) {
            event.isCancelled = true
            player.sendMessage("${event.block.x} ${event.block.y} ${event.block.z}")
        }
    }
}