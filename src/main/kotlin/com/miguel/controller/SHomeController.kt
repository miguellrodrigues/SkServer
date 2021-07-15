package com.miguel.controller

import com.miguel.entities.SHome
import com.miguel.repository.impl.MysqlHomeRepository
import java.util.*

class SHomeController(
    private val homeRepository: MysqlHomeRepository,
    private val locationController: SLocationsController
) {

    fun create(home: SHome) {
        locationController.create(home.location)

        homeRepository.create(home)
    }

    fun delete(home: SHome) {
        homeRepository.delete(home.id)
        locationController.delete(home.location)
    }

    fun save(home: SHome) {
        if (!homeRepository.exist(home.id)) {
            create(home)
        }
    }

    fun getPlayerHomes(player_id: UUID): ArrayList<SHome> {
        val playerHomes = homeRepository.getPlayerHomes(player_id)

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