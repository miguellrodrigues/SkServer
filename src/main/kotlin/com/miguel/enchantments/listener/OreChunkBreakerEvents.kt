package com.miguel.enchantments.listener

import com.miguel.Main
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import java.util.*

class OreChunkBreakerEvents : Listener {

    private fun getConnectedBlocks(
        block: Block,
        results: MutableSet<Block>,
        todo: MutableList<Block>,
        material: Material
    ) {
        BlockFace.values().forEach { blockFace ->
            val relative = block.getRelative(blockFace)

            if (relative.type == material) {
                if (results.add(relative)) {
                    todo.add(relative)
                }
            }
        }
    }

    private fun getConnectedBlocks(block: Block, material: Material): Set<Block> {
        val set: MutableSet<Block> = HashSet()

        val list = LinkedList<Block>()

        list.add(block)

        var poll = list.poll()

        while (poll != null) {
            getConnectedBlocks(poll, set, list, material)
            poll = list.poll()
        }

        return set
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val block = event.block

        if (!block.type.name.contains("ORE")) return

        val player = event.player
        val itemInMainHand = player.inventory.itemInMainHand

        val enchantments = itemInMainHand.enchantments

        if (enchantments.containsKey(Main.ORE_CHUNK_BREAKER)) {
            val connectedBlocks = getConnectedBlocks(block, block.type)

            connectedBlocks.forEach { b -> b.breakNaturally(itemInMainHand) }

            // deteriorate the item
            player.inventory.setItemInMainHand(
                itemInMainHand.damage(connectedBlocks.size, player)
            )
        }
    }
}