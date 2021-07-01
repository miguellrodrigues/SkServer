package com.miguel.repository.impl

import com.miguel.entities.SHome
import com.miguel.entities.data.SHomeData
import com.miguel.repository.IHomeRepository
import java.sql.SQLException
import java.util.*
import kotlin.properties.Delegates

class MysqlHomeRepository : IHomeRepository {

    private val connection = Mysql.connection

    private val database = "s18280_data"
    private val table = "shomes"

    override fun create(home: SHome): Boolean {
        try {
            val statement = connection.prepareStatement(
                "INSERT INTO $database.$table(player_id, location_id) VALUES " +
                        "('${home.owner}', '${home.location.id}')"
            )

            statement.execute()
            statement.close()

            return true
        } catch (e: SQLException) {
            throw Error(e.message)
        }
    }

    override fun save(home: SHome): Boolean {
        if (exist(home.id)) {
            setPlayerId(home.id, home.owner)
            setLocationId(home.id, home.location.id)
        } else {
            return create(
                home
            )
        }

        return true
    }

    override fun exist(id: Int): Boolean {
        var success = false

        try {
            val statement = connection.prepareStatement("SELECT * FROM $database.$table WHERE id='?'")

            statement.setInt(1, id)
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

    override fun getId(name: String): Int {
        var id by Delegates.notNull<Int>()

        try {
            val statement = connection.prepareStatement("SELECT * FROM $database.$table WHERE name='?'")

            statement.setString(1, name)
            val resultSet = statement.executeQuery()

            if (resultSet.next())
                id = resultSet.getInt("id")

            resultSet.close()
            statement.close()
        } catch (e: SQLException) {
            throw Error(e.message)
        }

        return id
    }

    override fun getPlayerHomes(player_id: UUID): List<SHomeData> {
        val homes = ArrayList<SHomeData>()

        try {
            val statement = connection.prepareStatement("SELECT * FROM $database.$table WHERE player_id='?'")

            statement.setString(1, player_id.toString())
            val resultSet = statement.executeQuery()

            while (resultSet.next()) {
                homes.add(
                    SHomeData(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        player_id,
                        resultSet.getInt("location_id")
                    )
                )
            }

            resultSet.close()
            statement.close()
        } catch (e: SQLException) {
            throw Error(e.message)
        }

        return homes
    }

    override fun setPlayerId(id: Int, player_id: UUID): Boolean {
        try {
            val statement =
                connection.prepareStatement("UPDATE $database.$table SET player_id = '$player_id' WHERE id='$id'")

            statement.executeUpdate()
            statement.close()

            return true
        } catch (e: SQLException) {
            throw Error(e.message)
        }
    }

    override fun setLocationId(id: Int, location_id: Int): Boolean {
        try {
            val statement =
                connection.prepareStatement("UPDATE $database.$table SET location_id = '$location_id' WHERE id='$id'")

            statement.executeUpdate()
            statement.close()

            return true
        } catch (e: SQLException) {
            throw Error(e.message)
        }
    }
}