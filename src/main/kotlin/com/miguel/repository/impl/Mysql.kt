package com.miguel.repository.impl

import java.sql.Connection
import java.sql.DriverManager
import java.util.*

object Mysql {

    private lateinit var connection: Connection
    private lateinit var prop: Properties

    fun remoteConnection(url: String, user: String, password: String): Boolean {
        println("Trying remote mysql connection...")

        try {
            Class.forName("com.mysql.jdbc.Driver")

            connection = DriverManager.getConnection(url, user, password)

            prop = Properties()

            prop.setProperty("url", url)
            prop.setProperty("user", user)
            prop.setProperty("password", password)

            println("Remote Mysql Connected!")
        } catch (e: Exception) {
            println("Error connecting to remote Mysql... ${e.message}")
            return false
        }

        return true
    }

    fun createTables() {
        val queryList = listOf(
            "CREATE TABLE IF NOT EXISTS `sk_account`\n" +
                    "(\n" +
                    " `id`      varchar(45) NOT NULL ,\n" +
                    " `balance` double NOT NULL ,\n" +
                    "\n" +
                    "PRIMARY KEY (`id`)\n" +
                    ");",

            "CREATE TABLE IF NOT EXISTS `sk_player`\n" +
                    "(\n" +
                    " `uuid`       varchar(45) NOT NULL ,\n" +
                    " `account_id` varchar(45) NOT NULL ,\n" +
                    "\n" +
                    "PRIMARY KEY (`uuid`),\n" +
                    "KEY `fkIdx_14` (`account_id`),\n" +
                    "CONSTRAINT `FK_13` FOREIGN KEY `fkIdx_14` (`account_id`) REFERENCES `sk_account` (`id`)\n" +
                    ");",

            "CREATE TABLE IF NOT EXISTS `sk_location`\n" +
                    "(\n" +
                    " `id`    varchar(45) NOT NULL ,\n" +
                    " `world` varchar(45) NOT NULL ,\n" +
                    " `x`     double NOT NULL ,\n" +
                    " `y`     double NOT NULL ,\n" +
                    " `z`     double NOT NULL ,\n" +
                    "\n" +
                    "PRIMARY KEY (`id`)\n" +
                    ");",

            "CREATE TABLE IF NOT EXISTS `sk_home`\n" +
                    "(\n" +
                    " `id`          int NOT NULL AUTO_INCREMENT ,\n" +
                    " `name`        varchar(45) NOT NULL ,\n" +
                    " `location_id` varchar(45) NOT NULL ,\n" +
                    " `player_uuid` varchar(45) NOT NULL ,\n" +
                    "\n" +
                    "PRIMARY KEY (`id`),\n" +
                    "KEY `fkIdx_28` (`location_id`),\n" +
                    "CONSTRAINT `FK_27` FOREIGN KEY `fkIdx_28` (`location_id`) REFERENCES `sk_location` (`id`),\n" +
                    "KEY `fkIdx_31` (`player_uuid`),\n" +
                    "CONSTRAINT `FK_30` FOREIGN KEY `fkIdx_31` (`player_uuid`) REFERENCES `sk_player` (`uuid`)\n" +
                    ");",

            "CREATE TABLE IF NOT EXISTS `sk_advertisement`\n" +
                    "(\n" +
                    " `id`             int NOT NULL ,\n" +
                    " `advertiserName` varchar(45) NOT NULL ,\n" +
                    " `name`           varchar(45) NOT NULL ,\n" +
                    " `price`          double NOT NULL ,\n" +
                    " `item`           varchar(255) NOT NULL ,\n" +
                    " `account_id`     varchar(45) NOT NULL ,\n" +
                    "\n" +
                    "PRIMARY KEY (`id`),\n" +
                    "KEY `fkIdx_49` (`account_id`),\n" +
                    "CONSTRAINT `FK_48` FOREIGN KEY `fkIdx_49` (`account_id`) REFERENCES `sk_account` (`id`)\n" +
                    ");"
        )

        queryList.forEach { createTable(it) }
    }

    fun getMysqlConnection(): Connection {
        if (this.connection.isClosed || !connection.isValid(16))
            remoteConnection(prop.getProperty("url"), prop.getProperty("user"), prop.getProperty("password"))

        return this.connection
    }

    private fun createTable(query: String) {
        try {
            val statement = connection.prepareStatement(
                query
            )

            statement.execute()
            statement.close()

        } catch (e: Exception) {
            throw Error(e.message)
        }
    }
}
