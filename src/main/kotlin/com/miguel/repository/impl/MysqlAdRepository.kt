package com.miguel.repository.impl

import com.miguel.entities.SAd
import com.miguel.repository.IAdRepository
import java.sql.SQLException
import java.util.*

class MysqlAdRepository : IAdRepository {

    private val connection = Mysql.connection

    private val database = "s18280_data"
    private val table = "sk_advertisement"

    override fun create(ad: SAd) {
        try {
            val statement = connection.prepareStatement(
                "INSERT INTO $database.$table(id, advertiserName, name, price, amount, material, account_id) VALUES " +
                        "('${ad.id}', '${ad.advertiserName}', '${ad.name}', '${ad.price}', '${ad.amount}', '${ad.material}', '${ad.account_id}');"
            )

            statement.execute()
            statement.close()
        } catch (e: SQLException) {
            throw Error(e.message)
        }
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

    override fun getAll(): List<SAd> {
        val ads = ArrayList<SAd>()

        try {
            val statement = connection.prepareStatement("SELECT * FROM $database.$table")

            val resultSet = statement.executeQuery()

            while (resultSet.next()) {
                ads.add(
                    SAd(
                        resultSet.getInt("id"),
                        resultSet.getString("advertiserName"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("amount"),
                        resultSet.getString("material"),
                        resultSet.getInt("account_id")
                    )
                )
            }

            resultSet.close()
            statement.close()
        } catch (e: SQLException) {
            throw Error(e.message)
        }

        return ads
    }
}