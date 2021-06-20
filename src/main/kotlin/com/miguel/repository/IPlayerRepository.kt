package com.miguel.repository

import com.miguel.entities.SPlayer
import java.util.*

interface IPlayerRepository {

    fun create(player: SPlayer): Boolean

    fun save(player: SPlayer): Boolean

    fun exist(uuid: UUID): Boolean

    fun getAccount(uuid: UUID): Int

    fun setAccount(uuid: UUID, account: Int): Boolean

    fun getAllUUID(): List<UUID>
}