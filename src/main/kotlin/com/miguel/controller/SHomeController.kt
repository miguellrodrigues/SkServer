package com.miguel.controller

import com.miguel.entities.SHome
import com.miguel.repository.impl.MysqlHomeRepository
import kotlin.collections.ArrayList

class SHomeController(
    private val homeRepository: MysqlHomeRepository,
    private val locationController: SLocationsController
) {

    fun create(home: SHome): Int {
        val locationID = locationController.create(home.location, home.name)

        return homeRepository.create(home, locationID)
    }

    fun delete(home: SHome) {
        homeRepository.delete(home.id)
        locationController.delete(home.location)
    }

    fun save(home: SHome): Int {
        if (!homeRepository.exist(home.id)) {
            return create(home)
        }

        return 1
    }

    fun getPlayerHomes(id: Int): ArrayList<SHome> {
        val playerHomes = homeRepository.getPlayerHomes(id)

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