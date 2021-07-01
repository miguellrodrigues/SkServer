package com.miguel

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.miguel.commands.GameCommands
import com.miguel.commands.common.Anuncio
import com.miguel.commands.common.Banco
import com.miguel.commands.common.Home
import com.miguel.commands.common.Mercado
import com.miguel.common.command.CommandExecutor
import com.miguel.common.command.CommandManager
import com.miguel.game.bank.BankManager
import com.miguel.game.home.HomeManager
import com.miguel.game.manager.InventoryManager
import com.miguel.game.manager.PlayerManager
import com.miguel.game.manager.TagManager
import com.miguel.game.market.MarketManager
import com.miguel.game.runnables.AutoMessage
import com.miguel.listener.EntityEvents
import com.miguel.listener.InventoryEvents
import com.miguel.listener.PlayerEvents
import com.miguel.listener.ServerEvents
import com.miguel.packets.CustomPing
import com.miguel.repository.impl.Mysql
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class Main : JavaPlugin() {

    companion object {
        lateinit var INSTANCE: JavaPlugin

        lateinit var PROTOCOL_MANAGER: ProtocolManager
    }

    override fun onLoad() {
        dataFolder.mkdir()

        config.options().copyDefaults(true)
        config.options().copyHeader(true)

        saveDefaultConfig()

        Mysql.remoteConnection("18.229.96.83", "s18280_data", "u18280_fnbewSuHnB", "z+6Q.PF.5cxcYl9dfkqwa!=H", 3306)
        Mysql.createTables()

        InventoryManager.init()

        PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager()
    }

    override fun onEnable() {
        INSTANCE = this

        BankManager.loadCurrencies()
        HomeManager.init()
        MarketManager.init()

        server.pluginManager.registerEvents(PlayerEvents(), this)
        server.pluginManager.registerEvents(EntityEvents(), this)
        server.pluginManager.registerEvents(InventoryEvents(), this)
        server.pluginManager.registerEvents(ServerEvents(), this)

        val commandMap = Bukkit.getServer().commandMap

        commandMap.register("sk", CommandExecutor())

        commandMap.register("home", Home())
        commandMap.register("mercado", Mercado())
        commandMap.register("anuncio", Anuncio())
        commandMap.register("banco", Banco())

        CommandManager.register(GameCommands::class.java)

        Thread(AutoMessage(120)).start()
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
    }

    override fun onDisable() {
        HandlerList.unregisterAll(this)

        MarketManager.delete()
        HomeManager.delete()

        PlayerManager.save()

        saveConfig()
    }
}