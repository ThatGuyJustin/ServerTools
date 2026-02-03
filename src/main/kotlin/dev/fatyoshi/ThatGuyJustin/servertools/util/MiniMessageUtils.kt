package dev.fatyoshi.thatguyjustin.servertools.util

import dev.fatyoshi.thatguyjustin.servertools.ServerTools
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

val mm = MiniMessage.builder()
    .tags(
        TagResolver.builder()
            .resolver(StandardTags.defaults())
            .build()
    )
    .build()

fun String.mm(instance: MiniMessage = mm) = instance.deserialize(this)
fun Player.getAudience(): Audience { return ServerTools.instance.adventure().player(this.uuid) }
fun Player.sendComponent(component: Component) = this.getAudience().sendMessage(component)
fun Player.sendMM(msg: String, instance: MiniMessage = mm) = this.getAudience().sendMessage(msg.mm(instance))

fun ServerPlayer.getAudience(): Audience { return ServerTools.instance.adventure().player(this.uuid) }
fun ServerPlayer.sendComponent(component: Component) = this.getAudience().sendMessage(component)
fun ServerPlayer.sendMM(msg: String, instance: MiniMessage = mm) = this.getAudience().sendMessage(msg.mm(instance))

fun sendAll(component: Component) = ServerTools.instance.adventure().all().sendMessage(component)
fun sendAll(msg: String, instance: MiniMessage = mm) = sendAll(msg.mm(instance))