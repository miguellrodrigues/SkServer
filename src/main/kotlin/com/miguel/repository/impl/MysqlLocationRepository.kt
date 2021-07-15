package com.miguel.repository.impl

import com.miguel.entities.SLocation
import com.miguel.repository.ILocationRepository
import java.sql.SQLException
import java.sql.Statement

class MysqlLocationRepository : ILocationRepository {

    private val database = "s18280_data"
    private val table = "sk_location"

    override fun create(location: SLocation) {
        try {
            val statement = Mysql.getMysqlConnection().prepareStatement(
                "INSERT INTO $database.$table(id, world, x, y, z) VALUES ('${location.id}', '${location.world}', ${location.x}, ${location.y}, ${location.z});",
                Statement.RETURN_GENERATED_KEYS
            )

            statement.execute()
            statement.close()

        } catch (e: SQLException) {
            throw Error(e.message)
        }
    }

    override fun exist(id: String): Boolean {
        var success = false

        try {
            val statement = Mysql.getMysqlConnection().prepareStatement("SELECT * FROM $database.$table WHERE id='$id'")

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

    override fun getById(id: String): SLocation? {
        if (!exist(id)) return null

        lateinit var sLocation: SLocation

        try {
            val statement = Mysql.getMysqlConnection().prepareStatement("SELECT * FROM $database.$table WHERE id='$id'")

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

    override fun delete(id: String): Boolean {
        if (!exist(id)) return false

        try {
            val statement = Mysql.getMysqlConnection().prepareStatement("DELETE FROM $database.$table WHERE id='$id'")

            statement.execute()
            statement.close()

            return true
        } catch (e: SQLException) {
            throw Error(e.message)
        }
    }
}