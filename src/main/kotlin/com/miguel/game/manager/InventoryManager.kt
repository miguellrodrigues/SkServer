package com.miguel.game.manager

import com.miguel.game.market.MarketManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.yaml.snakeyaml.error.Mark

object InventoryManager {

    private val adBaseInventory =
        Bukkit.createInventory(null, 54, Component.text("Anúncios").color(TextColor.color(0, 255, 0)))

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
        adBaseInventory.setItem(45, GameManager.createItem("§eAnunciantes", Material.BLUE_DYE))
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
                val marketInventory =
                    Bukkit.createInventory(player, 27, Component.text("Mercado", NamedTextColor.YELLOW))

                marketInventory.setItem(
                    12, GameManager.createItem(
                        "§aAnunciantes",
                        arrayOf(" ", "§fClique para ver os anunciantes"),
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
                openPlayersHeadsInventory(player, 1)
            }

        }
    }

    fun openPlayersHeadsInventory(player: Player, page: Int) {
        val inventory = Bukkit.createInventory(
            player,
            adBaseInventory.size,
            Component.text("Anunciantes ", NamedTextColor.GREEN)
                .append(Component.text("Página ", NamedTextColor.WHITE))
                .append(Component.text("$page", NamedTextColor.YELLOW))
        )

        inventory.contents = adBaseInventory.contents
        inventory.setItem(45, GameManager.createItem("§e.", Material.RED_STAINED_GLASS_PANE))

        val owners = MarketManager.getOwners()
        val skulls = mutableListOf<ItemStack>()

        skulls.addAll(
            owners.map { GameManager.skull(it) }
        )

        skulls.add(
            GameManager.skull("Governo")
        )

        val playersPerPage = 28

        val pos = (page - 1) * playersPerPage

        if (page > 1) {
            inventory.setItem(0, GameManager.createItem("§fPágina anterior", Material.LIGHT_BLUE_DYE))

            for (i in pos until owners.size) {
                val skull = skulls[i]

                if (inventory.firstEmpty() != -1) {
                    inventory.addItem(skull)
                }
            }
        } else {
            skulls.forEach {
                if (inventory.firstEmpty() != -1) {
                    inventory.addItem(it)
                }
            }
        }

        if (inventory.firstEmpty() == -1) {
            inventory.setItem(8, GameManager.createItem("§fPróxima página", Material.LIME_DYE))
        }

        addInventory(inventory)

        player.openInventory(inventory)
        player.playSound(
            player.location,
            Sound.BLOCK_NOTE_BLOCK_PLING,
            1f,
            1f
        )
    }

    fun openAdInventory(player: Player, owner: String, page: Int) {
        val inventory = Bukkit.createInventory(
            player,
            adBaseInventory.size,
            Component.text("Página ", NamedTextColor.WHITE)
                .append(Component.text("$page", NamedTextColor.YELLOW))
                .append(Component.text(" - ", NamedTextColor.WHITE))
                .append(Component.text(owner, NamedTextColor.YELLOW))
        )

        inventory.contents = adBaseInventory.contents


        val ads = MarketManager.getByOwner(owner)

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
        player.playSound(
            player.location,
            Sound.BLOCK_NOTE_BLOCK_PLING,
            1f,
            1f
        )
    }
}