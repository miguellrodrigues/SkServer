package com.miguel.listener

import com.miguel.values.Values
import io.papermc.paper.event.entity.WardenAngerChangeEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockReceiveGameEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.event.entity.EntitySpawnEvent

class EntityEvents : Listener {

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        val entity = event.entity

        if (entity is Player) {
            event.isCancelled = !Values.DAMAGE
        }
    }
}