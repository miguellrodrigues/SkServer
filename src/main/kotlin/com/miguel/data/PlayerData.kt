package com.miguel.data

import com.miguel.mysql.MysqlConnector
import com.miguel.mysql.MysqlManager
import java.util.*

object PlayerData {

    private val dataMap = HashMap<UUID, Data>()

    fun loadAllData() {
        val allUUID = MysqlManager.getAllUUID()

        if (allUUID.isNotEmpty()) {
            allUUID.forEach {
                val data = Data()

                data.uuid = it
                data.money = MysqlManager.getValue(it, "money") as Double

                dataMap[it] = data
            }
        }

        MysqlConnector.closeConnection()
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
        MysqlConnector.remoteConnection("18.229.96.83", "s18280_main_data", "u18280_LosUKo2NCI", "RdSOI8OMgJO8JJT^@rmH2vOo", 3306)

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
            MysqlManager.createPlayer(uuid)

            MysqlManager.setValue(uuid, "money", money)
        }
    }
}