package com.miguel.common.command

import com.miguel.common.command.Permission.Companion.has
import com.miguel.manager.PlayerManager
import org.apache.commons.lang.ArrayUtils
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

class CommandExecutor : BukkitCommand("sk") {

    private val cooldownMap = HashMap<UUID, Long>()

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage(CommandManager.error.toString() + "Comando não encontrado!")
            return true
        }

        if (CommandManager.getCommand(args[0]) == null) {
            sender.sendMessage(CommandManager.error.toString() + "Comando não encontrado!")
            return true
        }

        val command = CommandManager.getCommand(args[0])!!
        val commandArgs = ArrayUtils.remove(args, 0)

        if (sender is Player && !command.player) {
            sender.sendMessage(CommandManager.error.toString() + "Este comando não pode ser executado por um player!")
            return true
        }

        if (sender is ConsoleCommandSender && !command.console) {
            sender.sendMessage(CommandManager.error.toString() + "Este comando não pode ser executado via console!")
            return true
        }

        if (command.permission != Permission.NONE && !has(command.permission, sender)) {
            sender.sendMessage(CommandManager.error.toString() + "Você não tem permissão para usar este comando!")
            return true
        }

        if (sender is Player && inCooldown(sender)) {
            val cooldown = getCooldown(sender)
            if ("." != cooldown) {
                sender.sendMessage(CommandManager.error.toString() + "Aguarde ${getCooldown(sender)} Para usar outro comando")
            }
            return true
        }

        if (commandArgs.size < command.min || commandArgs.size > command.max && command.max != -1) {
            sender.sendMessage(
              CommandManager.error
                .toString() + command.usage
            )

            return true
        }

        CommandManager.execute(command, sender, commandArgs)

        if (sender is Player) {
            if (!sender.hasPermission("sk.commandflow")) {
                setCooldown(sender, 5)
            }
        }

        return false
    }

    private fun setCooldown(player: Player, time: Long) {
        cooldownMap[player.uniqueId] = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(time)
    }

    private fun inCooldown(player: Player): Boolean {
        return cooldownMap.containsKey(player.uniqueId) && cooldownMap[player.uniqueId]!! > System.currentTimeMillis()
    }

    private fun getCooldown(player: Player): String? {
        if (inCooldown(player)) {
            val cooldown = TimeUnit.MILLISECONDS
                .toSeconds(cooldownMap[player.uniqueId]!! - System.currentTimeMillis())

            var seconds = " segundo"

            if (cooldown > 1L) seconds += "s"

            if (cooldown > 0L) return cooldown.toString() + seconds

            if (cooldown == 0L) return "."
        }

        return null
    }
}