package com.miguel.structure

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.miguel.Main
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import java.io.File
import java.nio.file.Files

object BinaryStructure {

    fun load(fileName: String): Structure? {
        val file = File(
            Main.INSTANCE.dataFolder, "structures/$fileName.dat"
        )

        if (!file.exists()) {
            return null
        }

        val bytes = Files.readAllBytes(file.toPath())
        val blocks = JsonParser.parseString(String(bytes)).asJsonArray

        val futureBlocks = blocks.map {
            val obj = it.asJsonObject
            val loc = obj["location"].asJsonObject

            FutureBlock(
                FutureLocation(
                    loc["x"].asDouble,
                    loc["y"].asDouble,
                    loc["z"].asDouble,
                    loc["yaw"].asFloat,
                    loc["pitch"].asFloat
                ),
                obj["data"].asString
            )
        }

        return Structure(futureBlocks.toMutableList())
    }

    fun save(blocks: List<Block>, fileName: String) {
        val file = File(
            Main.INSTANCE.dataFolder, "structures/$fileName.dat"
        )

        val futureBlocks = blocks.filter { it.type != Material.AIR }.map {
            FutureBlock(
                FutureLocation(
                    it.location.x,
                    it.location.y,
                    it.location.z,
                    it.location.yaw,
                    it.location.pitch
                ),
                it.blockData.asString
            )
        }.toMutableList()

        // write the structure to a binary file
        Files.write(file.toPath(), Gson().toJson(futureBlocks).toByteArray())
    }

    data class FutureLocation(
        val x: Double,
        val y: Double,
        val z: Double,
        val yaw: Float,
        val pitch: Float
    )

    class FutureBlock(
        val location: FutureLocation,
        val data: String
    ) {
        fun place(loc: Location) {
            val location = loc.clone().add(location.x, location.y, location.z)
            location.block.blockData = Bukkit.createBlockData(data)
        }
    }

    class Structure(
        private val blocks: MutableList<FutureBlock> = ArrayList()
    ) {
        fun place(location: Location) {
            if (blocks.isEmpty()) return

            blocks.forEach {
                it.place(location)
            }
        }

        fun destroy() {
            blocks.clear()
        }
    }
}