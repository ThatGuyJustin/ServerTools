package dev.turtlebongo.ThatGuyJustin.ServerTools;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class Config {

    public static final ForgeConfigSpec GENERAL_SPEC;
    public static ForgeConfigSpec.ConfigValue<Integer> restartHours;
    public static ForgeConfigSpec.ConfigValue<Boolean> discordEnabled;
    public static ForgeConfigSpec.ConfigValue<Boolean> mob_filter_enabled;
    public static ForgeConfigSpec.ConfigValue<String> botToken;
    public static ForgeConfigSpec.ConfigValue<String> webhookURL;
    public static ForgeConfigSpec.ConfigValue<String> chatChannel;
    public static ForgeConfigSpec.ConfigValue<String> loggingChannel;
    public static ForgeConfigSpec.ConfigValue<Boolean> enabledChatBridge;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> admin_roles;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> mob_filter;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> whitelisted_roles;
    public static ForgeConfigSpec.ConfigValue<Boolean> enforce_whitelist;
    public static ForgeConfigSpec.ConfigValue<Boolean> use_ftbteams;
    public static ForgeConfigSpec.ConfigValue<String> chat_format;


    static {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        setupConfig(configBuilder);
        GENERAL_SPEC = configBuilder.build();
    }

    private static void setupConfig(ForgeConfigSpec.Builder builder) {

//        Map<String, String> default_filter = new HashMap<>();
//
//        default_filter.put("minecraft:zombie", "minecraft:the_end");
        restartHours = builder.comment("How many hours after server startup should the server reboot?").define("server_restart_hours", 24);
        discordEnabled = builder.comment("Should discord featurs be enabled?").define("discord.enabled", false);
        botToken = builder.comment("The token for the discord bot").define("discord.bot_token", "");
        webhookURL = builder.comment("The Webhook URL for cross platform messaging").define("discord.webhook_url", "");
        chatChannel = builder.comment("The Channel to post all the messages to").define("discord.chat_channel", "");
        loggingChannel = builder.comment("The Channel to post all the log messages to").define("discord.logging_channel", "");
        enabledChatBridge = builder.comment("Whether to enable the chat bridge or not").define("discord.enable_chat_bridge", false);
        admin_roles = builder
                .comment("These roles are labeled as Admins to the bot")
                .defineList("discord.admin_roles", Arrays.asList("000000000000000"), entry -> true);
        mob_filter_enabled = builder.comment("Should the mob filter be enabled?").define("mob_filter_enabled", false);
        mob_filter = builder.comment("This is the config for the mob filter. Mapped as modid:mob/modid:world,minecraft:world. Example: 'minecraft:zombie/minecraft:the_end'").defineList("mob_filter", Arrays.asList("minecraft:zombie/minecraft:the_end"), entry -> true);
        whitelisted_roles = builder.comment("Members with these roles are allowed to join the server").define("server.whitelist.whitelist_roles", Arrays.asList("000000000000000"), entry -> true);
        enforce_whitelist = builder.comment("Should the member roles whitelist be enforced").define("server.whitelist.enforce_whitelist", false);
    }

}
