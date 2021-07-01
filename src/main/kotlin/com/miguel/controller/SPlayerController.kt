package com.miguel.controller

import com.miguel.entities.SPlayer
import com.miguel.entities.data.SPlayerData
import com.miguel.repository.impl.MysqlPlayerRepository
import java.util.*

class SPlayerController(
    private val playerRepository: MysqlPlayerRepository,
    private val accountController: SAccountController,
    private val homeController: SHomeController
) {

    fun create(player: SPlayerData): Boolean {
        return playerRepository.create(player)
    }

    fun get(uuid: UUID): SPlayer? {
        return accountController.get(playerRepository.getAccount(uuid))?.let {
            SPlayer(
                uuid,
                it,
                homeController.getPlayerHomes(uuid)
            )
        }
    }
}