package com.miguel.controller

import com.miguel.entities.SAccount
import com.miguel.repository.impl.MysqlAccountRepository
import java.util.*
import java.util.concurrent.CompletableFuture

class SAccountController(
    private val accountRepository: MysqlAccountRepository
) {

    private val accounts = mutableMapOf<UUID, SAccount>()

    fun create(account: SAccount) {
        return accountRepository.create(account)
    }

    fun generateID(owner: UUID): String {
        return UUID.nameUUIDFromBytes("SAccount:$owner".toByteArray()).toString().substring(0, 8)
    }

    fun exist(id: String): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync { accountRepository.exist(id) }
    }

    fun save(account: SAccount) {
        accountRepository.save(account)
    }

    fun get(id: String): SAccount? {
        return accountRepository.getById(id)
    }
}