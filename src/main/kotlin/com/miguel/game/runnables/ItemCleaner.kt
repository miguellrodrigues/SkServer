package com.miguel.game.runnables

import com.miguel.game.manager.GameManager
import com.miguel.values.Strings
import org.bukkit.Bukkit
import org.bukkit.entity.Item

class ItemCleaner(private val messageInterval: Int) : BaseRunnable() {

    override var counter: Int = 240

    private val world = Bukkit.getWorld("world")!!

    override fun run() {
        while (true) {
            if (counter == 0) {
                world.entities.forEach {
                    if (it is Item)
                        it.remove()
                }

                GameManager.sendMessage("${Strings.MESSAGE_PREFIX}O chão foi limpo")

                counter = 240
            } else {
                if (counter % messageInterval == 0) {
                    GameManager.sendMessage("${Strings.MESSAGE_PREFIX}O chão será limpo em ${GameManager.formatTime(counter)}")
                }

                counter--
            }

            Thread.sleep(1000)
        }
    }
}