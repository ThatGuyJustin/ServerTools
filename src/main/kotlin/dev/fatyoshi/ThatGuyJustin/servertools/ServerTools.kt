package dev.fatyoshi.thatguyjustin.servertools

import de.maxhenkel.admiral.MinecraftAdmiral
import dev.fatyoshi.thatguyjustin.servertools.discord.DiscordHandler
import dev.fatyoshi.thatguyjustin.servertools.util.Logger
import dev.fatyoshi.thatguyjustin.servertools.util.sendAll
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.ServerChatEvent
import net.neoforged.neoforge.event.server.ServerStartedEvent
import net.neoforged.neoforge.event.server.ServerStartingEvent
import net.neoforged.neoforge.event.server.ServerStoppedEvent
import net.neoforged.neoforge.event.server.ServerStoppingEvent
import net.neoforged.neoforge.server.ServerLifecycleHooks
import java.util.*
import kotlin.time.Duration
import kotlin.time.toJavaDuration

@Mod("servertools")
class ServerTools {
    private var adventure: MinecraftServerAudiences? = null
    private val noticesRegex = Regex("""^time=(?<duration>[^,]+),message=(?<message>.*)$""")
    private var discordHandler: DiscordHandler? = null
    private var timer: Thread? = null
    private var startup: Date? = null
    private val mobFilter = HashMap<String, List<String>>()
    private var timerStop = false

    companion object {
        lateinit var instance: ServerTools
    }

    init {
        NeoForge.EVENT_BUS.register(this);
        ModLoadingContext.get().activeContainer.registerConfig(ModConfig.Type.COMMON, Config.GENERAL_SPEC, "servertools.toml")

        instance = this
    }

    @SubscribeEvent
    fun onServerStarting(event: ServerStartingEvent) {
        this.adventure = MinecraftServerAudiences.of(event.getServer())
    }

    @SubscribeEvent
    fun onCommandReg(event: RegisterCommandsEvent) {
        MinecraftAdmiral.builder(event.dispatcher, event.buildContext).addCommandClasses(
            Commands().javaClass
        ).build()
    }

    @SubscribeEvent
    fun onServerShutdown(event: ServerStoppedEvent) {
        this.adventure = null
    }


    @SubscribeEvent
    fun onChatMessage(event: ServerChatEvent) {
        if (!Config.discordEnabled!!.get()) return
        if (!Config.enabledChatBridge!!.get()) return
        discordHandler!!.sendWebHookMessage(event.player, event.message.string)
    }

    @SubscribeEvent
    fun onServerStarted(event: ServerStartedEvent) {
        this.startup = Date()

        if (Config.discordEnabled!!.get()) {
            Logger.info("Starting Discord Handler...", true)
            this.discordHandler = DiscordHandler(this.startup!!)
        }

        this.parseValues()

        if (Config.enableRestartTimer!!.get() == false) return

        timer = Thread {
            try {
                val restartIn = Duration.parse(Config.restartTime!!.get())

                // Time to make the config more system readable...and sort it...
                val rawMessageTimes = HashMap<Duration, String>()
                Config.restartTimerNotices!!.get().forEach { rawCfgValue ->
                    val match = noticesRegex.find(rawCfgValue)
                    if (match != null) {
                        val rawDuration = match.groups["duration"]?.value
                        val message = match.groups["message"]?.value
                        val duration = Duration.parse(rawDuration!!)
                        if (message != null) rawMessageTimes[duration] = message
                    }
                }

                // In the end we want it as longest duration to the shortest duration.
                val messageTimes = rawMessageTimes.toSortedMap().reversed().toMap()


                Logger.info("&fStartup Time&7: &a${this.startup}", true)
                Logger.info("&fConfigured Restart Time&7: &a${this.startup!!.toInstant().plus(restartIn.toJavaDuration())}", true)
                Logger.info("Timer started!", true)

                var currentSleep = restartIn

                messageTimes.forEach { (duration, message) ->
                    sleepUntilBroadcast(currentSleep.minus(duration).inWholeMilliseconds, message )
                    currentSleep = duration
                }

                instance.timerStop = true
                instance.shutdownServer()
            } catch (e: InterruptedException) {
                if (!this.timerStop) {
                    Logger.info("Unexpected thread yeeted, shutting down timer thread.", true)
                    e.printStackTrace()
                }
            }
        }
        timer!!.name = "Server Shutdown Timer"
        timer!!.start()
    }

    @SubscribeEvent
    fun onServerShutdown(event: ServerStoppingEvent?) {
        Logger.info("Shutting down all handlers...", true)
        if(!this.timerStop)
            try{
                this.timer!!.interrupt()
            }catch (_: InterruptedException){
                Logger.info("Timer stopped.", true)
            }
        if (Config.discordEnabled!!.get()) {
            if (Config.loggingChannel!!.get() != "") {
                val logs: TextChannel =
                    discordHandler?.botClient?.getTextChannelById(Config.loggingChannel!!.get()) ?: return
                val msg = String.format(
                    "[<t:%s:T>] Server is shutting down.",
                    Date().time / 1000
                )
                try {
                    logs.sendMessage(msg).queue()
                } catch (e: InsufficientPermissionException) {
                    Logger.error("Unable to post in log channel:", true)
                    e.printStackTrace()
                }
            }
            discordHandler!!.shutdown()
        }
    }

    //    @SubscribeEvent
    //    public void onEntitySpawn(LivingSpawnEvent event){
    //        if(Config.mob_filter_enabled.get()) {
    //            if (this.mob_filter.containsKey(event.getEntity().getType().getRegistryName().toString())) {
    //                String world_name = event.getEntity().level.dimension().location().toString();
    //                if (this.mob_filter.get(event.getEntity().getType().getRegistryName().toString()).contains(world_name)) {
    ////                event.setCanceled(true);
    //                    event.getEntity().remove(Entity.RemovalReason.DISCARDED);
    //                }
    //            }
    //        }
    //    }

    fun getTPS(): Double {
        val meanTickTime = ServerLifecycleHooks.getCurrentServer()!!.averageTickTimeNanos * 1.0E-6
        return Math.min(1000.0 / meanTickTime, 20.0)
    }

    fun shutdownServer() {
        val server = ServerLifecycleHooks.getCurrentServer()!!
        server.commands.performPrefixedCommand(server.createCommandSourceStack(), "stop")
    }

    @Throws(InterruptedException::class)
    private fun sleepUntilBroadcast(time: Long, msg: String) {
        Thread.sleep(time)
        sendAll(msg)
    }

    private fun parseValues() {
        val raw_values = Config.mob_filter!!.get()
        for (s in raw_values) {
            val mob_name = s.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            val worlds = s.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].split(",".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
            mobFilter[mob_name] = Arrays.asList(*worlds)
        }
    }

    fun adventure(): MinecraftServerAudiences {
        checkNotNull(this.adventure) { "Tried to access Adventure without a running server!" }
        return this.adventure!!
    }
}