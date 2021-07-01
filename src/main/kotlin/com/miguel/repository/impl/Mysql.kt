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
            "CREATE TABLE IF NOT EXISTS saccounts (\n" +
                    "  id INT NOT NULL AUTO_INCREMENT,\n" +
                    "  player_id VARCHAR(255),\n" +
                    "  balance DOUBLE,\n" +
                    "  PRIMARY KEY (id));",

            "CREATE TABLE IF NOT EXISTS splayers (\n" +
                    "  id INT NOT NULL AUTO_INCREMENT,\n" +
                    "  uuid VARCHAR(255),\n" +
                    "  account_id INT,\n" +
                    "  PRIMARY KEY (id));",

            "CREATE TABLE IF NOT EXISTS slocations (\n" +
                    "  id INT NOT NULL AUTO_INCREMENT,\n" +
                    "  home_name VARCHAR(255),\n" +
                    "  world VARCHAR(255),\n" +
                    "  x DOUBLE,\n" +
                    "  y DOUBLE,\n" +
                    "  z DOUBLE,\n" +
                    "  PRIMARY KEY (id));",

            "CREATE TABLE IF NOT EXISTS shomes (\n" +
                    "  id INT NOT NULL AUTO_INCREMENT,\n" +
                    "  name VARCHAR(255),\n" +
                    "  player_id VARCHAR(255),\n" +
                    "  location_id INT,\n" +
                    "  PRIMARY KEY (id));"
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
