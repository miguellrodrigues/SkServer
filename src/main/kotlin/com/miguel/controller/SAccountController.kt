package com.miguel.controller

import com.miguel.entities.SAccount
import com.miguel.repository.impl.MysqlAccountRepository
import java.util.concurrent.CompletableFuture

class SAccountController(
    private val accountRepository: MysqlAccountRepository
) {

    fun create(account: SAccount): Int {
        return accountRepository.create(account)
    }

    fun exist(id: Int): Boolean {
        return accountRepository.exist(id)
    }

    fun save(account: SAccount) {
        accountRepository.save(account)
    }

    fun get(id: Int): SAccount? {
        return accountRepository.getById(id)
    }

    fun changeBalance(id: Int, balance: Double) {
        CompletableFuture.runAsync {
            accountRepository.setBalance(id, accountRepository.getBalance(id) + balance)
        }
    }
}