package com.miguel.mysql

import com.miguel.Main
import com.mysql.cj.jdbc.MysqlDataSource
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object MysqlConnector {

    private lateinit var connection: Connection

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
            liteConnection()
        }
    }

    private fun liteConnection() {
        val file = File(Main.INSTANCE.dataFolder, "sk_server.db")
        if (!file.exists())
            file.createNewFile()

        val url = "jdbc:sqlite:$file"
        try {
            Class.forName("org.sqlite.JDBC")
            connection = DriverManager.getConnection(url)

            println("SQLite connection stabilized")
        } catch (e: Exception) {
            throw Error(e.message)
        }
    }

    fun closeConnection() {
        try {
            this.connection.close()
        } catch (e: SQLException) {
            throw Error(e.message)
        }
    }
}