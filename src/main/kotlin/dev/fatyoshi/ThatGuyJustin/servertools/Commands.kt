@file:Suppress("unused")
package dev.fatyoshi.thatguyjustin.servertools

import com.mojang.brigadier.context.CommandContext
import de.maxhenkel.admiral.annotations.Command
import dev.fatyoshi.thatguyjustin.servertools.util.sendMM
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
        if (!context.source.isPlayer) return
        context.source.player!!.sendMM("<click:open_url:'https://youtu.be/ok5Ci4hAx3M?t=24'><hover:show_text:'Ligma (click here)'><#4B1888>T<#4A1888>a<#4A1788>k<#491788>e <#481688>t<#481588>h<#471588>a<#3A1554>t <#7E3696>d<#732984>e<#681C72>p<#5D0F60>r<#63126A>e<#691573>s<#6F187D>s<#761A86>i<#7C1D90>o<#822099>n<#8823A3>!</hover></click>")
    }

    @Command("nickcheck")
    fun nickCheck(context: CommandContext<CommandSourceStack>) {
        if (!context.source.isPlayer) return
        val player = context.source.player!!
        val nickname = dev.fatyoshi.thatguyjustin.servertools.util.getNickname(player)
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
        if (!context.source.isPlayer) return
        val currentTime = Clock.System.now()
        val restartTime = ServerTools.instance.startup.toInstant().toKotlinInstant() + Duration.parse(Config.restartTime!!.get()) - currentTime
        val timeDelta = currentTime - ServerTools.instance.startup.toInstant().toKotlinInstant()

        context.source.player!!.sendMM(
            "<dark_gray><st>                              </st>\n"
                + "<aqua>     Server Uptime\n"
                + "<dark_gray><st>                              </st>\n"
                + "<gray>Started<dark_gray>:    <green>${timeDelta.toHumanReadable()} ago\n"
                + "<gray>Restarting<dark_gray>: <red>in ${restartTime.toHumanReadable()}\n"
                + "<dark_gray><st>                              </st>"
        )
    }
}