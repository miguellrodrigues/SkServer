package com.miguel.repository.impl

import com.miguel.entities.SLocation
import com.miguel.repository.ILocationRepository
import java.sql.SQLException

class MysqlLocationRepository : ILocationRepository {

    private val connection = Mysql.connection

    private val database = "s18280_data"
    private val table = "slocations"

    override fun create(location: SLocation, home_name: String): Boolean {
        if (exist(location.id)) return true

        try {
            val statement = connection.prepareStatement(
                "INSERT INTO $database.$table(home_name, world, x, y, z) VALUES ('$home_name', '${location.world}', ${location.x}, ${location.y}, ${location.z});"
            )

            statement.execute()
            statement.close()

            return true
        } catch (e: SQLException) {
            throw Error(e.message)
        }
    }

    override fun exist(id: Int): Boolean {
        var success = false

        try {
            val statement = connection.prepareStatement("SELECT * FROM $database.$table WHERE id='$id'")

            val resultSet = statement.executeQuery()

            if (resultSet.next())
                success = true

            resultSet.close()
            statement.close()
        } catch (e: SQLException) {
            throw Error(e.message)
        }

        return success
    }

    override fun getById(id: Int): SLocation? {
        if (!exist(id)) return null

        lateinit var sLocation: SLocation

        try {
            val statement = connection.prepareStatement("SELECT * FROM $database.$table WHERE id='$id'")

            val resultSet = statement.executeQuery()

            if (resultSet.next()) {
                sLocation = SLocation(
                    id = id,
                    world = resultSet.getString("world"),
                    x = resultSet.getDouble("x"),
                    y = resultSet.getDouble("y"),
                    z = resultSet.getDouble("z")
                )
            }

            resultSet.close()
            statement.close()
        } catch (e: SQLException) {
            throw Error(e.message)
        }

        return sLocation
    }

    override fun getByHomeName(name: String): SLocation {
        lateinit var sLocation: SLocation

        try {
            val statement = connection.prepareStatement("SELECT * FROM $database.$table WHERE home_name='$name'")

            val resultSet = statement.executeQuery()

            if (resultSet.next()) {
                sLocation = SLocation(
                    resultSet.getInt("id"),
                    name,
                    resultSet.getString("world"),
                    resultSet.getDouble("x"),
                    resultSet.getDouble("y"),
                    resultSet.getDouble("z")
                )
            }

            resultSet.close()
            statement.close()
        } catch (e: SQLException) {
            throw Error(e.message)
        }

        return sLocation
    }

    override fun delete(id: Int): Boolean {
        if (!exist(id)) return false

        try {
            val statement = connection.prepareStatement("DELETE FROM $database.$table WHERE id='$id'")

            statement.execute()
            statement.close()

            return true
        } catch (e: SQLException) {
            throw Error(e.message)
        }
    }
}