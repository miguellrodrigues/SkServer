package com.miguel.repository.impl

import com.miguel.entities.SAccount
import com.miguel.repository.IAccountRepository
import java.sql.SQLException
import java.util.*

class MysqlAccountRepository : IAccountRepository {

    private val connection = Mysql.connection

    private val database = "s18280_data"
    private val table = "saccounts"

    override fun create(account: SAccount): Boolean {
        try {
            val statement = connection.prepareStatement(
                "INSERT INTO $database.$table(player_id, balance) VALUES " +
                        "('${account.player_id}', '${account.balance}')"
            )

            statement.execute()
            statement.close()

            return true
        } catch (e: SQLException) {
            throw Error(e.message)
        }
    }

    override fun save(account: SAccount): Boolean {
        return if (exist(account.id)) {
            setBalance(account.id, account.balance)
        } else {
            create(account)
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

        return SAccount(id, getPlayerId(id), getBalance(id))
    }

    override fun getByPlayerId(player_id: UUID): SAccount {
        lateinit var account: SAccount

        try {
            val statement = connection.prepareStatement("SELECT * FROM $database.$table WHERE player_id='$player_id'")

            val resultSet = statement.executeQuery()

            if (resultSet.next()) {
                account = SAccount(
                    resultSet.getInt("id"),
                    player_id,
                    resultSet.getDouble("balance")
                )
            }

            resultSet.close()
            statement.close()
        } catch (e: SQLException) {
            throw Error(e.message)
        }

        return account
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