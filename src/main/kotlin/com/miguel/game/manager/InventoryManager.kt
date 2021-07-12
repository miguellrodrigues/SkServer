package com.miguel.game.manager

import com.miguel.game.market.MarketManager
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

object InventoryManager {

    private val adBaseInventory = Bukkit.createInventory(null, 54, Component.text("§aAnúncios"))

    fun init() {
        adBaseInventory.setItem(0, GameManager.createItem("§fSem página anterior.", Material.RED_DYE))
        adBaseInventory.setItem(1, GameManager.createItem("§e.", Material.YELLOW_STAINED_GLASS_PANE))
        adBaseInventory.setItem(2, GameManager.createItem("§e.", Material.YELLOW_STAINED_GLASS_PANE))
        adBaseInventory.setItem(3, GameManager.createItem("§e.", Material.YELLOW_STAINED_GLASS_PANE))
        adBaseInventory.setItem(4, GameManager.createItem("§e.", Material.YELLOW_STAINED_GLASS_PANE))
        adBaseInventory.setItem(5, GameManager.createItem("§e.", Material.YELLOW_STAINED_GLASS_PANE))
        adBaseInventory.setItem(6, GameManager.createItem("§e.", Material.YELLOW_STAINED_GLASS_PANE))
        adBaseInventory.setItem(7, GameManager.createItem("§e.", Material.YELLOW_STAINED_GLASS_PANE))
        adBaseInventory.setItem(8, GameManager.createItem("§fSem próxima página.", Material.RED_DYE))

        adBaseInventory.setItem(9, GameManager.createItem("§e.", Material.RED_STAINED_GLASS_PANE))
        adBaseInventory.setItem(17, GameManager.createItem("§e.", Material.RED_STAINED_GLASS_PANE))
        adBaseInventory.setItem(18, GameManager.createItem("§e.", Material.RED_STAINED_GLASS_PANE))
        adBaseInventory.setItem(26, GameManager.createItem("§e.", Material.RED_STAINED_GLASS_PANE))
        adBaseInventory.setItem(27, GameManager.createItem("§e.", Material.RED_STAINED_GLASS_PANE))
        adBaseInventory.setItem(35, GameManager.createItem("§e.", Material.RED_STAINED_GLASS_PANE))
        adBaseInventory.setItem(36, GameManager.createItem("§e.", Material.RED_STAINED_GLASS_PANE))
        adBaseInventory.setItem(44, GameManager.createItem("§e.", Material.RED_STAINED_GLASS_PANE))
        adBaseInventory.setItem(45, GameManager.createItem("§e.", Material.RED_STAINED_GLASS_PANE))
        adBaseInventory.setItem(46, GameManager.createItem("§e.", Material.RED_STAINED_GLASS_PANE))
        adBaseInventory.setItem(47, GameManager.createItem("§e.", Material.RED_STAINED_GLASS_PANE))
        adBaseInventory.setItem(48, GameManager.createItem("§e.", Material.RED_STAINED_GLASS_PANE))
        adBaseInventory.setItem(49, GameManager.createItem("§e.", Material.YELLOW_STAINED_GLASS_PANE))

        adBaseInventory.setItem(50, GameManager.createItem("§e.", Material.RED_STAINED_GLASS_PANE))
        adBaseInventory.setItem(51, GameManager.createItem("§e.", Material.RED_STAINED_GLASS_PANE))
        adBaseInventory.setItem(52, GameManager.createItem("§e.", Material.RED_STAINED_GLASS_PANE))
        adBaseInventory.setItem(53, GameManager.createItem("§e.", Material.RED_STAINED_GLASS_PANE))
    }

    private val inventories = ArrayList<Inventory>()

    private fun addInventory(inventory: Inventory) {
        inventories.add(inventory)
    }

    fun remove(inventory: Inventory) {
        inventories.remove(inventory)
    }

    fun has(inventory: Inventory): Boolean {
        return inventory in inventories
    }

    enum class InventoryType {
        MARKET,
        ADS
    }

    fun createInventory(player: Player, type: InventoryType) {
        when (type) {

            InventoryType.MARKET -> {
                val marketInventory = Bukkit.createInventory(player, 27, Component.text("§e§rMercado"))

                marketInventory.setItem(
                    12, GameManager.createItem(
                        "§aAnuncios",
                        arrayOf(" ", "§fClique para ver os anuncios"),
                        Material.KNOWLEDGE_BOOK
                    )
                )

                marketInventory.setItem(
                    14, GameManager.createItem(
                        "§bAnunciar",
                        arrayOf(" ", "§fClique para fazer um anuncio"),
                        Material.PAPER
                    )
                )

                var x = 0

                while (marketInventory.firstEmpty() != -1) {
                    if (++x % 2 == 0) {
                        marketInventory.setItem(
                            marketInventory.firstEmpty(),
                            GameManager.createItem("§a.", Material.YELLOW_STAINED_GLASS_PANE)
                        )
                    } else {
                        marketInventory.setItem(
                            marketInventory.firstEmpty(),
                            GameManager.createItem("§a.", Material.LIME_STAINED_GLASS_PANE)
                        )
                    }
                }

                addInventory(marketInventory)

                player.openInventory(marketInventory)
            }

            InventoryType.ADS -> {
                openAdInventory(player, 1)
            }

        }
    }

    fun openAdInventory(player: Player, page: Int) {
        val inventory = Bukkit.createInventory(
            player,
            adBaseInventory.size,
            Component.text("§aAnúncios §fPágina §e${page}")
        )

        inventory.contents = adBaseInventory.contents

        val ads = MarketManager.getAllAds()

        val adsPerPage = 28

        val pos = (page - 1) * adsPerPage

        if (page > 1) {
            inventory.setItem(0, GameManager.createItem("§fPágina anterior", Material.LIGHT_BLUE_DYE))

            for (i in pos until ads.size) {
                val ad = ads[i]

                if (inventory.firstEmpty() != -1) {
                    inventory.addItem(ad.icon())
                }
            }
        } else {
            ads.forEach {
                if (inventory.firstEmpty() != -1) {
                    inventory.addItem(it.icon())
                }
            }
        }

        if (inventory.firstEmpty() == -1) {
            inventory.setItem(8, GameManager.createItem("§fPróxima página", Material.LIME_DYE))
        }

        addInventory(inventory)

        player.openInventory(inventory)
    }
}