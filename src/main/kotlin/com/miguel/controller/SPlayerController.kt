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
        accountController.create(player.account)

        val account = accountController.getByPlayerId(player.uuid)

        return playerRepository.create(player.uuid, account.id)
    }

    fun save(player: SPlayer) {
        if (playerRepository.exist(player.uuid)) {
            accountController.save(player.account)
        } else {
            create(player)
        }
    }

    fun get(uuid: UUID): SPlayer {
        return if (playerRepository.exist(uuid)) {
            println("Loading Existing -> $uuid")

            SPlayer(
                id = playerRepository.getId(uuid),
                uuid = uuid,
                account = accountController.get(playerRepository.getAccount(uuid))!!,
                homes = homeController.getPlayerHomes(uuid)
            )
        } else {
            SPlayer(
                uuid = uuid,
                account = SAccount(0, uuid, .0),
                homes = emptyList()
            )
        }
    }
}