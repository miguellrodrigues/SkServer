package com.miguel.controller

import com.miguel.entities.SAccount
import com.miguel.repository.impl.MysqlAccountRepository

class SAccountController(
    private val accountRepository: MysqlAccountRepository
) {

    fun create(account: SAccount): Boolean {
        return accountRepository.create(account)
    }

    fun get(id: Int): SAccount? {
        return accountRepository.getById(id)
    }
}