package com.miguel.game.runnables

import com.miguel.game.manager.GameManager
import com.miguel.values.Strings

class AutoMessage(private val interval: Int) : BaseRunnable() {

    override var counter = 0

    private var messages = arrayOf(
        "${Strings.MESSAGE_PREFIX}acesse nosso servidor no discord, §ahttps://discord.gg/J33egdFkNx"
    )

    override fun run() {
        while (true) {
            if (counter % interval == 0) {
                GameManager.sendMessage(messages[(messages.indices).random()])
                counter = 0
            }

            counter++

            Thread.sleep(1000)
        }
    }
}