package com.miguel.controller

import com.miguel.entities.SHome
import com.miguel.entities.SPlayer
import com.miguel.entities.data.SHomeData
import com.miguel.repository.impl.MysqlHomeRepository
import java.util.*
import kotlin.collections.ArrayList

class SHomeController(
    private val homeRepository: MysqlHomeRepository,
    private val locationController: SLocationsController
) {

    fun create(home: SHomeData): Boolean {
        return homeRepository.create(home)
    }

    fun getPlayerHomes(uuid: UUID): List<SHome> {
        val playerHomes = homeRepository.getPlayerHomes(uuid)

        val homes = ArrayList<SHome>()

        playerHomes.forEach {
            locationController.get(it.location_id)?.let { it1 ->
                SHome(
                    it.name,
                    it1
                )
            }?.let { it2 ->
                homes.add(
                    it2
                )
            }
        }

        return homes.toList()
    }
}