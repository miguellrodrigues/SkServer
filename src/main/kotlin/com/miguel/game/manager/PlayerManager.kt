package com.miguel.game.manager

import com.miguel.controller.SAccountController
import com.miguel.controller.SHomeController
import com.miguel.controller.SLocationsController
import com.miguel.controller.SPlayerController
import com.miguel.entities.SAccount
import com.miguel.entities.SHome
import com.miguel.entities.SLocation
import com.miguel.entities.SPlayer
import com.miguel.repository.impl.MysqlAccountRepository
import com.miguel.repository.impl.MysqlHomeRepository
import com.miguel.repository.impl.MysqlLocationRepository
import com.miguel.repository.impl.MysqlPlayerRepository
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

object PlayerManager {

    private val playerController = SPlayerController(
        MysqlPlayerRepository(),
        SAccountController(MysqlAccountRepository()),
        SHomeController(MysqlHomeRepository(), SLocationsController(MysqlLocationRepository()))
    )

    val data = HashMap<UUID, SPlayer>()

    fun load(player: Player) {
        CompletableFuture.runAsync {
            if (player.uniqueId !in data) {
                data[player.uniqueId] = playerController.get(player.uniqueId).get()
            }
        }
    }

    private fun getAccount(uuid: UUID): SAccount? {
        return data[uuid]?.account
    }
    
    fun getBalance(uuid: UUID): Double {
        return getAccount(uuid)?.balance ?: .0
    }

    private fun setBalance(uuid: UUID, balance: Double) {
        data[uuid]?.account?.balance = balance
    }

    fun changeBalance(uuid: UUID, amount: Double) {
        CompletableFuture.runAsync {
            setBalance(uuid, getBalance(uuid) + amount)
        }
    }

    fun changeBalance(account_id: String, amount: Double) {
        CompletableFuture.runAsync {
            data.values.first { it.account.id == account_id }.account.balance += amount
        }
    }

    fun isValidAccount(id: String): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync { data.values.filter { it.account.id == id }.size == 1 }
    }

    fun getHomes(uuid: UUID): List<SHome> {
        return data[uuid]!!.homes.filter { !it.delete }
    }

    private fun addHome(uuid: UUID, home: SHome) {
        data[uuid]!!.homes.add(home)
    }

    fun createHome(uuid: UUID, location: SLocation, name: String) {
        addHome(
            uuid, SHome(
                player_id = uuid,
                name = name,
                location = location
            )
        )
    }

    fun removeHome(uuid: UUID, home: SHome) {
        val index = data[uuid]!!.homes.indexOf(home)
        data[uuid]!!.homes[index].delete = true
    }

    fun save() {
        data.forEach { (_, splayer) ->
            playerController.save(splayer)
        }
    }

    fun getAccountId(uuid: UUID): String {
        return data[uuid]!!.account.id
    }

    fun isPlayerOnline(account: String): SPlayer? {
        return data.values.firstOrNull { it.account.id == account && Bukkit.getPlayer(it.uuid) != null }
    }
}