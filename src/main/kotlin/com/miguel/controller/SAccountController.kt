package com.miguel.controller

import com.miguel.entities.SAccount
import com.miguel.repository.impl.MysqlAccountRepository

class SAccountController(
    private val accountRepository: MysqlAccountRepository
) {

    fun create(account: SAccount): Int {
        return accountRepository.create(account)
    }

    fun save(account: SAccount) {
        accountRepository.save(account)
    }

    fun get(id: Int): SAccount? {
        return accountRepository.getById(id)
    }
}