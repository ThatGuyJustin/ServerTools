package dev.fatyoshi.thatguyjustin.servertools.util

import dev.fatyoshi.thatguyjustin.servertools.ServerTools
import net.kyori.adventure.audience.Audience
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import java.util.UUID

fun Player.getAudience(): Audience { return ServerTools.instance.adventure().player(this.uuid) }

fun ServerPlayer.getAudience(): Audience { return ServerTools.instance.adventure().player(this.uuid) }

fun UUID.getAudience(): Audience { return ServerTools.instance.adventure().player(this) }