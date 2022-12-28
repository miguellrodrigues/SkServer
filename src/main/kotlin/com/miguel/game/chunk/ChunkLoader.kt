package com.miguel.game.chunk

import com.miguel.Main
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

class ChunkLoader(
    val chunk: Chunk,
    private val delayTime: Long
) {

    private var running = false
    private lateinit var task: BukkitTask

    fun run() {
        if (running) return

        running = true

        task = object : BukkitRunnable() {
            override fun run() {
                Bukkit.getConsoleSender().sendMessage("Loading chunk ${chunk.x} ${chunk.z}")

                if (! chunk.isLoaded) chunk.load(true)
            }
        }.runTaskTimer(Main.INSTANCE, 0, delayTime * 20)
    }

    fun stop() {
        if (! running) return

        running = false

        task.cancel()
        chunk.unload(true)
    }
}