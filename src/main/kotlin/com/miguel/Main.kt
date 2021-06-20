package com.miguel

import com.miguel.commands.GameCommands
import com.miguel.commands.common.Anuncio
import com.miguel.commands.common.Banco
import com.miguel.commands.common.Home
import com.miguel.commands.common.Mercado
import com.miguel.common.command.CommandExecutor
import com.miguel.common.command.CommandManager
import com.miguel.data.PlayerData
import com.miguel.game.bank.BankManager
import com.miguel.game.home.HomeManager
import com.miguel.game.manager.InventoryManager
import com.miguel.game.manager.TagManager
import com.miguel.game.market.MarketManager
import com.miguel.game.runnables.AutoMessage
import com.miguel.game.runnables.ItemCleaner
import com.miguel.listener.EntityEvents
import com.miguel.listener.InventoryEvents
import com.miguel.listener.PlayerEvents
import com.miguel.listener.ServerEvents
import com.miguel.mysql.MysqlManager
import com.miguel.packets.CustomPing
import com.miguel.repository.impl.Mysql
import org.bukkit.craftbukkit.v1_16_R3.CraftServer
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    companion object {
        lateinit var INSTANCE: JavaPlugin
    }

    override fun onLoad() {
        dataFolder.mkdir()

        config.options().copyDefaults(true)
        config.options().copyHeader(true)

        saveDefaultConfig()

        Mysql.remoteConnection(
            "",
            "",
            "",
            "",
            3306
        )

        Mysql.createTables()

        InventoryManager.init()
    }

    private val craftServer = (server as CraftServer)

    override fun onEnable() {
        INSTANCE = this

        MysqlManager.init()

        BankManager.loadCurrencies()
        HomeManager.init()
        MarketManager.init()

        server.pluginManager.registerEvents(PlayerEvents(), this)
        server.pluginManager.registerEvents(EntityEvents(), this)
        server.pluginManager.registerEvents(InventoryEvents(), this)
        server.pluginManager.registerEvents(ServerEvents(), this)

        craftServer.commandMap.register("sk", CommandExecutor())

        craftServer.commandMap.register("home", Home())
        craftServer.commandMap.register("mercado", Mercado())
        craftServer.commandMap.register("anuncio", Anuncio())
        craftServer.commandMap.register("banco", Banco())

        CommandManager.register(GameCommands::class.java)

        Thread(AutoMessage(120)).start()
        Thread(ItemCleaner(60)).start()

        Thread(TagManager()).start()

        CustomPing(
            this,
            arrayOf(
                "",
                "§a§k.§r §fF(x) = y §e! §a§k.§r",
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

        PlayerData.saveData()

        saveConfig()
    }
}