package com.miguel.repository

import com.miguel.entities.SAccount
import java.util.*

interface IAccountRepository {

    fun create(account: SAccount): Boolean

    fun save(account: SAccount): Boolean

    fun exist(id: Int): Boolean

    fun getById(id: Int): SAccount?

    fun getBalance(id: Int): Double

    fun setBalance(id: Int, balance: Double): Boolean

    fun getPlayerId(id: Int): UUID

    fun getByPlayerId(player_id: UUID): SAccount
}