package dev.fatyoshi.thatguyjustin.servertools

import com.mojang.brigadier.context.CommandContext
import de.maxhenkel.admiral.annotations.Command
import dev.fatyoshi.thatguyjustin.servertools.util.sendMM
import net.minecraft.commands.CommandSourceStack

@Command("servertools")
class Commands {
    @Command
    fun servertools(context: CommandContext<CommandSourceStack>) {
        if (!context.source.isPlayer) return
        context.source.player!!.sendMM("<click:open_url:'https://youtu.be/ok5Ci4hAx3M?t=24'><hover:show_text:'Ligma (click here)'><#4B1888>T<#4A1888>a<#4A1788>k<#491788>e <#481688>t<#481588>h<#471588>a<#3A1554>t <#7E3696>d<#732984>e<#681C72>p<#5D0F60>r<#63126A>e<#691573>s<#6F187D>s<#761A86>i<#7C1D90>o<#822099>n<#8823A3>!</hover></click>")
    }
}