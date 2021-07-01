package com.miguel.repository

import com.miguel.entities.SLocation

interface ILocationRepository {

    fun create(location: SLocation, home_name: String): Boolean

    fun exist(id: Int): Boolean

    fun getById(id: Int): SLocation?

    fun getByHomeName(name: String): SLocation

    fun delete(id: Int): Boolean
}