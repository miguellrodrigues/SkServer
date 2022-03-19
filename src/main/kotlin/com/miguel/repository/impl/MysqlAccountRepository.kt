package com.miguel.repository.impl

import com.miguel.entities.SAccount
import com.miguel.repository.IAccountRepository
import com.miguel.values.Values
import java.sql.SQLException

class MysqlAccountRepository : IAccountRepository {

    private val table = "sk_account"

    override fun create(account: SAccount) {
        try {
            val statement = Mysql.getMysqlConnection().prepareStatement(
                "INSERT INTO ${Values.DATABASE}.$table(id, balance) VALUES " +
                        "('${account.id}', '${account.balance}')"
            )

            statement.execute()
            statement.close()
        } catch (e: SQLException) {
            throw Error(e.message)
        }
    }

    override fun save(account: SAccount): Boolean {
        return if (exist(account.id)) {
            setBalance(account.id, account.balance)
        } else {
            create(account)
            true
        }
    }

    override fun exist(id: String): Boolean {
        var success = false

        try {
            val statement = Mysql.getMysqlConnection().prepareStatement("SELECT * FROM ${Values.DATABASE}.$table WHERE id='$id'")

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

    override fun getById(id: String): SAccount? {
        if (!exist(id)) return null

        return SAccount(id, getBalance(id))
    }

    override fun getBalance(id: String): Double {
        if (!exist(id)) return -1.0

        var balance = .0

        try {
            val statement = Mysql.getMysqlConnection().prepareStatement("SELECT * FROM ${Values.DATABASE}.$table WHERE id='$id'")

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

    override fun setBalance(id: String, balance: Double): Boolean {
        try {
            val statement =
                Mysql.getMysqlConnection()
                    .prepareStatement("UPDATE ${Values.DATABASE}.$table SET balance = '$balance' WHERE id='$id'")

            statement.executeUpdate()
            statement.close()

            return true
        } catch (e: SQLException) {
            throw Error(e.message)
        }
    }
}