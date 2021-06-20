package com.miguel.repository

import com.miguel.entities.SHome
import java.util.*

interface IHomeRepository {

    fun create(home: SHome): Boolean

    fun save(home: SHome): Boolean

    fun exist(code: String): Boolean


}