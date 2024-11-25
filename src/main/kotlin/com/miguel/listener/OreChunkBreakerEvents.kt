package com.miguel.listener

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import net.kyori.adventure.key.Key
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.enchantment.EnchantItemEvent
import java.util.*

class OreChunkBreakerEvents : Listener {

    private val registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
    private val oreChunkBreaker = registry.getOrThrow(Key.key("ore_chunk_breaker"))

    private val probEnchant = 0.15

    private val enchantsToAdd = listOf(
        Enchantment.SILK_TOUCH
    )

    private fun getConnectedBlocks(
        block: Block,
        results: MutableSet<Block>,
        todo: MutableList<Block>,
        material: Material
    ) {
        BlockFace.entries.forEach { blockFace ->
            val relative = block.getRelative(blockFace)

            if (relative.type == material) {
                if (results.add(relative)) {
                    todo.add(relative)
                }
            }
        }
    }

    private fun getConnectedBlocks(block: Block, material: Material): Set<Block> {
        val set = mutableSetOf<Block>()
        val list = LinkedList<Block>().apply { add(block) }

        while (list.isNotEmpty()) {
            val poll = list.poll()

            BlockFace.entries.forEach { blockFace ->
                val relative = poll.getRelative(blockFace)

                if (relative.type == material && set.add(relative)) {
                    list.add(relative)
                }
            }
        }

        return set
    }

    @EventHandler
    fun onEnchantItem(event: EnchantItemEvent) {
        val enchants = event.enchantsToAdd

        if (Math.random() > probEnchant) return

        enchants.forEach {
            if (enchantsToAdd.contains(it.key)) {
                enchants[oreChunkBreaker] = 1
                return@forEach
            }
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val block = event.block

        if (!block.type.name.contains("ORE")) return

        val player = event.player
        val itemInMainHand = player.inventory.itemInMainHand

        val enchantments = itemInMainHand.enchantments

        if (enchantments.containsKey(oreChunkBreaker)) {
            val connectedBlocks = getConnectedBlocks(block, block.type)

            connectedBlocks.forEach { b -> b.breakNaturally(itemInMainHand) }

            // deteriorate the item
            player.inventory.setItemInMainHand(
                itemInMainHand.damage(connectedBlocks.size, player)
            )
        }
    }
}