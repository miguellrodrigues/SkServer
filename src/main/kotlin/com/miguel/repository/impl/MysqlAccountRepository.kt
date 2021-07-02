package com.miguel.repository.impl

import com.miguel.entities.SAccount
import com.miguel.repository.IAccountRepository
import java.sql.SQLException
import java.sql.Statement
import java.util.*
import kotlin.properties.Delegates

class MysqlAccountRepository : IAccountRepository {

    private val connection = Mysql.connection

    private val database = "s18280_data"
    private val table = "saccounts"

    override fun create(account: SAccount): Int {
        var id by Delegates.notNull<Int>()

        try {
            val statement = connection.prepareStatement(
                "INSERT INTO $database.$table(balance) VALUES " +
                        "('${account.balance}')", Statement.RETURN_GENERATED_KEYS
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

    override fun save(account: SAccount): Boolean {
        return if (exist(account.id)) {
            setBalance(account.id, account.balance)
        } else {
            create(account)
            true
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

    override fun getById(id: Int): SAccount? {
        if (!exist(id)) return null

        return SAccount(id, getBalance(id))
    }

    override fun getBalance(id: Int): Double {
        if (!exist(id)) return -1.0

        var balance = .0

        try {
            val statement = connection.prepareStatement("SELECT * FROM $database.$table WHERE id='$id'")

            val resultSet = statement.executeQuery()

            if (resultSet.next())
                balance = resultSet.getDouble("balance")

            resultSet.close()
            statement.close()
        } catch (e: SQLException) {
            throw Error(e.message)
        }

        return balance
    }

    override fun getPlayerId(id: Int): UUID {
        lateinit var uuid: UUID

        try {
            val statement = connection.prepareStatement("SELECT * FROM $database.$table WHERE id='$id'")

            val resultSet = statement.executeQuery()

            if (resultSet.next())
                uuid = UUID.fromString(resultSet.getString("player_id"))

            resultSet.close()
            statement.close()
        } catch (e: SQLException) {
            throw Error(e.message)
        }

        return uuid
    }

    override fun setBalance(id: Int, balance: Double): Boolean {
        try {
            val statement =
                connection.prepareStatement("UPDATE $database.$table SET balance = '$balance' WHERE id='$id'")

            statement.executeUpdate()
            statement.close()

            return true
        } catch (e: SQLException) {
            throw Error(e.message)
        }
    }
}