package com.miguel

import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.plugin.bootstrap.PluginProviderContext
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.data.EnchantmentRegistryEntry
import io.papermc.paper.registry.event.RegistryEvents
import io.papermc.paper.registry.event.RegistryFreezeEvent
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.plugin.java.JavaPlugin

class BootStrapper : PluginBootstrap {

    override fun bootstrap(context: BootstrapContext) {
        context.lifecycleManager.registerEventHandler(
            RegistryEvents.ENCHANTMENT.freeze()
                .newHandler { event: RegistryFreezeEvent<Enchantment?, EnchantmentRegistryEntry.Builder> ->
                    event.registry().register(
                        TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("skserver:ocb"))) { b: EnchantmentRegistryEntry.Builder ->

                        b.description(Component.text("Ore Crusher Booster"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.PICKAXES))
                            .anvilCost(1)
                            .maxLevel(25)
                            .weight(10)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                            .activeSlots(EquipmentSlotGroup.HAND)
                    }
                })
    }

    override fun createPlugin(context: PluginProviderContext): JavaPlugin {
        return Main()
    }
}