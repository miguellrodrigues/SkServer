package com.miguel.controller

import com.miguel.entities.SAccount
import com.miguel.entities.SPlayer
import com.miguel.repository.impl.MysqlPlayerRepository
import java.util.*

class SPlayerController(
    private val playerRepository: MysqlPlayerRepository,
    private val accountController: SAccountController,
    private val homeController: SHomeController
) {

    fun create(player: SPlayer): Boolean {
        return playerRepository.create(player)
    }

    fun get(uuid: UUID): SPlayer {
        return if (playerRepository.exist(uuid)) {
            SPlayer(
                id = playerRepository.getId(uuid),
                uuid = uuid,
                account = accountController.get(playerRepository.getAccount(uuid))!!,
                homes = homeController.getPlayerHomes(uuid)
            )
        } else {
            SPlayer(
                uuid = uuid,
                account = SAccount(0, .0),
                homes = emptyList()
            )
        }
    }
}