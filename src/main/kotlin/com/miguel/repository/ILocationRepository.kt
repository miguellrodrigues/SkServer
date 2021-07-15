package com.miguel.repository

import com.miguel.entities.SLocation

interface ILocationRepository {

    fun create(location: SLocation)

    fun exist(id: String): Boolean

    fun getById(id: String): SLocation?

    fun delete(id: String): Boolean
}