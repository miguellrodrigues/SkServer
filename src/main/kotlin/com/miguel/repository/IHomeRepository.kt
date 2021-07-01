package com.miguel.repository

import com.miguel.entities.SHome
import com.miguel.entities.data.SHomeData
import java.util.*

interface IHomeRepository {

    fun create(home: SHomeData): Boolean

    fun save(home: SHome): Boolean

    fun exist(id: Int): Boolean

    fun getId(name: String): Int

    fun getPlayerHomes(player_id: UUID): List<SHomeData>
}