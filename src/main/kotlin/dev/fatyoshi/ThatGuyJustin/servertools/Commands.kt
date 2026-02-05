@file:Suppress("unused")
package dev.fatyoshi.thatguyjustin.servertools

import com.mojang.brigadier.context.CommandContext
import de.maxhenkel.admiral.annotations.Command
import de.maxhenkel.admiral.annotations.Name
import de.maxhenkel.admiral.annotations.RequiresPermission
import dev.fatyoshi.thatguyjustin.servertools.util.Logger
import dev.fatyoshi.thatguyjustin.servertools.util.getNickname
import dev.fatyoshi.thatguyjustin.servertools.util.sendMM
import dev.fatyoshi.thatguyjustin.servertools.util.sendMMResponse
import dev.fatyoshi.thatguyjustin.servertools.util.stplaySound
import net.kyori.adventure.key.Key
import dev.fatyoshi.thatguyjustin.servertools.util.toHumanReadable
import net.minecraft.commands.CommandSourceStack
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinInstant

@Command("servertools")
class Commands {
    @Command
    fun servertools(context: CommandContext<CommandSourceStack>) {
        context.sendMMResponse("<click:open_url:'https://youtu.be/ok5Ci4hAx3M?t=24'><hover:show_text:'Ligma (click here)'><#4B1888>T<#4A1888>a<#4A1788>k<#491788>e <#481688>t<#481588>h<#471588>a<#3A1554>t <#7E3696>d<#732984>e<#681C72>p<#5D0F60>r<#63126A>e<#691573>s<#6F187D>s<#761A86>i<#7C1D90>o<#822099>n<#8823A3>!</hover></click>")
    }

    @Command("nickcheck")
    fun nickCheck(context: CommandContext<CommandSourceStack>) {
        if (!context.source.isPlayer) return
        val player = context.source.player!!
        val nickname = getNickname(player)
        if (nickname != null) {
            player.sendMM("<#00FF00>Your nickname is set to: <#FFFF00>$nickname")
        } else {
            player.sendMM("<#FF0000>You do not have a nickname set.")
        }
    }

    @Command("playsoundtest")
    fun playSoundTest(context: CommandContext<CommandSourceStack>) {
        if (!context.source.isPlayer) return
        context.source.player!!.stplaySound(
            Key.key(Config.notificationSound!!.get()),
            Config.notificationVolume!!.get().toFloat(),
            Config.notificationPitch!!.get().toFloat()
        )
    }

}

@Command("uptime")
class Uptime {
    @OptIn(ExperimentalTime::class)
    @Command
    fun uptime(context: CommandContext<CommandSourceStack>) {
        val currentTime = Clock.System.now()
        val restartTime = ServerTools.instance.timerStarted!!.toInstant().toKotlinInstant() + ServerTools.instance.restartDuration!! - currentTime
        val timeDelta = currentTime - ServerTools.instance.startup.toInstant().toKotlinInstant()

        context.sendMMResponse(
            "<dark_gray><st>                              </st>\n"
                + "<aqua>     Server Uptime\n"
                + "<dark_gray><st>                              </st>\n"
                + "<gray>Started<dark_gray>:    <green>${timeDelta.toHumanReadable()} ago\n"
                + "<gray>Restarting<dark_gray>: <red>${if(ServerTools.instance.timerActive) "in ${restartTime.toHumanReadable()}" else "N/A"}\n"
                + "<dark_gray><st>                              </st>"
        )
    }
}

@Command("timer")
@RequiresPermission("servertools.timer")
class Timer {
    private val timeHover: String = "<hover:show_text:\"" +
            "<gray>Time Key<dark_gray>:\n" +
            "<aqua>d<dark_gray>: <gray>Days\n" +
            "<aqua>h<dark_gray>: <gray>Hours\n" +
            "<aqua>m<dark_gray>: <gray>Minutes\n" +
            "<aqua>s<dark_gray>: <gray>Seconds\n" +
            "<green>Example<dark_gray>:\n" +
            "<gray>1d12h30m <dark_gray>= <gray>1 Day<dark_gray>, <gray>12 Hours<dark_gray>, <gray>30 Minutes" +
            "\">"

    @Command
    fun timerHelp(context: CommandContext<CommandSourceStack>) {
        context.sendMMResponse(
            "<dark_gray><st>                                            </st>\n"
                    + "<aqua><bold>     Restart Timer Commands</bold>\n"
                    + "<dark_gray><st>                                            </st>\n"
                    + "  <dark_gray>/<gray>timer <red>stop\n"
                    + "<dark_gray>   • <gray>Stops the current timer<dark_gray>.\n"
                    + "  <dark_gray>/<gray>timer <yellow>push $timeHover<i>{time}</i></hover>\n"
                    + "<dark_gray>   • <gray>Pushes the timer back by <yellow>$timeHover<i>{time}</i></hover><dark_gray>.\n"
                    + "  <dark_gray>/<gray>timer <green>start $timeHover<i>{time}</i></hover>\n"
                    + "<dark_gray>   • <gray>Starts a new restart timer with the given time<dark_gray>.\n"
                    + "<dark_gray><st>                                            </st>"
        )
    }

    @OptIn(ExperimentalTime::class)
    @Command("stop")
    @Command("cancel")
    @RequiresPermission("servertools.timer.stop")
    fun timerStop(context: CommandContext<CommandSourceStack>) {
        if (!ServerTools.instance.timerActive){
            context.source.player!!.sendMM("<dark_red><bold>❌</bold> <red>No active timer<dark_red>.")
            return
        }
        val currentTime = Clock.System.now()
        val timeRemaining = ServerTools.instance.timerStarted!!.toInstant().toKotlinInstant() + ServerTools.instance.restartDuration!! - currentTime
        ServerTools.instance.stopTimer()
        Logger.info("Timer stopped by ${if(context.source.isPlayer) "${context.source.player!!.name.string}" else "Console"}", true)
        context.sendMMResponse("<dark_green><bold>✔</bold> <green>Active timer stopped <dark_green>(<green>${timeRemaining.toHumanReadable()} Remaining<dark_green>)<dark_green>.")
    }

    @Command("start")
    @Command("new")
    @RequiresPermission("servertools.timer.start")
    fun timerStart(context: CommandContext<CommandSourceStack>, @Name("duration") duration: String): Int {
        if (ServerTools.instance.timerActive){
            context.sendMMResponse("<dark_red><bold>❌</bold> <red>There is still an active timer<dark_red>.")
            return 1
        }

        var parsed: Duration?
        try{
            parsed = Duration.parse(duration)
        }catch(e: IllegalArgumentException){
            context.sendMMResponse("<dark_red><bold>❌</bold> <red>Could not convert <bold>\"$duration\"</bold> to a correct Duration<dark_red>.")
            return 1
        }
        ServerTools.instance.startTimer(parsed)

        context.sendMMResponse("<dark_green><bold>✔</bold> <green>New timer started and set for <bold>${parsed.toHumanReadable()}</bold><dark_green>.")
        return 1
    }

    @OptIn(ExperimentalTime::class)
    @Command("push")
    @Command("extend")
    @RequiresPermission("servertools.timer.extend")
    fun timerExtend(context: CommandContext<CommandSourceStack>, @Name("duration") duration: String): Int {
        if (!ServerTools.instance.timerActive){
            context.sendMMResponse("<dark_red><bold>❌</bold> <red>There is no active timer<dark_red>.")
            return 1
        }

        var parsed: Duration?
        try{
            parsed = Duration.parse(duration)
        }catch(e: IllegalArgumentException){
            context.sendMMResponse("<dark_red><bold>❌</bold> <red>Could not convert <bold>\"$duration\"</bold> to a correct Duration<dark_red>.")
            return 1
        }

        val currentTime = Clock.System.now()
        val timeRemaining = ServerTools.instance.timerStarted!!.toInstant().toKotlinInstant() + ServerTools.instance.restartDuration!! - currentTime

        ServerTools.instance.stopTimer()
        Logger.info("Timer pushed $parsed by ${if(context.source.isPlayer) "${context.source.player!!.name.string}" else "Console"}", true)
        ServerTools.instance.startTimer(timeRemaining.plus(parsed))

        context.sendMMResponse("<dark_green><bold>✔</bold> <green>Timer has been extended by <bold>${parsed.toHumanReadable()}</bold><dark_green>. <green>Restart is now in<dark_green>: <green><bold>${(timeRemaining + parsed).toHumanReadable()}</bold><dark_green>.")
        return 1
    }
}