package com.miguel.repository

import com.miguel.entities.SPlayer
import com.miguel.entities.data.SPlayerData
import java.util.*

interface IPlayerRepository {

    fun create(player: SPlayerData): Boolean

    fun save(player: SPlayer): Boolean

    fun exist(uuid: UUID): Boolean

    fun getId(uuid: UUID): Int

    fun getAccount(uuid: UUID): Int

    fun setAccount(uuid: UUID, account: Int): Boolean

    fun getAllUUID(): List<UUID>
}