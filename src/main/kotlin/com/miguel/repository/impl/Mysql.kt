package com.miguel.repository.impl

import com.mysql.cj.jdbc.MysqlDataSource
import java.sql.Connection

object Mysql {

    lateinit var connection: Connection

    fun remoteConnection(host: String?, database: String?, username: String?, password: String?, port: Int) {
        println("Trying remote mysql connection...")

        try {
            Class.forName("com.mysql.cj.jdbc.Driver")

            val dataSource = MysqlDataSource()
            dataSource.serverName = host
            dataSource.port = port
            dataSource.databaseName = database
            dataSource.user = username
            dataSource.password = password
            dataSource.serverTimezone = "UTC"
            dataSource.autoReconnect = true

            connection = dataSource.connection

            println("Remote Mysql Connected!")
        } catch (e: Exception) {
            println("Error connecting to remote Mysql... ${e.message}")
        }
    }

    fun createTables() {
        val queryList = listOf(
            "CREATE TABLE IF NOT EXISTS `sk_account`\n" +
                    "(\n" +
                    " `id`      int NOT NULL AUTO_INCREMENT ,\n" +
                    " `balance` double NOT NULL ,\n" +
                    "\n" +
                    "PRIMARY KEY (`id`)\n" +
                    ");",

            "CREATE TABLE IF NOT EXISTS `sk_player`\n" +
                    "(\n" +
                    " `uuid`       varchar(45) NOT NULL ,\n" +
                    " `account_id` int NOT NULL ,\n" +
                    "\n" +
                    "PRIMARY KEY (`uuid`),\n" +
                    "KEY `fkIdx_14` (`account_id`),\n" +
                    "CONSTRAINT `FK_13` FOREIGN KEY `fkIdx_14` (`account_id`) REFERENCES `sk_account` (`id`)\n" +
                    ");",

            "CREATE TABLE IF NOT EXISTS `sk_location`\n" +
                    "(\n" +
                    " `id`    int NOT NULL AUTO_INCREMENT ,\n" +
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
                    " `location_id` int NOT NULL ,\n" +
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
                    " `amount`         int NOT NULL ,\n" +
                    " `material`       varchar(45) NOT NULL ,\n" +
                    " `player_uuid`    varchar(45) NOT NULL ,\n" +
                    "\n" +
                    "PRIMARY KEY (`id`),\n" +
                    "KEY `fkIdx_42` (`player_uuid`),\n" +
                    "CONSTRAINT `FK_41` FOREIGN KEY `fkIdx_42` (`player_uuid`) REFERENCES `sk_player` (`uuid`)\n" +
                    ");"
        )

        queryList.forEach { createTable(it) }
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
