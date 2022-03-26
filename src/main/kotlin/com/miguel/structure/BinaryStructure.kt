package com.miguel.structure

import com.miguel.Main
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object BinaryStructure {

    fun load(fileName: String): Structure? {
        val file = File(
            Main.INSTANCE.dataFolder, "structures/$fileName.bin"
        )

        if (!file.exists()) {
            return null
        }

        try {
            val fis = FileInputStream(file)
            val ois = ObjectInputStream(fis)

            val blocks = mutableListOf<FutureBlock>()

            while (ois.available() > 0) {
                val x = ois.readDouble()
                val y = ois.readDouble()
                val z = ois.readDouble()

                val data = ois.readUTF()

                blocks.add(FutureBlock(FutureLocation(x, y, z), data))
            }

            return Structure(blocks)
        }catch (e: Exception) {
            println("${e.message}")
            return null
        }
    }

    fun save(blocks: List<Block>, fileName: String,
             centerX: Int,
             centerY: Int,
             centerZ: Int
    ) {
        val file = File(
            Main.INSTANCE.dataFolder, "structures/$fileName.bin"
        )

        val futureBlocks = blocks.filter { it.type != Material.AIR }.map {
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
        }

        try {
            val fos = FileOutputStream(file)
            val oos = ObjectOutputStream(fos)

            futureBlocks.forEach {
                oos.writeDouble(it.location.x)
                oos.writeDouble(it.location.y)
                oos.writeDouble(it.location.z)
                oos.writeUTF(it.data)
            }

            oos.close()
        }catch (e: Exception) {
            e.printStackTrace()
        }
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