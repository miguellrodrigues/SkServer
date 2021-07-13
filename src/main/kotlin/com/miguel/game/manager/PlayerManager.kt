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
                data[player.uniqueId] = CompletableFuture.supplyAsync { playerController.get(player.uniqueId) }.get()
            }
        }
    }

    private fun getAccount(uuid: UUID): SAccount? {
        return data[uuid]?.account
    }

    fun getBalance(uuid: UUID): Double {
        return getAccount(uuid)?.balance ?: .0
    }

    fun setBalance(uuid: UUID, balance: Double) {
        data[uuid]?.account?.balance = balance
    }

    fun changeBalance(uuid: UUID, amount: Double) {
        setBalance(uuid, getBalance(uuid) + amount)
    }

    fun changeBalance(account_id: Int, amount: Double) {
        playerController.accountController.changeBalance(account_id, amount)
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

    fun setAccountID(uuid: UUID, accountID: Int) {
        data[uuid]!!.account.id = accountID
    }

    fun getAccountId(player: Player): Int {
        return data[player.uniqueId]!!.account.id
    }
}