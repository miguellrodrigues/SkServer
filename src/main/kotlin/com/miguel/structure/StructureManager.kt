package com.miguel.structure

import java.util.concurrent.CompletableFuture

object StructureManager {

    private val loadedStructures = mutableMapOf<String, BinaryStructure.Structure?>()

    private fun loadStructure(name: String) {
        val structure = BinaryStructure.load(name)
        loadedStructures[name] = structure
    }

    fun getStructure(name: String): CompletableFuture<BinaryStructure.Structure?> {
        return CompletableFuture.supplyAsync {
            if (!isStructureLoaded(name)) {
                loadStructure(name)
            }

            loadedStructures[name]
        }
    }

    fun getStructureNames(): List<String> {
        return loadedStructures.keys.toList()
    }

    fun isStructureLoaded(name: String): Boolean {
        return loadedStructures.containsKey(name)
    }

    fun unloadStructure(name: String) {
        CompletableFuture.runAsync {
            getStructure(name).get()?.destroy()
        }
        loadedStructures.remove(name)
    }

    fun unloadAllStructures() {
        loadedStructures.clear()
    }
}