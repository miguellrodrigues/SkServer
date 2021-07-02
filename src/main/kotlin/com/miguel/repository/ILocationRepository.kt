package com.miguel.repository

import com.miguel.entities.SLocation

interface ILocationRepository {

    fun create(location: SLocation, home_name: String): Int

    fun exist(id: Int): Boolean

    fun getById(id: Int): SLocation?

    fun delete(id: Int): Boolean
}