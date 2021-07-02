package com.miguel.repository.impl

import com.miguel.entities.SLocation
import com.miguel.repository.ILocationRepository
import java.sql.SQLException
import java.sql.Statement
import kotlin.properties.Delegates

class MysqlLocationRepository : ILocationRepository {

    private val connection = Mysql.connection

    private val database = "s18280_data"
    private val table = "slocations"

    override fun create(location: SLocation, home_name: String): Int {
        if (exist(location.id)) return location.id

        var id by Delegates.notNull<Int>()

        try {
            val statement = connection.prepareStatement(
                "INSERT INTO $database.$table(world, x, y, z) VALUES ('${location.world}', ${location.x}, ${location.y}, ${location.z});",
                Statement.RETURN_GENERATED_KEYS
            )

            statement.execute()
            val rs = statement.generatedKeys

            if (rs.next()) {
                id = rs.getInt(1)
            }

            rs.close()
            statement.close()

        } catch (e: SQLException) {
            throw Error(e.message)
        }

        return id
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