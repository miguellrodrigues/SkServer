package com.miguel.controller

import com.miguel.entities.SHome
import com.miguel.repository.impl.MysqlHomeRepository
import java.util.*
import kotlin.collections.ArrayList

class SHomeController(
    private val homeRepository: MysqlHomeRepository,
    private val locationController: SLocationsController
) {

    fun create(home: SHome): Boolean {
        locationController.create(home.location, home.name)

        val location = locationController.getByHomeName(home.name)

        return homeRepository.create(home, location.id)
    }

    fun delete(home: SHome) {
        val location = locationController.getByHomeName(home.name)

        homeRepository.delete(home.id)
        locationController.delete(location)
    }

    fun save(home: SHome): Boolean {
        if (!homeRepository.exist(home.id)) {
            return create(home)
        }

        return true
    }

    fun getPlayerHomes(uuid: UUID): ArrayList<SHome> {
        val playerHomes = homeRepository.getPlayerHomes(uuid)

        val homes = ArrayList<SHome>()

        playerHomes.forEach {
            locationController.get(it.location_id)?.let { it1 ->
                SHome(
                    it.id,
                    it.name,
                    it.player_id,
                    it1
                )
            }?.let { it2 ->
                homes.add(
                    it2
                )
            }
        }

        return homes
    }
}