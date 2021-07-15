package com.miguel.repository

import com.miguel.entities.SHome
import com.miguel.entities.data.SHomeData
import java.util.*

interface IHomeRepository {

    fun create(home: SHome)

    fun exist(id: Int): Boolean

    fun getPlayerHomes(player_id: UUID): List<SHomeData>

    fun delete(id: Int): Boolean
}