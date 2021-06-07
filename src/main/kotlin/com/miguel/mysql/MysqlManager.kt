package com.miguel.mysql

import com.miguel.data.PlayerData
import java.sql.Connection
import java.sql.SQLException
import java.util.*

object MysqlManager {

    private const val table = "sk_players"
    lateinit var connection: Connection

    fun init() {
        MysqlConnector.remoteConnection()

        try {
            val statement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS $table(id INT AUTO_INCREMENT PRIMARY KEY, uuid VARCHAR(255), money FLOAT)"
            )

            statement.execute()
            statement.close()
        } catch (e: SQLException) {
            throw Error(e.message)
        }

        PlayerData.loadAllData()
    }

    private fun playerExist(uuid: UUID): Boolean {
        try {
            val statement = connection.prepareStatement(
                "SELECT * FROM $table WHERE uuid=?"
            )

            statement.setString(1, uuid.toString())

            val resultSet = statement.executeQuery()
            if (resultSet.next())
                return true

            statement.close()

        } catch (e: SQLException) {
            throw Error(e.message)
        }

        return false
    }

    fun createPlayer(uuid: UUID) {
        if (playerExist(uuid))
            return

        try {
            val statement = connection.prepareStatement(
                "INSERT INTO $table(uuid, money) VALUES ('$uuid', '0.0')"
            )

            statement.execute()
            statement.close()

        } catch (e: SQLException) {
            throw Error(e.message)
        }
    }

    fun getValue(uuid: UUID, column: String): Any? {
        var value: Any? = null

        try {
            val statement = connection.prepareStatement(
                "SELECT * FROM $table WHERE uuid=?"
            )

            statement.setString(1, uuid.toString())

            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                value = resultSet.getObject(column)
            }

            statement.close()
            resultSet.close()

        } catch (e: SQLException) {
            throw Error(e.message)
        }

        return value
    }

    fun setValue(uuid: UUID, column: String, value: Any) {
        try {
            val statement = connection.prepareStatement(
                "UPDATE $table SET $column = '$value' WHERE uuid='${uuid}'"
            )

            statement.executeUpdate()
            statement.close()
        } catch (e: Exception) {
            throw Error(e.message)
        }
    }

    fun getAllUUID(): List<UUID> {
        val list: MutableList<UUID> = ArrayList()

        try {
            val statement = connection.prepareStatement(
                "SELECT * FROM $table"
            )

            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                list.add(UUID.fromString(resultSet.getString("uuid")))
            }

        } catch (e: SQLException) {
            throw Error(e.localizedMessage)
        }

        return list.toList()
    }
}