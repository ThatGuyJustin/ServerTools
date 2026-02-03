package dev.fatyoshi.thatguyjustin.servertools.util

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import java.util.UUID

fun playSound(audience: Audience, source: Sound.Source, soundType: Key, volume: Float, pitch: Float) = audience.playSound(
    Sound.sound(
        soundType,
        source,
        volume,
        pitch
    )
)

fun Audience.stPlaySound(sound: Key, volume: Float, pitch: Float) = playSound(
    this,
    Sound.Source.MASTER,
    sound,
    volume,
    pitch
)

fun Player.stplaySound(sound: Key, volume: Float, pitch: Float) = this.getAudience().stPlaySound(
    sound,
    volume,
    pitch
)

fun ServerPlayer.stplaySound(sound: Key, volume: Float, pitch: Float) = this.getAudience().stPlaySound(
    sound,
    volume,
    pitch
)

fun UUID.stplaySound(sound: Key, volume: Float, pitch: Float) = this.getAudience().stPlaySound(
    sound,
    volume,
    pitch
)