package dev.fatyoshi.thatguyjustin.servertools

import de.maxhenkel.admiral.MinecraftAdmiral
import dev.fatyoshi.thatguyjustin.servertools.discord.DiscordHandler
import dev.fatyoshi.thatguyjustin.servertools.util.Logger
import dev.fatyoshi.thatguyjustin.servertools.util.sendMM
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences
import net.minecraft.network.chat.Component
import net.minecraft.world.phys.Vec3
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.ServerChatEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.server.ServerStartedEvent
import net.neoforged.neoforge.event.server.ServerStartingEvent
import net.neoforged.neoforge.event.server.ServerStoppedEvent
import net.neoforged.neoforge.event.server.ServerStoppingEvent
import net.neoforged.neoforge.server.ServerLifecycleHooks
import java.util.*

@Mod("servertools")
class ServerTools {
    private var adventure: MinecraftServerAudiences? = null

    private var discordHandler: DiscordHandler? = null
    private var timer: Thread? = null
    private val startup = Date()
    private val playerLoginCache: List<UUID> = ArrayList()
    private val locationCache = HashMap<UUID, Vec3>()
    private val mob_filter = HashMap<String, List<String>>()
    private var timerStop = false

    companion object {
        lateinit var instance: ServerTools
    }

    init {
        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);
        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        ModLoadingContext.get().activeContainer.registerConfig(ModConfig.Type.COMMON, Config.GENERAL_SPEC, "servertools.toml")

        instance = this
    }

    /**
     * Fired on the global Forge bus.
     */
//    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
//        Logger
//    }

    @SubscribeEvent
    fun onServerStarting(event: ServerStartingEvent) {
        this.adventure = MinecraftServerAudiences.of(event.getServer())

        if (Config.discordEnabled!!.get()) {
            Logger.info("Starting Discord Handler...", true)
            this.discordHandler = DiscordHandler(this.startup)
        }
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


    //    @SubscribeEvent
    //    public void onLogin(PlayerEvent.PlayerLoggedInEvent event){
    //        try {
    ////            event.getPlayer().setInvulnerable(true);
    //            this.playerLoginCache.add(event.getPlayer().getUUID());
    //            this.locationCache.put(event.getPlayer().getUUID(), event.getPlayer().getPosition(0));
    //        }catch (Exception e){
    //        }
    //    }
    //    @SubscribeEvent
    //    public void onLogoff(PlayerEvent.PlayerLoggedOutEvent event){
    //        if(this.playerLoginCache.contains(event.getPlayer().getUUID())){
    //            event.getPlayer().setInvulnerable(false);
    //            this.playerLoginCache.remove(event.getPlayer().getUUID());
    //        }
    //    }
    //
    //    @SubscribeEvent
    //    public void onPlayerInteract(PlayerInteractEvent event){
    //        if(this.playerLoginCache.contains(event.getPlayer().getUUID())){
    //            event.getPlayer().getPosition(0);
    //            event.getPlayer().setInvulnerable(false);
    //            this.playerLoginCache.remove(event.getPlayer().getUUID());
    //            this.locationCache.remove(event.getPlayer().getUUID());
    //        }
    //    }
    //
    //    @SubscribeEvent
    //    public void onEntityUpdate(LivingEvent.LivingUpdateEvent event){
    //        if(this.locationCache.containsKey(event.getEntity().getUUID())){
    //            if(!this.locationCache.containsKey(event.getEntity().getUUID())) return;
    //            if(!this.locationCache.get(event.getEntity().getUUID()).equals(event.getEntity().getPosition(0))){
    //                this.locationCache.remove(event.getEntity().getUUID());
    //                event.getEntity().setInvulnerable(false);
    //                this.playerLoginCache.remove(event.getEntity().getUUID());
    //            }
    //        }
    //    }
    @SubscribeEvent
    fun onChatMessage(event: ServerChatEvent) {
        if (!Config.discordEnabled!!.get()) return
        if (!Config.enabledChatBridge!!.get()) return
        discordHandler!!.sendWebHookMessage(event.player, event.message.string)
    }

    @SubscribeEvent
    fun onServerStarted(event: ServerStartedEvent) {
        val server_dir = ServerLifecycleHooks.getCurrentServer()!!.serverDirectory
        timer = Thread {
            try {
                val server = ServerLifecycleHooks.getCurrentServer()!!
                Logger.info("Timer started!", true)
//                sleepUntilBroadcast(10000, buildAnnouncement("in 1 Hour     ", "#F0B3FF"));
//                sleepUntilBroadcast(10000, buildAnnouncement("in 30 Minutes ", "#E380FF"));
//                sleepUntilBroadcast(10000, buildAnnouncement("in 10 Minutes ", "#D04DFF"));
//                sleepUntilBroadcast(10000, buildAnnouncement("in 5 Minutes  ", "#BE1FFF"));
//                sleepUntilBroadcast(10000, buildAnnouncement("in 1 Minute   ", "#AD00F7"));
//                sleepUntilBroadcast(10000, buildAnnouncement("in 10 Seconds ", "#9B00E0"));
                val rootHours = Config.restartHours!!.get()
//                sleepUntilBroadcast(
//                    (rootHours - 1).toLong() * 60 * 60 * 1000,
//                    buildAnnouncement("in 1 Hour", "#F0B3FF")
//                )
//                sleepUntilBroadcast((30 * 60 * 1000).toLong(), buildAnnouncement("in 30 Minutes ", "#E380FF"))
//                sleepUntilBroadcast((20 * 60 * 1000).toLong(), buildAnnouncement("in 10 Minutes ", "#D04DFF"))
//                sleepUntilBroadcast((5 * 60 * 1000).toLong(), buildAnnouncement("in 5 Minutes ", "#BE1FFF"))
//                sleepUntilBroadcast((4 * 60 * 1000).toLong(), buildAnnouncement("in 1 Minute ", "#AD00F7"))
//                sleepUntilBroadcast(50000, buildAnnouncement("in 10 Seconds ", "#9B00E0"))
//                val colors = arrayOf(
//                    "#8A00C9",
//                    "#7800B3",
//                    "#67009C",
//                    "#550086",
//                    "#44006F",
//                    "#330058",
//                    "#29004A",
//                    "#1F003B",
//                    "#15002D",
//                    "#0C001F"
//                )
//                colors.reverse()
//                var countdown = 9
//                while (countdown > 0) {
//                    sleepUntilBroadcast(1000, buildAnnouncement("in $countdown Seconds  ", colors[countdown]))
//                    countdown--
//                }
//                server.sendSystemMessage(buildAnnouncement("RIGHT NOW ", colors[0]))
                instance.timerStop = true
                instance.shutdownServer()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        timer!!.name = "Server Shutdown Timer"
/*        timer!!.start()*/
        this.parseValues()
    }

    @SubscribeEvent
    fun onServerShutdown(event: ServerStoppingEvent?) {
        Logger.info("Shutting down all handlers...", true)
        if(!this.timerStop)
            try{
                this.timer!!.interrupt()
            }catch (exception: InterruptedException){
                Logger.info("Timer stopped.", true)
            }
        if (Config.discordEnabled!!.get()) {
            if (Config.loggingChannel!!.get() != null) {
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
    //    @SubscribeEvent
    //    public static void RegisterCommands(RegisterCommandsEvent event) {
    //        RegisterSlashCommands.register(event.getDispatcher());
    //
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
    private fun sleepUntilBroadcast(time: Long, msg: Component) {
        val server = ServerLifecycleHooks.getCurrentServer()!!
        Thread.sleep(time)
        server.sendSystemMessage(msg)
        for (p in server.playerList.players) {
            p.displayClientMessage(msg, false)
        }
    }

//    private fun buildAnnouncement(timeLeft: String, hex: String): Component {
//        val s =
//            Style.EMPTY.withColor(TextColor.parseColor(hex)).withBold(true)
//        val staticMiddle: Component =
//            Component.literal(StringUtils.color("&7Server Reboot will happen "))
//        val announce = Component.literal(StringUtils.color("&8("))
//            .append(Component.literal("!").withStyle(s))
//            .append(Component.literal(StringUtils.color("&8) ")))
//        val time = Component.literal(timeLeft).withStyle(s)
//
//        return Component.literal(StringUtils.color("&8(")).append(Component.literal("!").withStyle(s))
//            .append(Component.literal(StringUtils.color("&8) "))).append(staticMiddle).append(time).append(announce)
//    }

    private fun parseValues() {
        val raw_values = Config.mob_filter!!.get()
        for (s in raw_values) {
            val mob_name = s.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            val worlds = s.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].split(",".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
            mob_filter[mob_name] = Arrays.asList(*worlds)
        }
    }

    fun adventure(): MinecraftServerAudiences {
        checkNotNull(this.adventure) { "Tried to access Adventure without a running server!" }
        return this.adventure!!
    }
}