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
            "CREATE TABLE IF NOT EXISTS `saccounts`\n" +
                    "(\n" +
                    " `id`      int NOT NULL AUTO_INCREMENT,\n" +
                    " `balance` double NOT NULL ,\n" +
                    "\n" +
                    "PRIMARY KEY (`id`)\n" +
                    ");",

            "CREATE TABLE IF NOT EXISTS `splayers`\n" +
                    "(\n" +
                    " `id`         int NOT NULL AUTO_INCREMENT,\n" +
                    " `uuid`       varchar(45) NOT NULL ,\n" +
                    " `account_id` int NOT NULL ,\n" +
                    "\n" +
                    "PRIMARY KEY (`id`),\n" +
                    "KEY `fkIdx_14` (`account_id`),\n" +
                    "CONSTRAINT `FK_13` FOREIGN KEY `fkIdx_14` (`account_id`) REFERENCES `saccounts` (`id`)\n" +
                    ");",

            "CREATE TABLE IF NOT EXISTS `slocations`\n" +
                    "(\n" +
                    " `id`    int NOT NULL AUTO_INCREMENT,\n" +
                    " `world` varchar(45) NOT NULL ,\n" +
                    " `x`     double NOT NULL ,\n" +
                    " `y`     double NOT NULL ,\n" +
                    " `z`     double NOT NULL ,\n" +
                    "\n" +
                    "PRIMARY KEY (`id`)\n" +
                    ");",

            "CREATE TABLE IF NOT EXISTS `shomes`\n" +
                    "(\n" +
                    " `id`          int NOT NULL AUTO_INCREMENT,\n" +
                    " `name`        varchar(45) NOT NULL ,\n" +
                    " `location_id` int NOT NULL ,\n" +
                    " `player_id`   int NOT NULL ,\n" +
                    "\n" +
                    "PRIMARY KEY (`id`),\n" +
                    "KEY `fkIdx_28` (`location_id`),\n" +
                    "CONSTRAINT `FK_27` FOREIGN KEY `fkIdx_28` (`location_id`) REFERENCES `slocations` (`id`),\n" +
                    "KEY `fkIdx_31` (`player_id`),\n" +
                    "CONSTRAINT `FK_30` FOREIGN KEY `fkIdx_31` (`player_id`) REFERENCES `splayers` (`id`)\n" +
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
