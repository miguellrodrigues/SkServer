package com.miguel.game.manager

import com.miguel.controller.SAccountController
import com.miguel.controller.SHomeController
import com.miguel.controller.SLocationsController
import com.miguel.controller.SPlayerController
import com.miguel.entities.SAccount
import com.miguel.entities.SHome
import com.miguel.entities.SPlayer
import com.miguel.repository.impl.MysqlAccountRepository
import com.miguel.repository.impl.MysqlHomeRepository
import com.miguel.repository.impl.MysqlLocationRepository
import com.miguel.repository.impl.MysqlPlayerRepository
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.collections.HashMap

object PlayerManager {

    private val playerController = SPlayerController(
        MysqlPlayerRepository(),
        SAccountController(MysqlAccountRepository()),
        SHomeController(MysqlHomeRepository(), SLocationsController(MysqlLocationRepository()))
    )

    val data = HashMap<UUID, SPlayer>()

    fun load(player: Player) {
        data[player.uniqueId] = playerController.get(player.uniqueId)

        println(data[player.uniqueId].toString())

    /*CompletableFuture.runAsync {
            data[player.uniqueId] = playerController.get(player.uniqueId)

            println(data[player.uniqueId].toString())
        }*/
    }

    private fun getAccount(uuid: UUID): SAccount {
        return data[uuid]!!.account
    }

    fun getBalance(uuid: UUID): Double {
        return getAccount(uuid).balance
    }

    fun setBalance(uuid: UUID, balance: Double) {
        data[uuid]?.account?.balance = balance
    }

    fun increaseBalance(uuid: UUID, amount: Double) {
        setBalance(uuid, getBalance(uuid) + amount)
    }

    fun decreaseBalance(uuid: UUID, amount: Double) {
        setBalance(uuid, getBalance(uuid) - amount)
    }

    fun getHomes(uuid: UUID): List<SHome> {
        return data[uuid]!!.homes.filter { !it.delete }
    }

    fun addHome(uuid: UUID, home: SHome) {
        data[uuid]!!.homes.add(home)
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
}