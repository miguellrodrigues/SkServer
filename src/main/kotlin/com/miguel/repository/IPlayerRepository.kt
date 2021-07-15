package com.miguel.repository

import java.util.*

interface IPlayerRepository {

    fun create(uuid: UUID, account_id: String)

    fun exist(uuid: UUID): Boolean

    fun getAccount(uuid: UUID): String

    fun setAccount(uuid: UUID, account: String): Boolean

    fun getAllUUID(): List<UUID>
}