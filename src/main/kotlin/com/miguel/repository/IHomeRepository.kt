package com.miguel.repository

import com.miguel.entities.SHome
import com.miguel.entities.data.SHomeData

interface IHomeRepository {

    fun create(home: SHome, location_id: Int): Int

    fun save(home: SHome): Boolean

    fun exist(id: Int): Boolean

    fun getId(name: String): Int

    fun getPlayerHomes(player_id: Int): List<SHomeData>

    fun setPlayerId(id: Int, player_id: Int): Boolean

    fun setLocationId(id: Int, location_id: Int): Boolean

    fun delete(id: Int): Boolean
}