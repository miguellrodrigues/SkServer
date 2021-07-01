package com.miguel.repository

import com.miguel.entities.SLocation

interface ILocationRepository {

    fun create(location: SLocation): Boolean

    fun exist(id: Int): Boolean

    fun getById(id: Int): SLocation?
}