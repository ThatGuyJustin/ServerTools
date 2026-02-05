package dev.fatyoshi.thatguyjustin.servertools.discord

import dev.fatyoshi.thatguyjustin.servertools.Config
import dev.fatyoshi.thatguyjustin.servertools.ServerTools
import dev.fatyoshi.thatguyjustin.servertools.WhitelistConfig
import dev.fatyoshi.thatguyjustin.servertools.util.Logger
import dev.fatyoshi.thatguyjustin.servertools.util.sendAll
import dev.fatyoshi.thatguyjustin.servertools.util.toHex
import dev.minn.jda.ktx.interactions.commands.*
import dev.minn.jda.ktx.jdabuilder.intents
import dev.minn.jda.ktx.jdabuilder.light
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.AttachedFile
import net.dv8tion.jda.api.utils.data.DataArray
import net.dv8tion.jda.api.utils.data.DataObject
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.server.ServerLifecycleHooks
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import oshi.SystemInfo
import java.io.File
import java.io.IOException
import java.lang.Double
import java.util.*
import java.util.function.Consumer
import kotlin.io.path.absolute
import kotlin.io.path.name
import kotlin.time.Duration

class DiscordHandler(private var startup: Date): ListenerAdapter() {
    private lateinit var chatChannel: TextChannel
    lateinit var botClient: JDA

    companion object {
        val mentionRegex = Regex("(@\\w+)")
    }

    init{
        val token = Config.botToken?.get()

        if(token == null){
            Logger.error("&7[&dDiscord&7] &fBlank token found. Please insert a token into the config and reboot to continue!", true)
        } else {
            Logger.info("&7[&dDiscord&7] &fAttempting Discord Login...", true)
            this.botClient = light(token, enableCoroutines=true) {
                intents += listOf(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
            }
            this.botClient.getPresence().setActivity(Activity.watching("The server burn...🔥"))
            this.botClient.addEventListener(this)
        }

        botClient.updateCommands {
            slash("tps", "Gets the current server TPS.")
            slash("server", "Get current server information.")
            slash("list", "Gets a list of all the online players.")
            slash("ping", "Cookie!")
            slash("disconnect", "Force disconnect yourself if your client crashes but your user is still connected to the server.") {
                option<String>("username", "Your Username", true)
            }
            slash("whitelist", "Whitelist yourself! [Note: Gets Logged]") {
                subcommand("add", "Add to whitelist") {
                    restrict(true)
                    option<String>("username", "Username", true)
                }
                subcommand("remove", "Remove from whitelist.") {
                    restrict(true, Permission.ADMINISTRATOR)
                    option<String>("username", "Username", true)
                }
                subcommand("status", "Check whitelist status of a member.") {
                    option<User>("user", "User to check, or blank to see your own status.")
                }
            }
        }.queue()
    }

    fun sendWebHookMessage(p: ServerPlayer, msg: String?) {

        if(!Config.enabledChatBridge!!.get() || Config.webhookURL!!.get() == "") return

        val payload = DataObject.empty().put("content", msg)

        val avatarUrl = Config.avatarSkinProxy!!.get().replace("%uuid%", p.uuid.toString()).replace("%uuid-no-dash%", p.uuid.toString().replace("-", ""))

        payload.put("username", p.name.string)
        payload.put("avatar_url", avatarUrl)

        payload.put("allowed_mentions",
            DataObject.empty().put("parse", DataArray.empty().add("users")))

        val request = Request.Builder()
            .url(Config.webhookURL!!.get())
            .post(payload.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        botClient.httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Logger.error("Unable to send webhook message.", true)
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.close()
            }
        })

    }

    fun shutdown() {
        this.botClient.shutdown()
    }

    override fun onReady(event: ReadyEvent) {
        Logger.info("&7[&dDiscord&7] &fLogged into Discord as ${event.jda.selfUser.name}#${event.jda.selfUser.discriminator} (${event.jda.selfUser.id})",  true)

        if(Config.enabledChatBridge!!.get()) {
            val chatChannel: String = Config.chatChannel?.get() ?: return

            try {
                this.chatChannel = botClient.getTextChannelById(Config.chatChannel!!.get())!!
            } catch (e: NullPointerException) {
                Logger.error("&7[&dDiscord&7] &fUnable to get chat channel: Null Pointer Exception", true)
                e.printStackTrace()
            }

        }

        if (Config.loggingChannel!!.get() != "") {
            val logs = botClient.getTextChannelById(Config.loggingChannel!!.get())
            val msg = String.format(
                "[<t:%s:T>] Server has started",
                Date().time / 1000
            )
            try {
                logs!!.sendMessage(msg).queue()
            } catch (e: InsufficientPermissionException) {
                Logger.error("Unable to post in log channel:", true)
                e.printStackTrace()
            }
        }

    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (!event.isFromGuild || event.author.isBot) return
        var isAdmin = false

        for (role in event.member!!.roles) {
            if (Config.admin_roles!!.get().contains(role.id)) {
                isAdmin = true
            }
        }

        if (!Config.enabledChatBridge!!.get()) return
        if (event.channel.id != chatChannel.id) return

        val discordColor = "#5865F2"
        val memberColor = event.member!!.colors.primary?.toHex() ?: "#99aab5"
        val user = "<color:$memberColor>${event.member!!.nickname ?: event.author.globalName ?: event.author.name}<reset>"


        var peopleToPing = mutableListOf<String>()
        var tmpMsg = event.message.contentDisplay

        if(mentionRegex.containsMatchIn(event.message.contentDisplay)){
            mentionRegex.findAll(tmpMsg).forEach { match ->
                if(ServerLifecycleHooks.getCurrentServer()!!.playerList.playerNamesArray.contains(match.value.replace("@", ""))) {
                    if(!peopleToPing.contains(match.value.replace("@", "")))
                        peopleToPing.add(match.value.replace("@", ""))
                    tmpMsg = tmpMsg.replace(match.value, "<green>${match.value}<gray>")
                }
            }
        }

        val msg = "<dark_gray>[<color:$discordColor>D<dark_gray>] $user <dark_gray>» <gray>${tmpMsg}"

        sendAll(msg)
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val server = ServerLifecycleHooks.getCurrentServer()!!
        when(event.name) {
            "ping" -> {
                val time = System.currentTimeMillis()
                event.reply("Pong!").setEphemeral(true).queue { response: InteractionHook ->
                    response.editOriginal(
                        String.format(
                            "Pong: %d ms",
                            System.currentTimeMillis() - time
                        )
                    ).queue()
                }
                return
            }
            "tps" -> {
                event.reply("**TPS**: `${ServerTools.instance.getTPS()}`").setEphemeral(true).queue()
            }
            "server" -> {
                val maxMemory = Runtime.getRuntime().maxMemory()
                val totalMemory = Runtime.getRuntime().totalMemory()
                val freeMemory = Runtime.getRuntime().freeMemory()

                val si = SystemInfo()
                val averageUsage = si.hardware.processor.getSystemLoadAverage(3)
                val cpuUsage =
                    si.hardware.processor.getSystemCpuLoadBetweenTicks(si.hardware.processor.systemCpuLoadTicks)

                val server = ServerLifecycleHooks.getCurrentServer()!!

                val used = ((totalMemory - freeMemory) / 1024 / 1024).toInt()
                val total = (totalMemory / 1024 / 1024).toInt()
                val allocated = (maxMemory / 1024 / 1024).toInt()
                val percent = used.toDouble() / total.toDouble() * 100.00

                val players = Arrays.asList(*server.playerList.playerNamesArray)
                Collections.sort(players)

                val cpu = """
     `${String.format("%.2f%%", cpuUsage)}` Current
     `
     """.trimIndent() + String.format(
                    "%.2f%%",
                    averageUsage[0]
                ) + "` 1m\n`" + String.format("%.2f%%", averageUsage[1]) + "` 5m\n`" + String.format(
                    "%.2f%%",
                    averageUsage[2]
                ) + "` 15m"

                val e = EmbedBuilder().setTitle("Server Status").setDescription("**MOTD**: `" + server.motd + '`')
                    .addField("TPS", Double.toString(ServerTools.instance.getTPS()), true)
                    .addField(
                        "Ram Usage (" + allocated + "MB Allocated)",
                        String.format("%s MB/%s MB (`%.1f%%`)", used, total, percent),
                        true
                    )
                    .addField("CPU Usage", cpu, true)
                    .addField(
                        "Timer",
                        String.format(
                            "**Started at**: <t:%1\$s:T> (<t:%1\$s:R>)\n**Restart At**: <t:%2\$s:T> (<t:%2\$s:R>)",
                            startup.time / 1000,
                            startup.time / 1000 + (Duration.parse(Config.restartTime!!.get()).inWholeSeconds)
                        ),
                        false
                    )
                    .addField(
                        String.format("Online Players (%s/%s)", server.playerCount, server.maxPlayers),
                        String.format("``` %s ```", java.lang.String.join(" ", players)),
                        false
                    )

                val path =
                    server.serverDirectory.absolute().name.substring(0, server.serverDirectory.absolute().name.length - 1)
                if (File("$path/server-icon.png").exists()) {
                    e.setThumbnail("attachment://server-icon.png")
                    event.replyEmbeds(e.build())
                        .addFiles(AttachedFile.fromData(File("$path/server-icon.png"), "server-icon.png"))
                        .setEphemeral(true).queue()
                } else {
                    event.replyEmbeds(e.build()).setEphemeral(true).queue()
                }
            }
            "list" -> {
                val players = server.playerList.playerNamesArray

                if (players.size == 0) event.reply("There is nobody online :(").setEphemeral(true)
                    .queue() else event.reply("`Online Players`: " + java.lang.String.join(", ", *players))
                    .setEphemeral(true).queue()
            }
            "disconnect", "auggie" -> {
                val playerName = event.getOption("username")!!.asString
                server.playerList.players.forEach(Consumer<ServerPlayer> { player: ServerPlayer ->
                    if (player.name.string == playerName) player.connection.connection.channel().disconnect()
                })
                event.reply("The player specified *should* be disconnected...").setEphemeral(true).queue()

                if (Config.loggingChannel!!.get() != "") {
                    val logs = botClient.getTextChannelById(Config.loggingChannel!!.get())
                    val msg = String.format(
                        "[<t:%s:T>] %s `%s` has used the command `/auggie %s` in channel %s",
                        Date().time / 1000, event.user.asMention, event.user.id, playerName, event.channel.asMention
                    )
                    try {
                        logs!!.sendMessage(msg).setAllowedMentions(HashSet()).queue()
                    } catch (e: InsufficientPermissionException) {
                        Logger.error("Unable to post in log channel:", true)
                        e.printStackTrace()
                    }
                }
            }
            "whitelist" -> {
                // Load the whitelist before doing anything else
                val whitelistCfg = WhitelistConfig().load()
                var list = whitelistCfg.users[event.user.id] ?: mutableListOf()
                when(event.subcommandName){
                    "add" -> {
                        if(list.size >= Config.max_whitelist_per_player!!.get() && event.member!!.roles.intersect(Config.admin_roles!!.get().toSet()).isEmpty()){
                            event.reply("Sorry, you have already whitelisted the max number of players.").setEphemeral(true).queue()
                        }else{
                            // Convert from username to UUID
                            val username = event.getOption("username")!!.asString
                            val uuid = resolveUUID(username)
                            if(uuid == null){
                                event.reply("Player $username can't be found.").setEphemeral(true).queue()
                            }else{
                                ServerLifecycleHooks.getCurrentServer()!!.commands.dispatcher.execute("whitelist add $username", ServerLifecycleHooks.getCurrentServer()!!.createCommandSourceStack())
                                event.reply("Player $username has been whitelisted.").setEphemeral(true).queue()
                                list.add(uuid.toString())
                                whitelistCfg.users[event.user.id] = list
                                WhitelistConfig().save(whitelistCfg)

                                if (Config.loggingChannel!!.get() != "") {
                                    val logs = botClient.getTextChannelById(Config.loggingChannel!!.get())
                                    try {
                                        logs!!.sendMessage("[<t:${Date().time / 1000}:T>] ${event.user.asMention} `${event.user.id}` has whitelisted `${username}`").setAllowedMentions(HashSet()).queue()
                                    } catch (e: InsufficientPermissionException) {
                                        Logger.error("Unable to post in log channel:", true)
                                        e.printStackTrace()
                                    }
                                }
                            }
                        }
                    }
                    "remove" -> {
                        val username = event.getOption("username")!!.asString
                        val uuid = resolveUUID(username)
                        if(!list.contains(uuid.toString())){
                            event.reply("Sorry, you are not the person who whitelisted this player.").setEphemeral(true).queue()
                        }else{
                            // Convert from username to UUID
                            if(uuid == null){
                                event.reply("Player $username can't be found.").setEphemeral(true).queue()
                            }else{
                                ServerLifecycleHooks.getCurrentServer()!!.commands.dispatcher.execute("whitelist remove $username", ServerLifecycleHooks.getCurrentServer()!!.createCommandSourceStack())
                                event.reply("Player $username has been removed from the whitelist.").setEphemeral(true).queue()
                                list.remove(uuid.toString())
                                whitelistCfg.users[event.user.id] = list
                                WhitelistConfig().save(whitelistCfg)

                                if (Config.loggingChannel!!.get() != "") {
                                    val logs = botClient.getTextChannelById(Config.loggingChannel!!.get())
                                    try {
                                        logs!!.sendMessage("[<t:${Date().time / 1000}:T>] ${event.user.asMention} `${event.user.id}` has un-whitelisted `${username}`").setAllowedMentions(HashSet()).queue()
                                    } catch (e: InsufficientPermissionException) {
                                        Logger.error("Unable to post in log channel:", true)
                                        e.printStackTrace()
                                    }
                                }
                            }
                        }
                    }
                    "status" -> {
                        val players = whitelistCfg.users[event.user.id]
                        if(players == null){
                            event.reply("No whitelisted players found for you.").setEphemeral(true).queue()
                        }else{
                            val allPlayers = players.joinToString(", ") { player -> resolveUsername(UUID.fromString(player)) }
                            event.reply("Player(s) You've whitelisted: `${allPlayers}`").setEphemeral(true).queue()
                        }
                    }
                }
            }
        }

    }

    private fun resolveUUID(username: String): UUID? {
        val profile = ServerLifecycleHooks.getCurrentServer()!!.profileCache?.get(username)?.orElse(null)
        return profile?.id
    }

    private fun resolveUsername(uuid: UUID): String {
        val profile = ServerLifecycleHooks.getCurrentServer()!!.profileCache?.get(uuid)?.orElse(null)
        return profile?.name ?: "Unknown Player"
    }
}