package dev.turtlebongo.ThatGuyJustin.ServerTools;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class Config {

    public static final ForgeConfigSpec GENERAL_SPEC;
    public static ForgeConfigSpec.ConfigValue<String> botToken;
    public static ForgeConfigSpec.ConfigValue<String> webhookURL;
    public static ForgeConfigSpec.ConfigValue<String> chatChannel;
    public static ForgeConfigSpec.ConfigValue<String> loggingChannel;
    public static ForgeConfigSpec.ConfigValue<Boolean> enabledChatBridge;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> admin_roles;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> mob_filter;

    static {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        setupConfig(configBuilder);
        GENERAL_SPEC = configBuilder.build();
    }

    private static void setupConfig(ForgeConfigSpec.Builder builder) {

//        Map<String, String> default_filter = new HashMap<>();
//
//        default_filter.put("minecraft:zombie", "minecraft:the_end");

        botToken = builder.comment("The token for the discord bot").define("discord.bot_token", "");
        webhookURL = builder.comment("The Webhook URL for cross platform messaging").define("discord.webhook_url", "");
        chatChannel = builder.comment("The Channel to post all the messages to").define("discord.chat_channel", "");
        loggingChannel = builder.comment("The Channel to post all the log messages to").define("discord.logging_channel", "");
        enabledChatBridge = builder.comment("Whether to enable the chat bridge or not").define("discord.enable_chat_bridge", false);
        admin_roles = builder
                .comment("These roles are labeled as Admins to the bot")
                .defineList("discord.admin_roles", Arrays.asList("000000000000000"), entry -> true);
        mob_filter = builder.comment("This is the config for the mob filter. Mapped as modid:mob/modid:world,minecraft:world. Example: 'minecraft:zombie/minecraft:the_end'").defineList("mob_filter", Arrays.asList("minecraft:zombie/minecraft:the_end"), entry -> true);
    }

}
