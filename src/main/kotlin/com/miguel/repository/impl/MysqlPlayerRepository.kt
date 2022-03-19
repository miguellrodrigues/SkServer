package com.miguel.repository.impl

import com.miguel.repository.IPlayerRepository
import com.miguel.values.Values
import java.sql.SQLException
import java.util.*

class MysqlPlayerRepository : IPlayerRepository {

    private val table = "sk_player"

    override fun create(uuid: UUID, account_id: String) {
        try {
            val statement = Mysql.getMysqlConnection().prepareStatement(
                "INSERT INTO ${Values.DATABASE}.$table(uuid, account_id) VALUES " +
                        "('${uuid}', '${account_id}')"
            )

            statement.execute()
            statement.close()
        } catch (e: SQLException) {
            throw Error(e.message)
        }
    }

    override fun getAccount(uuid: UUID): String {
        if (!exist(uuid)) return ""

        var account = ""

        try {
            val statement = Mysql.getMysqlConnection().prepareStatement(
                "SELECT account_id FROM ${Values.DATABASE}.$table WHERE uuid='$uuid'"
            )

            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                account = resultSet.getString(1)
            }

        } catch (e: Exception) {
            throw Error(e.message)
        }

        return account
    }

    override fun setAccount(uuid: UUID, account: String): Boolean {
        if (!exist(uuid)) return false

        try {
            val statement =
                Mysql.getMysqlConnection()
                    .prepareStatement("UPDATE ${Values.DATABASE}.$table SET account_id = '$account' WHERE uuid='$uuid'")

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
            val statement =
                Mysql.getMysqlConnection().prepareStatement("SELECT * FROM ${Values.DATABASE}.$table WHERE uuid='$uuid'")

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
            val statement = Mysql.getMysqlConnection().prepareStatement(
                "SELECT uuid FROM ${Values.DATABASE}.$table"
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