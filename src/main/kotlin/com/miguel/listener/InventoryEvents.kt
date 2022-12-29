package com.miguel.listener

import com.miguel.game.manager.InventoryManager
import com.miguel.game.market.MarketManager
import com.miguel.values.Strings
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
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

        if (view.type == InventoryType.GRINDSTONE) {
            val itemZero = view.getItem(0)
            val itemOne = view.getItem(1)

            if (itemZero == null) return
            if (itemOne == null) return

            val firstItem = if (itemZero.type == Material.AIR) {
                itemOne
            } else {
                itemZero
            }

            val resultItem = view.getItem(2)

            if (firstItem.type == Material.ENCHANTED_BOOK) return
            if (resultItem == null) return

            if (event.slot == 2) {
                val enchantments = firstItem.enchantments

                if (enchantments.isEmpty()) return

                val book = ItemStack(Material.BOOK, 1)

                if (player.inventory.containsAtLeast(book, enchantments.size)) {
                    enchantments.forEach { enchantment ->
                        player.inventory.removeItem(book)

                        val enchantedBook = ItemStack(Material.ENCHANTED_BOOK)
                        enchantedBook.addUnsafeEnchantment(enchantment.key, enchantment.value)

                        if (player.inventory.firstEmpty() == -1) {
                            player.world.dropItem(player.location, enchantedBook)
                        } else {
                            player.inventory.addItem(enchantedBook)
                        }
                    }
                }
            }
        }

        if (InventoryManager.has(inventory)) {
            if (currentItem == null || currentItem.type == Material.AIR)
                return

            event.isCancelled = true

            if (player.inventory.contains(currentItem)) return

            val title = PlainTextComponentSerializer.plainText().serialize(view.title()).lowercase()

            if (title.startsWith("página ")) {
                val split =  title.split(" - ")

                val owner = split[1]
                var page = split[0].split(" ")[1].toInt()

                when (currentItem.type) {
                    Material.LIME_DYE -> {
                        InventoryManager.openAdInventory(player, owner, ++page)
                    }

                    Material.LIGHT_BLUE_DYE -> {
                        InventoryManager.openAdInventory(player, owner, --page)
                    }

                    Material.RED_DYE -> {
                    }

                    Material.BLUE_DYE -> {
                        InventoryManager.openPlayersHeadsInventory(player, 1)
                    }

                    else -> {
                        if (!currentItem.type.name.lowercase(Locale.getDefault()).contains("glass")) {
                            val itemMeta = currentItem.itemMeta

                            val id =
                                itemMeta.lore()?.let {
                                    PlainTextComponentSerializer.plainText().serialize(it[7]).replace(" §fID §e", "")
                                        .toInt()
                                }

                            val ad = id?.let { MarketManager.getById(it) }!!

                            if (ad.advertiserName.lowercase() == player.name.lowercase()) return

                            MarketManager.purchase(player, ad)
                        }
                    }
                }
            } else if (title.startsWith("anunciantes página ")) {
                var page = title.replace("anunciantes página ", "").toInt()

                when (currentItem.type) {
                    Material.LIME_DYE -> {
                        InventoryManager.openPlayersHeadsInventory(player, ++page)
                    }

                    Material.LIGHT_BLUE_DYE -> {
                        InventoryManager.openPlayersHeadsInventory(player, --page)
                    }

                    Material.RED_DYE -> {
                    }

                    else -> {
                        if (!currentItem.type.name.lowercase(Locale.getDefault()).contains("glass")) {
                            val itemMeta = currentItem.itemMeta

                            val owner = itemMeta.displayName()
                                ?.let { PlainTextComponentSerializer.plainText().serialize(it) }

                            if (owner != null) {
                                InventoryManager.openAdInventory(player, ChatColor.stripColor(owner)!!, 1)
                            }
                        }
                    }
                }
            }

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
                            player.sendMessage("${Strings.MARKET_PREFIX} digite /anuncio para anunciar")
                            player.closeInventory()
                        }

                        else -> {
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