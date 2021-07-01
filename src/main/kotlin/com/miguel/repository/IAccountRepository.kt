package com.miguel.repository

import com.miguel.entities.SAccount

interface IAccountRepository {

    fun create(account: SAccount): Boolean

    fun save(account: SAccount): Boolean

    fun exist(id: Int): Boolean

    fun getById(id: Int): SAccount?

    fun getBalance(id: Int): Double

    fun setBalance(id: Int, balance: Double): Boolean
}