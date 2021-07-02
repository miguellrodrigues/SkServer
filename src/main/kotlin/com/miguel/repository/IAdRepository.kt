package com.miguel.repository

import com.miguel.entities.SAd

interface IAdRepository {

    fun create(ad: SAd)

    fun delete(id: Int): Boolean

    fun exist(id: Int): Boolean

    fun getAll(): List<SAd>
}