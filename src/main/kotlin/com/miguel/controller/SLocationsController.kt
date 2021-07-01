package com.miguel.controller

import com.miguel.entities.SLocation
import com.miguel.repository.impl.MysqlLocationRepository

class SLocationsController(
    private val locationRepository: MysqlLocationRepository
) {

    fun create(location: SLocation): Boolean {
        return locationRepository.create(location)
    }

    fun get(id: Int): SLocation? {
        return locationRepository.getById(id)
    }
}