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
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> admin_roles;

    static {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        setupConfig(configBuilder);
        GENERAL_SPEC = configBuilder.build();
    }

    private static void setupConfig(ForgeConfigSpec.Builder builder) {
        botToken = builder.comment("The token for the discord bot").define("discord.bot_token", "");
        webhookURL = builder.comment("The Webhook URL for cross platform messaging").define("discord.webhook_url", "");
        chatChannel = builder.comment("The Channel to post all the messages to").define("discord.chat_channel", "");
        loggingChannel = builder.comment("The Channel to post all the log messages to").define("discord.logging_channel", "");
        admin_roles = builder
                .comment("These roles are labeled as Admins to the bot")
                .defineList("discord.admin_roles", Arrays.asList("000000000000000"), entry -> true);
    }

}
