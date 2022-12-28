package com.miguel.game.chunk

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.miguel.Main
import org.bukkit.Location
import java.util.UUID

object ChunkLoaderManager {

    const val globalDelayTime = 120L
    private val chunkLoaders = mutableMapOf<UUID, ChunkLoader>()

    fun add(id: UUID, chunkLoader: ChunkLoader) {
        chunkLoaders[id] = chunkLoader
        chunkLoader.run()
    }

    fun remove(id: UUID) {
        chunkLoaders.remove(id)?.stop()
    }

    fun exists(id: UUID) = chunkLoaders.containsKey(id)

    fun exists(loc: Location): Boolean {
        return chunkLoaders.values.any { it.chunk == loc.chunk }
    }

    fun get(id: UUID) = chunkLoaders[id]

    fun saveAll() {
        val gson = Gson()
        val file = Main.INSTANCE.dataFolder.resolve("chunk_loaders.json")

        if (!file.exists()) file.createNewFile()
        val chunkLoaderArray = JsonArray()

        chunkLoaders.forEach { (id, chunkLoader) ->
            val obj = JsonObject()
            val chunk = chunkLoader.chunk

            val location = JsonObject()
            location.addProperty("world", chunk.world.name)
            location.addProperty("x", chunk.x)
            location.addProperty("z", chunk.z)

            obj.addProperty("id", id.toString())
            obj.addProperty("location", gson.toJson(location))

            chunkLoaderArray.add(obj)
        }

        file.writeText(gson.toJson(chunkLoaderArray))
    }

    fun loadAll() {
        val gson = Gson()
        val file = Main.INSTANCE.dataFolder.resolve("chunk_loaders.json")

        if (!file.exists()) return

        val chunkLoadersArray = file.readText()

        gson.fromJson(chunkLoadersArray, JsonArray::class.java).forEach { chunkLoader ->
            val obj = chunkLoader.asJsonObject
            val id = UUID.fromString(obj.get("id").asString)
            val location = gson.fromJson(obj.get("location").asString, JsonObject::class.java)

            val world = Main.INSTANCE.server.getWorld(location.get("world").asString)!!
            val x = location.get("x").asInt
            val z = location.get("z").asInt

            val chunk = world.getChunkAt(x, z)

            add(id, ChunkLoader(chunk, globalDelayTime))
        }
    }

    fun getChunkLoaders() = chunkLoaders.values
}