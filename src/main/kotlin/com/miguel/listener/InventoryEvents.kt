package com.miguel.listener

import com.miguel.game.manager.InventoryManager
import com.miguel.game.market.MarketManager
import com.miguel.values.Strings
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import java.util.*

class InventoryEvents : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked

        if (player !is Player)
            return

        val inventory = event.inventory

        val currentItem = event.currentItem

        val view = event.view

        if (InventoryManager.has(inventory)) {
            if (currentItem == null || currentItem.type == Material.AIR)
                return

            event.isCancelled = true

            val title = PlainTextComponentSerializer.plainText().serialize(view.title()).lowercase(Locale.getDefault())

            if (title.startsWith("anúncios página ")) {
                var page = title.replace("anúncios página ", "").toInt()

                when (currentItem.type) {
                    Material.LIME_DYE -> {
                        InventoryManager.openAdInventory(player, ++page)
                    }

                    Material.LIGHT_BLUE_DYE -> {
                        InventoryManager.openAdInventory(player, --page)
                    }

                    Material.RED_DYE -> {
                    }

                    else -> {
                        if (!currentItem.type.name.lowercase(Locale.getDefault()).contains("glass")) {
                            val itemMeta = currentItem.itemMeta!!

                            val id = PlainTextComponentSerializer.plainText().serialize(itemMeta.lore()!![7]).toInt()

                            val ad = MarketManager.getById(id)

                            if (ad.advertiserName.lowercase() == player.name.lowercase()) return

                            MarketManager.purchase(player, ad)
                        }
                    }
                }

            } else {
                when (title) {
                    "mercado" -> {
                        when (currentItem.type) {
                            Material.KNOWLEDGE_BOOK -> {
                                if (MarketManager.getAllAds().isEmpty()) {
                                    player.sendMessage("${Strings.MARKET_PREFIX}Nenhum anúncio encontrado")
                                    player.closeInventory()
                                } else {
                                    InventoryManager.createInventory(player, InventoryManager.InventoryType.ADS)
                                }
                            }

                            Material.PAPER -> {
                                player.sendMessage("${Strings.MARKET_PREFIX} digite /anunio para anunciar")
                                player.closeInventory()
                            }

                            else -> {
                            }
                        }

                    }
                }
            }
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val inventory = event.inventory

        if (InventoryManager.has(inventory))
            InventoryManager.remove(inventory)
    }
}