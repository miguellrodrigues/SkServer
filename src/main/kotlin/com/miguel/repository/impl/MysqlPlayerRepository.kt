package com.miguel.repository.impl

import com.miguel.entities.SPlayer
import com.miguel.entities.data.SPlayerData
import com.miguel.repository.IPlayerRepository
import java.sql.SQLException
import java.util.*
import kotlin.properties.Delegates

class MysqlPlayerRepository : IPlayerRepository {

    private val connection = Mysql.connection

    private val database = "s18280_data"
    private val table = "splayers"

    override fun create(player: SPlayerData): Boolean {
        try {
            val statement = connection.prepareStatement(
                "INSERT INTO $database.$table(uuid, account) VALUES " +
                        "('${player.uuid}', '${player.account}')"
            )

            statement.execute()
            statement.close()

            return true
        } catch (e: SQLException) {
            throw Error(e.message)
        }
    }

    override fun save(player: SPlayer): Boolean {
        return setAccount(player.uuid, player.account.id)
    }

    override fun getAccount(uuid: UUID): Int {
        if (!exist(uuid)) return 0

        var account = 0

        try {
            val statement = connection.prepareStatement(
                "SELECT account FROM $database.$table WHERE uuid='$uuid'"
            )

            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                account = resultSet.getInt(1)
            }

        } catch (e: Exception) {
            throw Error(e.message)
        }

        return account
    }

    override fun setAccount(uuid: UUID, account: Int): Boolean {
        if (!exist(uuid)) return false

        try {
            val statement =
                connection.prepareStatement("UPDATE $database.$table SET account = '$account' WHERE uuid='$uuid'")

            statement.executeUpdate()
            statement.close()

            return true
        } catch (e: SQLException) {
            throw Error(e.message)
        }
    }

    override fun getId(uuid: UUID): Int {
        var id by Delegates.notNull<Int>()

        try {
            val statement = connection.prepareStatement(
                "SELECT id FROM $database.$table WHERE uuid='$uuid'"
            )

            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                id = resultSet.getInt("id")
            }

        } catch (e: Exception) {
            throw Error(e.message)
        }

        return id
    }

    override fun exist(uuid: UUID): Boolean {
        var exist = false

        try {
            val statement = connection.prepareStatement("SELECT * FROM $database.$table WHERE uuid='$uuid'")

            val resultSet = statement.executeQuery()

            if (resultSet.next())
                exist = true

            resultSet.close()
            statement.close()
        } catch (e: SQLException) {
            throw Error(e.message)
        }

        return exist
    }

    override fun getAllUUID(): List<UUID> {
        val list: MutableList<UUID> = ArrayList()

        try {
            val statement = connection.prepareStatement(
                "SELECT uuid FROM $database.$table"
            )

            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                list.add(UUID.fromString(resultSet.getString(1)))
            }

        } catch (e: SQLException) {
            throw Error(e.message)
        }

        return list
    }
}