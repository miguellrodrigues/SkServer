package com.miguel.mysql

import com.miguel.Main
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object MysqlConnector {

    private lateinit var connection: Connection

    fun remoteConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver")

            val dataSource = MysqlDataSource()

            dataSource.serverName = ""
            dataSource.port = 3306
            dataSource.databaseName = ""
            dataSource.user = ""
            dataSource.setPassword("")
            dataSource.serverTimezone = "UTC"

            this.connection = dataSource.connection
            MysqlManager.connection = this.connection
        } catch (e: Exception) {
            print("error: cant't connect to remote mysql server")
            liteConnection()
        }
    }

    fun closeConnection() {
        try {
            this.connection.close()
        } catch (e: SQLException) {
            throw Error(e.message)
        }
    }

    private fun liteConnection() {
        val file = File(Main.INSTANCE.dataFolder, "sk.db")
        if (!file.exists())
            file.createNewFile()

        val url = "jdbc:sqlite:$file"
        try {
            Class.forName("org.sqlite.JDBC")
            this.connection = DriverManager.getConnection(url)
            MysqlManager.connection = this.connection
        } catch (e: Exception) {
            throw Error(e.message)
        }
    }
}