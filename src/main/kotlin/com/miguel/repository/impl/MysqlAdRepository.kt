package com.miguel.repository.impl

import com.miguel.entities.SAd
import com.miguel.repository.IAdRepository
import com.miguel.values.Values
import java.sql.SQLException
import java.util.*

class MysqlAdRepository : IAdRepository {

    private val table = "sk_advertisement"

    private val b64encoder = Base64.getEncoder()
    private val b64decoder = Base64.getDecoder()

    override fun create(ad: SAd) {
        try {
            val item = b64encoder.encodeToString(ad.item)

            val statement = Mysql.getMysqlConnection().prepareStatement(
                "INSERT INTO ${Values.DATABASE}.$table(id, advertiserName, name, price, item, account_id) VALUES " +
                        "('${ad.id}', '${ad.advertiserName}', '${ad.name}', '${ad.price}', '$item', '${ad.account_id}');"
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
            val statement = Mysql.getMysqlConnection().prepareStatement("DELETE FROM ${Values.DATABASE}.$table WHERE id='$id'")

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

    override fun getAll(): List<SAd> {
        val ads = ArrayList<SAd>()

        try {
            val statement = Mysql.getMysqlConnection().prepareStatement("SELECT * FROM ${Values.DATABASE}.$table")

            val resultSet = statement.executeQuery()

            while (resultSet.next()) {
                ads.add(
                    SAd(
                        resultSet.getInt("id"),
                        resultSet.getString("advertiserName"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price"),
                        b64decoder.decode(resultSet.getString("item")),
                        resultSet.getString("account_id")
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