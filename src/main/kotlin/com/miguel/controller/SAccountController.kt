package com.miguel.controller

import com.miguel.entities.SAccount
import com.miguel.repository.impl.MysqlAccountRepository
import java.util.*

class SAccountController(
    private val accountRepository: MysqlAccountRepository
) {

    fun create(account: SAccount): Boolean {
        return accountRepository.create(account)
    }

    fun save(account: SAccount) {
        accountRepository.save(account)
    }

    fun get(id: Int): SAccount? {
        return accountRepository.getById(id)
    }

    fun getByPlayerId(player_id: UUID): SAccount {
        return accountRepository.getByPlayerId(player_id)
    }
}