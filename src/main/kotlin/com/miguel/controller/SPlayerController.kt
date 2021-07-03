package com.miguel.controller

import com.miguel.entities.SAccount
import com.miguel.entities.SPlayer
import com.miguel.game.manager.PlayerManager
import com.miguel.repository.impl.MysqlPlayerRepository
import java.util.*

class SPlayerController(
    private val playerRepository: MysqlPlayerRepository,
    val accountController: SAccountController,
    private val homeController: SHomeController
) {

    fun create(player: SPlayer) {
        val accountID = accountController.create(player.account)

        PlayerManager.setAccountID(player.uuid, accountID)

        playerRepository.create(player.uuid, accountID)
    }

    fun save(player: SPlayer) {
        if (playerRepository.exist(player.uuid)) {
            accountController.save(player.account)
        } else {
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

    fun get(uuid: UUID): SPlayer {
        return if (playerRepository.exist(uuid)) {
            SPlayer(
                uuid = uuid,
                account = accountController.get(playerRepository.getAccount(uuid))!!,
                homes = homeController.getPlayerHomes(uuid)
            )
        } else {
            SPlayer(
                uuid = uuid,
                account = SAccount(0, .0),
                homes = arrayListOf()
            )
        }
    }
}