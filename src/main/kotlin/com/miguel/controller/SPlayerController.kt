package com.miguel.controller

import com.miguel.entities.SAccount
import com.miguel.entities.SPlayer
import com.miguel.game.manager.AccountManager
import com.miguel.repository.impl.MysqlPlayerRepository
import java.util.*
import java.util.concurrent.CompletableFuture

class SPlayerController(
    private val playerRepository: MysqlPlayerRepository,
    private val homeController: SHomeController
) {

    fun create(player: SPlayer) {
        playerRepository.create(player.uuid, player.account)
    }

    fun save(player: SPlayer) {
        if (playerRepository.exist(player.uuid)) {
            AccountManager.save(AccountManager.get(player.account))
        } else {
            AccountManager.create(AccountManager.get(player.account))
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
                    account = playerRepository.getAccount(uuid),
                    homes = homeController.getPlayerHomes(uuid)
                )

            } else {
                SPlayer(
                    uuid = uuid,
                    account = AccountManager.newAccount(uuid),
                    homes = arrayListOf()
                )
            }
        }
    }
}