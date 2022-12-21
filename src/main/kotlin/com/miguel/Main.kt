package com.miguel

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.miguel.commands.GameCommands
import com.miguel.commands.common.*
import com.miguel.common.command.CommandExecutor
import com.miguel.common.command.CommandManager
import com.miguel.game.bank.BankManager
import com.miguel.game.manager.InventoryManager
import com.miguel.game.manager.PlayerManager
import com.miguel.game.manager.TagManager
import com.miguel.game.market.MarketManager
import com.miguel.listener.*
import com.miguel.packets.CustomPing
import com.miguel.repository.impl.Mysql
import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    companion object {
        lateinit var INSTANCE: JavaPlugin

        lateinit var PROTOCOL_MANAGER: ProtocolManager
    }

    override fun onLoad() {
        dataFolder.mkdir()

        val structuresDir = dataFolder.resolve("structures")
        structuresDir.mkdir()

        config.options().copyDefaults(true)
        config.options().parseComments(true)

        saveDefaultConfig()

        Mysql.remoteConnection(
            config.getString("mysql.url")!!,
            config.getString("mysql.user")!!,
            config.getString("mysql.password")!!
        )

        Mysql.createTables()

        InventoryManager.init()

        PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager()
    }

    override fun onEnable() {
        INSTANCE = this

        BankManager.loadCurrencies()
        MarketManager.init()
        BankManager.init()

        server.pluginManager.registerEvents(PlayerEvents(), this)
        server.pluginManager.registerEvents(EntityEvents(), this)
        server.pluginManager.registerEvents(InventoryEvents(), this)
        server.pluginManager.registerEvents(ServerEvents(), this)
        server.pluginManager.registerEvents(BlockEvents(), this)

        val commandMap = Bukkit.getServer().commandMap

        commandMap.register("", CommandExecutor())

        commandMap.register("home", Home())
        commandMap.register("mercado", Mercado())
        commandMap.register("anuncio", Anuncio())
        commandMap.register("banco", Banco())
        commandMap.register("invsee", Invsee())
        commandMap.register("sumo", Sumo())
        commandMap.register("ping", Ping())
        commandMap.register("info", Info())
        commandMap.register("chest", Chest())
        commandMap.register("governo", Governo())
        commandMap.register("blocar", Blocar())
        commandMap.register("craft", Craft())
        commandMap.register("structure", Structure())

        CommandManager.register(GameCommands::class.java)

        //Thread(AutoMessage(120)).start()
        Thread(TagManager()).start()

        CustomPing(
            this,
            arrayOf(
                "",
                "§a§k.§r §fS = {x ∈ ℝ | x != 0} §e! §a§k.§r",
                "",
                "§fBy §bAccess_Token",
                ""
            )
        )

        server.worlds[0].setGameRule(
            GameRule.ANNOUNCE_ADVANCEMENTS,
            false
        )
    }

    override fun onDisable() {
        HandlerList.unregisterAll(this)

        PlayerManager.save()
        MarketManager.save()

        saveConfig()
    }
}