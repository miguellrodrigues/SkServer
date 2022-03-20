package com.miguel.game.manager

import com.miguel.controller.SAccountController
import com.miguel.entities.SAccount
import com.miguel.repository.impl.MysqlAccountRepository
import java.util.*
import java.util.concurrent.CompletableFuture

object AccountManager {

    private val accountController = SAccountController(MysqlAccountRepository())

    private val accounts = mutableMapOf<String, SAccount?>()

    fun load(id: String) {
        CompletableFuture.supplyAsync {
            accountController.get(id)
        }.thenAccept {
            accounts[id] = it
        }
    }

    fun exist(id: String): CompletableFuture<Boolean> {
        return accountController.exist(id)
    }

    fun save(account: SAccount) {
        accountController.save(account)
    }

    fun create(account: SAccount) {
        CompletableFuture.runAsync {
            accountController.create(account)
        }
    }

    private fun generateID(owner: UUID): String {
        return UUID.nameUUIDFromBytes("SAccount:$owner".toByteArray()).toString().substring(0, 8)
    }

    fun get(id: String): SAccount {
        if (accounts.containsKey(id)) {
            return accounts[id]!!
        }

        load(id)
        return accounts[id]!!
    }

    fun isValidAccount(id: String): Boolean {
        return accounts.containsKey(id)
    }

    fun getBalance(id: String): Double {
        return get(id).balance
    }

    fun setBalance(id: String, balance: Double) {
        accounts[id]?.balance = balance
    }

    fun changeBalance(id: String, amount: Double) {
        accounts[id]?.balance = getBalance(id) + amount
    }

    fun newAccount(owner: UUID): String {
        val id = generateID(owner)

        val account = SAccount(id, 0.0)
        create(account)

        accounts[id] = account

        return id
    }
}