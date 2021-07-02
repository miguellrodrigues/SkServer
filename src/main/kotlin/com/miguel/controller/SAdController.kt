package com.miguel.controller

import com.miguel.entities.SAd
import com.miguel.repository.impl.MysqlAdRepository

class SAdController(private val repository: MysqlAdRepository) {

    fun create(ad: SAd) {
        repository.create(ad)
    }

    fun save(ad: SAd) {
        if (!repository.exist(ad.id)) {
            create(ad)
        }
    }

    fun getAll(): List<SAd> {
        return repository.getAll()
    }

    fun delete(ad: SAd) {
        repository.delete(ad.id)
    }
}