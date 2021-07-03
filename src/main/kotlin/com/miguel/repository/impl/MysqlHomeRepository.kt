package com.miguel.repository.impl

import com.miguel.entities.SHome
import com.miguel.entities.data.SHomeData
import com.miguel.repository.IHomeRepository
import java.sql.SQLException
import java.sql.Statement
import java.util.*
import kotlin.properties.Delegates

class MysqlHomeRepository : IHomeRepository {

    private val database = "s18280_data"
    private val table = "sk_home"

    override fun create(home: SHome, location_id: Int): Int {
        var id by Delegates.notNull<Int>()

        try {
            val statement = Mysql.getMysqlConnection().prepareStatement(
                "INSERT INTO $database.$table(name, location_id, player_uuid) VALUES " +
                        "('${home.name}', '${location_id}', '${home.player_id}');",
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

    override fun save(home: SHome): Boolean {
        return setPlayerId(home.id, home.player_id) && setLocationId(home.id, home.location.id)
    }

    override fun exist(id: Int): Boolean {
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

    override fun getId(name: String): Int {
        var id by Delegates.notNull<Int>()

        try {
            val statement = Mysql.getMysqlConnection().prepareStatement("SELECT * FROM $database.$table WHERE name='?'")

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
            val statement = Mysql.getMysqlConnection().prepareStatement("SELECT * FROM $database.$table WHERE player_uuid='$player_id'")

            val resultSet = statement.executeQuery()

            while (resultSet.next()) {
                homes.add(
                    SHomeData(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("location_id"),
                        player_id
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
                Mysql.getMysqlConnection().prepareStatement("UPDATE $database.$table SET player_id = '$player_id' WHERE id='$id'")

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
                Mysql.getMysqlConnection().prepareStatement("UPDATE $database.$table SET location_id = '$location_id' WHERE id='$id'")

            statement.executeUpdate()
            statement.close()

            return true
        } catch (e: SQLException) {
            throw Error(e.message)
        }
    }

    override fun delete(id: Int): Boolean {
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