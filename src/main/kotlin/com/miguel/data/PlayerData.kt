package com.miguel.data

import java.util.*

object PlayerData {

    private val dataMap = HashMap<UUID, Data>()

    fun loadAllData() {
    }

    fun createData(uuid: UUID) {
        if (uuid !in dataMap) {
            val data = Data()

            data.uuid = uuid

            data.money = 0.0

            dataMap[uuid] = data
        }
    }

    fun getData(uuid: UUID): Data? {
        if (uuid in dataMap) {
            return dataMap[uuid]!!
        }

        return null
    }

    fun saveData() {
        dataMap.keys.forEach {
            val data = dataMap[it]!!

            data.save()
        }
    }

    class Data {
        lateinit var uuid: UUID

        var money = 0.0

        @Synchronized
        fun save() {

        }
    }
}