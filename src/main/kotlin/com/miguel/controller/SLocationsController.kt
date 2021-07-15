package com.miguel.controller

import com.miguel.entities.SLocation
import com.miguel.repository.impl.MysqlLocationRepository

class SLocationsController(
    private val locationRepository: MysqlLocationRepository
) {

    fun create(location: SLocation) {
        locationRepository.create(location)
    }

    fun delete(location: SLocation): Boolean {
        return locationRepository.delete(location.id)
    }

    fun get(id: String): SLocation? {
        return locationRepository.getById(id)
    }
}