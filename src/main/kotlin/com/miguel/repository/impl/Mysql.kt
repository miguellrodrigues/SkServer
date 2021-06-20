package com.miguel.repository.impl

import com.mysql.cj.jdbc.MysqlDataSource
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

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

            connection = dataSource.connection

            println("Remote Mysql Connected!")
        } catch (e: Exception) {
            println("Error connecting to remote Mysql... ${e.message}")
        }
    }

    fun createTables() {
        createPlayersTable()
    }

    private fun createPlayersTable() {
        try {
            val statement = connection.prepareStatement(
                "CREATE TABLE `splayers` (\n" +
                        "\t`id` INT NOT NULL AUTO_INCREMENT,\n" +
                        "\t`uuid` VARCHAR,\n" +
                        "\t`account` INT,\n" +
                        "\tPRIMARY KEY (`id`)\n" +
                        ");"
            )


            statement.execute()
            statement.close()

        } catch (e: Exception) {
            throw Error(e.message)
        }
    }
}
