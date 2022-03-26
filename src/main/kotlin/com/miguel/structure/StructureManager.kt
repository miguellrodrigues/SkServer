package com.miguel.structure

import com.miguel.Main
import org.bukkit.Location
import org.bukkit.scheduler.BukkitRunnable
import java.util.concurrent.CompletableFuture

object StructureManager {

    private val loadedStructures = mutableMapOf<String, BinaryStructure.Structure?>()

    private fun loadStructure(name: String) {
        val structure = BinaryStructure.load(name)
        loadedStructures[name] = structure
    }

    private fun getStructure(name: String): BinaryStructure.Structure? {
        loadStructure(name)
        return loadedStructures[name]
    }

    fun placeStructure(name: String, location: Location): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            val structure = getStructure(name)

            if (structure != null) {
                object : BukkitRunnable() {
                    override fun run() {
                        structure.place(location)
                    }
                }.runTask(Main.INSTANCE)
            }

            structure != null
        }
    }

    fun getStructureNames(): List<String> {
        return loadedStructures.keys.toList()
    }

    fun isStructureLoaded(name: String): Boolean {
        return loadedStructures.containsKey(name)
    }

    fun unloadStructure(name: String) {
        getStructure(name)?.destroy()
        loadedStructures.remove(name)
    }

    fun unloadAllStructures() {
        loadedStructures.clear()
    }
}