package com.miguel.repository.impl

import com.miguel.entities.SPlayer
import com.miguel.repository.IPlayerRepository
import java.sql.SQLException
import java.util.*

class MysqlPlayerRepository : IPlayerRepository {

    private val connection = Mysql.connection

    private val database = "s18280_main_data"
    private val table = "splayers"

    override fun create(player: SPlayer): Boolean {
        try {
            val statement = connection.prepareStatement(
                "INSERT INTO $database.$table(uuid, money, account) VALUES " +
                        "('${player.uuid}', '${player.money}', '${player.account}')"
            )

            statement.execute()
            statement.close()

            return true
        } catch (e: SQLException) {
            throw Error(e.message)
        }
    }

    override fun save(player: SPlayer): Boolean {
        val uuid = player.uuid

        if (exist(uuid)) {
            setAccount(uuid, player.account)
        } else {
            return create(player)
        }

        return true
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
            val id = uuid.toString()
            val statement =
                connection.prepareStatement("UPDATE $database.$table SET account = '$account' WHERE uuid='$id'")

            statement.executeUpdate()
            statement.close()

            return true
        } catch (e: SQLException) {
            throw Error(e.message)
        }
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