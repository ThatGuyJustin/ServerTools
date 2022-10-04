package dev.turtlebongo.ThatGuyJustin.ServerTools.discord;

import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import dev.turtlebongo.ThatGuyJustin.ServerTools.Config;
import dev.turtlebongo.ThatGuyJustin.ServerTools.ServerTools;
import dev.turtlebongo.ThatGuyJustin.ServerTools.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.AttachedFile;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;
import oshi.SystemInfo;

import java.io.File;
import java.util.*;

public class DiscordHandler extends ListenerAdapter {

    private JDAWebhookClient webhookClient;
    private TextChannel chatChannel;
    private JDA botClient;
    private Date startup;

    public DiscordHandler(Date startup) {
        this.startup = startup;
        String token = Config.botToken.get();

        if (token == null) {
            Logger.error("&7[&dDiscord&7] &fBlank token found. Please insert a token into the config and reboot to continue!", true);
        } else {
            Logger.info("&7[&dDiscord&7] &fAttempting Discord Login...", true);
            this.botClient = JDABuilder.createLight(token).setEnabledIntents(GatewayIntent.getIntents(3276799)).build();
            this.botClient.getPresence().setActivity(Activity.watching("The server burn...🔥"));
            this.botClient.addEventListener(this);
        }
        this.setupWebhook();
    }

    @Override
    public void onReady(ReadyEvent event) {
        Logger.info("&7[&dDiscord&7] &fLogged into Discord as " + event.getJDA().getSelfUser().getName() + "#" + event.getJDA().getSelfUser().getDiscriminator(), true);
        String chatChannel = Config.chatChannel.get();
        if (chatChannel == null) return;
        try {
            this.chatChannel = this.botClient.getTextChannelById(Config.chatChannel.get());
        } catch (NullPointerException e) {
            Logger.error("&7[&dDiscord&7] &fUnable to get chat channel: Null Pointer Exception", true);
            e.printStackTrace();
        }
        if(Config.loggingChannel.get() != null){
            TextChannel logs = this.botClient.getTextChannelById(Config.loggingChannel.get());
            String msg = String.format("[<t:%s:T>] Server has started",
                    new Date().getTime() / 1000);
            try {
                logs.sendMessage(msg).queue();
            }catch (InsufficientPermissionException e){
                Logger.error("Unable to post in log channel:", true);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (!event.isFromGuild()) return;
        if (event.getAuthor().isBot()) return;
        boolean isAdmin = false;

        for (Role role : event.getMember().getRoles()) {
            if (Config.admin_roles.get().contains(role.getId())) {
                isAdmin = true;
            }
        }

        if (isAdmin) {
            if (event.getMessage().getContentDisplay().equals("!mc-register")) {
                event.getChannel().sendTyping().queue();
                event.getGuild().upsertCommand(Commands.slash("tps", "Gets the current server TPS.")).queue();
                event.getGuild().upsertCommand(Commands.slash("server", "Get current server information.")).queue();
                event.getGuild().upsertCommand(Commands.slash("time", "Shows time left until server reboot.")).queue();
                event.getGuild().upsertCommand(Commands.slash("list", "Get a list of all the online players.")).queue();
                event.getGuild().upsertCommand(Commands.slash("ping", "pong!")).queue();
                event.getGuild().upsertCommand(Commands.slash("disconnect", "Force disconnect yourself if your client crashes but are still connected to the server.").addOption(OptionType.STRING, "username", "Your username", true)).queue();
                event.getGuild().upsertCommand(Commands.slash("lilly", "Force disconnect yourself if your client crashes but are still connected to the server.").addOption(OptionType.STRING, "username", "Your username", true)).queue();
                event.getChannel().sendTyping().queue();
                event.getChannel().sendMessage("Commands added!").queue();
                return;
            }
        }

        if (!event.getChannel().getId().equals(this.chatChannel.getId())) return;

        String color = "#5865F2";
        TextColor hex = TextColor.parseColor(color);
        Style ds = Style.EMPTY.withColor(hex);
        Style s = Style.EMPTY.withColor(event.getMember().getColorRaw());
        MutableComponent user = new TextComponent(String.format("%s", event.getAuthor().getName())).withStyle(s);
        MutableComponent msg = new TextComponent(StringUtils.color("&8[")).append(new TextComponent("D").withStyle(ds)).append(StringUtils.color("&8] ")).append(user).append(StringUtils.color("&8 » &7" + event.getMessage().getContentDisplay()));

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            p.displayClientMessage(msg, false);
        }
    }

    private void setupWebhook() {
        String webhookURL = Config.webhookURL.get();
        if (webhookURL == null) {
            Logger.error("Unable to create webhook client, null webhook url.", true);
        }
        this.webhookClient = JDAWebhookClient.withUrl(webhookURL);
    }

    public void sendWebHookMessage(ServerPlayer p, String msg) {

        if (this.webhookClient == null) return;

        AllowedMentions mentions = new AllowedMentions();
        mentions.withParseUsers(true);
        mentions.withParseEveryone(false);
        mentions.withParseRoles(false);

        WebhookMessageBuilder b = new WebhookMessageBuilder();
        b.setUsername(p.getName().getString());
        b.setAllowedMentions(mentions);
        b.setAvatarUrl(String.format("https://crafatar.com/renders/head/%s?overlay", p.getUUID()));
        b.setContent(msg);

        this.webhookClient.send(b.build());
    }

    public void shutdown() {
        this.webhookClient = null;
        this.botClient.shutdown();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("tps")) {
            event.reply("Currently the TPS is **" + ServerTools.getTPS() + "**").setEphemeral(true).queue();
        }
        if (event.getName().equals("server")) {

            long maxMemory = Runtime.getRuntime().maxMemory();
            long freeMemory = Runtime.getRuntime().freeMemory();

            SystemInfo si = new SystemInfo();
            double[] averageUsage = si.getHardware().getProcessor().getSystemLoadAverage(3);
            double cpuUsage = si.getHardware().getProcessor().getSystemCpuLoadBetweenTicks(si.getHardware().getProcessor().getSystemCpuLoadTicks());

            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

            int used = (int) ((maxMemory-freeMemory) / 1024 / 1024);
            int total = (int) (maxMemory / 1024 / 1024);
            double percent = ((double) used / (double) total * 100.00);

            List<String> players = Arrays.asList(server.getPlayerList().getPlayerNamesArray());
            Collections.sort(players);

            String cpu = "`" + String.format("%.2f%%", cpuUsage) + "` Current\n`" + String.format("%.2f%%", averageUsage[0]) + "` 1m\n`" + String.format("%.2f%%", averageUsage[1]) + "` 5m\n`" + String.format("%.2f%%", averageUsage[2]) + "` 15m";

            EmbedBuilder e = new EmbedBuilder().setTitle("Server Status").setDescription("**MOTD**: `" + server.getMotd() + '`')
                    .addField("TPS", Double.toString(ServerTools.getTPS()), true)
                    .addField("Ram Usage", String.format("%s MB/%s MB (`%.1f%%`)", used, total, percent), true)
                    .addField("CPU Usage", cpu, true)
                    .addField("Timer", String.format("**Started at**: <t:%1$s:T> (<t:%1$s:R>)\n**Restart At**: <t:%2$s:T> (<t:%2$s:R>)", this.startup.getTime() / 1000, (this.startup.getTime() / 1000) + 21600), false)
                    .addField(String.format("Online Players (%s/%s)", server.getPlayerCount(), server.getMaxPlayers()), String.format("``` %s ```", String.join(" ", players)), false);

            String path = server.getServerDirectory().getAbsolutePath().substring(0, server.getServerDirectory().getAbsolutePath().length() - 1);
            if(new File(path + "/server-icon.png").exists()){
                e.setThumbnail("attachment://server-icon.png");
                event.replyEmbeds(e.build()).addFiles(AttachedFile.fromData(new File(path + "/server-icon.png"), "server-icon.png")).setEphemeral(true).queue();
            }else{
                event.replyEmbeds(e.build()).setEphemeral(true).queue();
            }


        }
        if (event.getName().equals("time")) {
            String msg = String.format("**Server Started at**: <t:%1$s:T> (<t:%1$s:R>)\n**Server Will Restart At**: <t:%2$s:T> (<t:%2$s:R>)", this.startup.getTime() / 1000, (this.startup.getTime() / 1000) + 21600);
            event.reply(msg).setEphemeral(true).queue();
            return;
        }
        if (event.getName().equals("ping")) {
            long time = System.currentTimeMillis();
            event.reply("Pong!").setEphemeral(true).queue(response ->
                    response.editOriginal(String.format("Pong: %d ms", System.currentTimeMillis() - time)).queue()
            );
            return;
        }
        if (event.getName().equals("list")) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            String[] players = server.getPlayerList().getPlayerNamesArray();

            if (players.length == 0)
                event.reply("There is nobody online :(").setEphemeral(true).queue();
            else
                event.reply("`Online Players`: " + String.join(", ", players)).setEphemeral(true).queue();

        }
        if (event.getName().equals("disconnect") || event.getName().equals("lilly")) {
            String playerName = event.getOption("username").getAsString();
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            server.getPlayerList().getPlayers().forEach((player) -> {
                if (player.getName().getString().equals(playerName))
                    player.connection.getConnection().channel().disconnect();
            });
            event.reply("The player specified *should* be disconnected...").setEphemeral(true).queue();

            if(Config.loggingChannel.get() != null){
                TextChannel logs = this.botClient.getTextChannelById(Config.loggingChannel.get());
                String msg = String.format("[<t:%s:T>] %s `%s` has used the command `/lilly %s` in channel %s",
                        new Date().getTime() / 1000, event.getUser().getAsMention(), event.getUser().getId(), playerName, event.getChannel().getAsMention());
                try {
                    logs.sendMessage(msg).setAllowedMentions(new HashSet<>()).queue();
                }catch (InsufficientPermissionException e){
                    Logger.error("Unable to post in log channel:", true);
                    e.printStackTrace();
                }
            }

        }
    }

    public JDA getBotClient() {
        return botClient;
    }
}
