package com.miguel.listener

import com.miguel.Main
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.ChatColor
import org.bukkit.Effect
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

class BlockEvents : Listener {

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player

        if (player.inventory.itemInMainHand.type == Material.WOODEN_AXE && player.gameMode == GameMode.CREATIVE) {
            event.isCancelled = true
            player.sendMessage("${event.block.x} ${event.block.y} ${event.block.z}")
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player

        val itemInMainHand = player.inventory.itemInMainHand

        if (! itemInMainHand.hasItemMeta()) return

        val itemMeta = itemInMainHand.itemMeta

        if (!itemMeta.hasDisplayName()) return

        val serializer = PlainTextComponentSerializer.plainText()
        val displayName = serializer.serialize(itemMeta.displayName()!!)

        if (displayName.lowercase() == "§bblocão") {
            val placedBlock = event.blockPlaced

            val blockLocation = placedBlock.location
            val type = placedBlock.type

            val directionStr = ChatColor.stripColor(serializer.serialize(itemMeta.lore()!![1]))!!.split(" ")
            val quantityStr  = ChatColor.stripColor(serializer.serialize(itemMeta.lore()!![2]))!!.split(" ")

            val quantity = quantityStr[2].toInt()

            val sign = directionStr[2][0]
            val axis = directionStr[2][1]

            val direction = when (sign) {
                '+' -> 1
                '-' -> -1
                else -> 0
            }

            val addVector = when (axis) {
                'x' -> Vector(1.0, 0.0, 0.0)
                'y' -> Vector(0.0, 1.0, 0.0)
                'z' -> Vector(0.0, 0.0, 1.0)
                else -> Vector(0.0, 0.0, 0.0)
            }

            val add = addVector.multiply(direction.toDouble())

            object : BukkitRunnable() {
                val location = blockLocation.clone()
                var counter = quantity

                override fun run() {
                    location.add(add)

                    if (location.block.type != Material.AIR || --counter == 0) {
                        cancel()
                        return
                    }

                    location.block.type = type
                    location.world.playEffect(
                        location,
                        Effect.STEP_SOUND,
                        type
                    )
                }
            }.runTaskTimer(Main.INSTANCE, 0, 10)
        }
    }
}