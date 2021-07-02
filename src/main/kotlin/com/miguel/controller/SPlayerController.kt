package com.miguel.controller

import com.miguel.entities.SAccount
import com.miguel.entities.SPlayer
import com.miguel.repository.impl.MysqlPlayerRepository
import java.util.*
import kotlin.properties.Delegates

class SPlayerController(
    private val playerRepository: MysqlPlayerRepository,
    private val accountController: SAccountController,
    private val homeController: SHomeController
) {

    fun create(player: SPlayer): Int {
        val accountID = accountController.create(player.account)

        return playerRepository.create(player.uuid, accountID)
    }

    fun save(player: SPlayer) {
        var playerID by Delegates.notNull<Int>()

        playerID = if (playerRepository.exist(player.uuid)) {
            accountController.save(player.account)
            player.id
        } else {
            create(player)
        }

        player.homes.forEach {
            if (it.delete) {
                homeController.delete(it)
            } else {
                it.player_id = playerID
                homeController.save(it)
            }
        }
    }

    fun get(uuid: UUID): SPlayer {
        return if (playerRepository.exist(uuid)) {
            val id = playerRepository.getId(uuid)

            SPlayer(
                id = id,
                uuid = uuid,
                account = accountController.get(playerRepository.getAccount(uuid))!!,
                homes = homeController.getPlayerHomes(id)
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