package dev.turtlebongo.ThatGuyJustin.ServerTools;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import dev.turtlebongo.ThatGuyJustin.ServerTools.discord.DiscordHandler;
import dev.turtlebongo.ThatGuyJustin.ServerTools.util.Logger;
import dev.turtlebongo.ThatGuyJustin.ServerTools.util.StringUtils;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.minecraft.network.chat.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("servertools")
public class ServerTools
{

    private DiscordHandler discordHandler;
    private Thread timer;
    private Date startup = new Date();
    private List<UUID> playerLoginCache = new ArrayList<>();
    private HashMap<UUID, Vec3> locationCache = new HashMap<>();
    private HashMap<String, List<String>> mob_filter = new HashMap<>();

    public ServerTools()
    {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.GENERAL_SPEC, "servertools.toml");
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        Logger.info("Starting Discord Handler...", true);
        this.discordHandler = new DiscordHandler(this.startup);

    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event){
        this.timer = new Thread(() -> {
            try {

                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                Logger.info("Timer started!", true);
//                sleepUntilBroadcast(10000, buildAnnouncement("in 1 Hour     ", "#c55d5d"));
//                sleepUntilBroadcast(10000, buildAnnouncement("in 30 Minutes ", "#c65958"));
//                sleepUntilBroadcast(10000, buildAnnouncement("in 10 Minutes ", "#c85553"));
//                sleepUntilBroadcast(10000, buildAnnouncement("in 5 Minutes  ", "#c9504e"));
//                sleepUntilBroadcast(10000, buildAnnouncement("in 1 Minute   ", "#c94c49"));
//                sleepUntilBroadcast(10000, buildAnnouncement("in 10 Seconds ", "#ca4744"));
                sleepUntilBroadcast(300 * 60 * 1000, buildAnnouncement("in 1 Hour", "#fcff00"));
                sleepUntilBroadcast(30 * 60 * 1000, buildAnnouncement("in 30 Minutes", "#fbed00"));
                sleepUntilBroadcast(20 * 60 * 1000, buildAnnouncement("in 10 Minutes", "#f9dc00"));
                sleepUntilBroadcast(5 * 60 * 1000, buildAnnouncement("in 5 Minutes", "#f5ca00"));
                sleepUntilBroadcast(4 * 60 * 1000, buildAnnouncement("in 1 Minute", "#f0b900"));
                sleepUntilBroadcast(50000, buildAnnouncement("in 10 Seconds", "#eba800"));
                String[] colors = { "#ca0000", "#cb0f0a", "#cb1913", "#cc211b", "#cc2821", "#cc2e28", "#cc332e", "#cc3934", "#cb3e39", "#cb433f"};
                int countdown = 9;
                while(countdown > 0){
                    sleepUntilBroadcast(1000, buildAnnouncement("in " + countdown + " Seconds  ", colors[countdown]));
                    countdown--;
                }
                server.getPlayerList().broadcastMessage(buildAnnouncement("RIGHT NOW ", colors[0]), ChatType.SYSTEM, UUID.randomUUID());
                ServerTools.shutdownServer();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        timer.setName("Server Shutdown Timer");
        timer.start();
        this.parseValues();
    }

    @SubscribeEvent
    public void onServerShutdown(ServerStoppingEvent event){
        Logger.info("Shutting down all handlers...", true);
        this.timer.stop();
        if(Config.loggingChannel.get() != null){
            TextChannel logs = this.discordHandler.getBotClient().getTextChannelById(Config.loggingChannel.get());
            String msg = String.format("[<t:%s:T>] Server is shutting down.",
                    new Date().getTime() / 1000);
            try {
                logs.sendMessage(msg).queue();
            }catch (InsufficientPermissionException e){
                Logger.error("Unable to post in log channel:", true);
                e.printStackTrace();
            }
        }
        this.discordHandler.shutdown();
    }

//    @SubscribeEvent
//    public void onLogin(PlayerEvent.PlayerLoggedInEvent event){
//        try {
////            event.getPlayer().setInvulnerable(false);
//            this.playerLoginCache.add(event.getPlayer().getUUID());
//            this.locationCache.put(event.getPlayer().getUUID(), event.getPlayer().getPosition(0));
//        }catch (Exception e){
//        }
//    }

    @SubscribeEvent
    public void onLogoff(PlayerEvent.PlayerLoggedOutEvent event){
        if(this.playerLoginCache.contains(event.getPlayer().getUUID())){
            event.getPlayer().setInvulnerable(false);
            this.playerLoginCache.remove(event.getPlayer().getUUID());
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event){
        if(this.playerLoginCache.contains(event.getPlayer().getUUID())){
            event.getPlayer().getPosition(0);
            event.getPlayer().setInvulnerable(false);
            this.playerLoginCache.remove(event.getPlayer().getUUID());
            this.locationCache.remove(event.getPlayer().getUUID());
        }
    }

    @SubscribeEvent
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent event){
        if(this.locationCache.containsKey(event.getEntity().getUUID())){
            if(!this.locationCache.containsKey(event.getEntity().getUUID())) return;
            if(!this.locationCache.get(event.getEntity().getUUID()).equals(event.getEntity().getPosition(0))){
                this.locationCache.remove(event.getEntity().getUUID());
                event.getEntity().setInvulnerable(false);
                this.playerLoginCache.remove(event.getEntity().getUUID());
            }
        }
    }

    @SubscribeEvent
    public void onChatMessage(ServerChatEvent event){
        if(!Config.enabledChatBridge.get()) return;
        this.discordHandler.sendWebHookMessage(event.getPlayer(), event.getMessage());
    }

    @SubscribeEvent
    public void onEntitySpawn(LivingSpawnEvent event){
        if(this.mob_filter.containsKey(event.getEntity().getType().getRegistryName().toString())){
            String world_name = event.getEntity().level.dimension().location().toString();
            if(this.mob_filter.get(event.getEntity().getType().getRegistryName().toString()).contains(world_name)){
//                event.setCanceled(true);
                event.getEntity().remove(Entity.RemovalReason.DISCARDED);
            }
        }
    }

//    @SubscribeEvent
//    public static void RegisterCommands(RegisterCommandsEvent event) {
//        RegisterSlashCommands.register(event.getDispatcher());
//
//    }

    public static double getTPS() {
        double meanTickTime = ServerLifecycleHooks.getCurrentServer().getAverageTickTime() * 1.0E-6D;
        return Math.min(1000.0 / meanTickTime, 20);
    }

    public static void shutdownServer(){
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        server.getCommands().performCommand(server.createCommandSourceStack(), "stop");
    }

    private void sleepUntilBroadcast(long time, Component msg) throws InterruptedException {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        Thread.sleep(time);
        server.getPlayerList().broadcastMessage(msg, ChatType.SYSTEM, UUID.randomUUID());
    }

    private MutableComponent buildAnnouncement(String timeLeft, String hex){
        Style s = Style.EMPTY.withColor(TextColor.parseColor(hex)).withBold(true);

        TextComponent staticMiddle = new TextComponent(StringUtils.color("&7Server Reboot will happen "));
        MutableComponent announce = new TextComponent(StringUtils.color("&8(")).append(new TextComponent("!").withStyle(s)).append(new TextComponent(StringUtils.color("&8) ")));
        MutableComponent time = new TextComponent(timeLeft).withStyle(s);
        MutableComponent finalMsg = new TextComponent(StringUtils.color("&8(")).append(new TextComponent("!").withStyle(s)).append(new TextComponent(StringUtils.color("&8) "))).append(staticMiddle).append(time).append(announce);

        return finalMsg;
    }

    private void parseValues(){
        List<? extends String> raw_values = Config.mob_filter.get();
        for(String s: raw_values){
            String mob_name = s.split("/")[0];
            String[] worlds = s.split("/")[1].split(",");
            this.mob_filter.put(mob_name, Arrays.asList(worlds));
        }
    }

}
