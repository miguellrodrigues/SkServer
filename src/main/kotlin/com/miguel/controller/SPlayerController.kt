package com.miguel.controller

import com.miguel.entities.SAccount
import com.miguel.entities.SPlayer
import com.miguel.repository.impl.MysqlPlayerRepository
import java.util.*
import java.util.concurrent.CompletableFuture

class SPlayerController(
    private val playerRepository: MysqlPlayerRepository,
    val accountController: SAccountController,
    private val homeController: SHomeController
) {

    fun create(player: SPlayer) {
        playerRepository.create(player.uuid, player.account.id)
    }

    fun save(player: SPlayer) {
        if (playerRepository.exist(player.uuid)) {
            accountController.save(player.account)
        } else {
            accountController.create(player.account)
            create(player)
        }

        player.homes.forEach {
            if (it.delete) {
                homeController.delete(it)
            } else {
                homeController.save(it)
            }
        }
    }

    fun get(uuid: UUID): CompletableFuture<SPlayer> {
        return CompletableFuture.supplyAsync {
            if (playerRepository.exist(uuid)) {
                SPlayer(
                    uuid = uuid,
                    account = accountController.get(playerRepository.getAccount(uuid))!!,
                    homes = homeController.getPlayerHomes(uuid)
                )
            } else {
                SPlayer(
                    uuid = uuid,
                    account = SAccount(
                        accountController.generateID(uuid),
                        .0
                    ),
                    homes = arrayListOf()
                )
            }
        }
    }
}