package com.miguel.controller

import com.miguel.entities.SLocation
import com.miguel.repository.impl.MysqlLocationRepository

class SLocationsController(
    private val locationRepository: MysqlLocationRepository
) {

    fun create(location: SLocation, home_name: String): Boolean {
        return locationRepository.create(location, home_name)
    }

    fun delete(location: SLocation) {
        return locationRepository.delete(location.id)
    }

    fun get(id: Int): SLocation? {
        return locationRepository.getById(id)
    }

    fun getByHomeName(name: String): SLocation {
        return locationRepository.getByHomeName(name)
    }
}