package dev.fatyoshi.thatguyjustin.servertools.util

import dev.ftb.mods.ftbessentials.util.FTBEPlayerData
import net.minecraft.world.entity.player.Player

fun getNickname(player: Player): String? {
    try {
        val data = FTBEPlayerData.getOrCreate(player)
        val nick = data?.get()?.nick
        if (!nick.isNullOrEmpty()) {
            return nick
        }
        return null
    } catch (e: NoClassDefFoundError) {
        return null
    } catch (e: Exception) {
        Logger.error("Error getting nickname for player ${player.uuid}:", true)
        e.printStackTrace()
        return null
    }
}