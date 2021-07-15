package com.miguel.repository

import com.miguel.entities.SAccount
import java.util.*

interface IAccountRepository {

    fun create(account: SAccount)

    fun save(account: SAccount): Boolean

    fun exist(id: String): Boolean

    fun getById(id: String): SAccount?

    fun getBalance(id: String): Double

    fun setBalance(id: String, balance: Double): Boolean
}