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

    override fun create(home: SHome) {
        try {
            val statement = Mysql.getMysqlConnection().prepareStatement(
                "INSERT INTO $database.$table(name, location_id, player_uuid) VALUES " +
                        "('${home.name}', '${home.location.id}', '${home.player_id}');",
                Statement.RETURN_GENERATED_KEYS
            )

            statement.execute()
            statement.close()
        } catch (e: SQLException) {
            throw Error(e.message)
        }
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

    override fun getPlayerHomes(player_id: UUID): List<SHomeData> {
        val homes = ArrayList<SHomeData>()

        try {
            val statement = Mysql.getMysqlConnection()
                .prepareStatement("SELECT * FROM $database.$table WHERE player_uuid='$player_id'")

            val resultSet = statement.executeQuery()

            while (resultSet.next()) {
                homes.add(
                    SHomeData(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("location_id"),
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