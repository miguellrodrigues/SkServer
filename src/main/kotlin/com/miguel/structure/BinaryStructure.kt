package com.miguel.structure

import com.google.gson.Gson
import com.miguel.Main
import org.apache.commons.io.FileUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import java.io.File

object BinaryStructure {

    fun load(fileName: String): Structure? {
        val file = File(
            Main.INSTANCE.dataFolder, "structures/$fileName.structure"
        )

        if (!file.exists()) {
            return null
        }

        val blocks = Gson().fromJson(
            FileUtils.readFileToString(file, "UTF-8"),
            Array<FutureBlock>::class.java
        )

        val futureBlocks = blocks.toMutableList()
        return Structure(futureBlocks)
    }

    fun save(blocks: List<Block>, fileName: String,
             centerX: Int,
             centerY: Int,
             centerZ: Int
    ) {
        val file = File(
            Main.INSTANCE.dataFolder, "structures/$fileName.structure"
        )

        val futureBlocks = blocks.filter { it.type != Material.AIR }.map {
            // center the coordinates of the block with respect to the center of the structure

            val x = it.location.x - centerX
            val y = it.location.y - centerY
            val z = it.location.z - centerZ

            FutureBlock(
                FutureLocation(
                    x,
                    y,
                    z,
                ),
                it.blockData.asString
            )
        }.toMutableList()

        // write the structure to a binary file
        FileUtils.writeStringToFile(
            file,
            Gson().toJson(futureBlocks),
            "UTF-8"
        )
    }

    data class FutureLocation(
        val x: Double,
        val y: Double,
        val z: Double,
    )

    class FutureBlock(
        val location: FutureLocation,
        val data: String
    ) {
        fun place(loc: Location) {
            val blockData = Bukkit.createBlockData(data)
            val location = loc.clone().add(location.x, location.y, location.z)

            location.block.blockData = blockData
        }

        fun remove(loc: Location) {
            val location = loc.clone().add(location.x, location.y, location.z)
            location.block.type = Material.AIR
        }
    }

    class Structure(
        private val blocks: MutableList<FutureBlock> = ArrayList()
    ) {
        private lateinit var placed: Location

        fun place(location: Location) {
            blocks.forEach {
                it.place(location)
            }

            placed = location
        }

        fun destroy() {
            blocks.forEach { it.remove(placed) }
            blocks.clear()
        }
    }
}