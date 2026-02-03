package dev.fatyoshi.thatguyjustin.servertools

import net.neoforged.neoforge.common.ModConfigSpec;


object Config {
    var GENERAL_SPEC: ModConfigSpec? = null
    var restartHours: ModConfigSpec.ConfigValue<Int>? = null
    var discordEnabled: ModConfigSpec.ConfigValue<Boolean>? = null
    var mob_filter_enabled: ModConfigSpec.ConfigValue<Boolean>? = null
    var botToken: ModConfigSpec.ConfigValue<String>? = null
    var webhookURL: ModConfigSpec.ConfigValue<String>? = null
    var chatChannel: ModConfigSpec.ConfigValue<String>? = null
    var loggingChannel: ModConfigSpec.ConfigValue<String>? = null
    var enabledChatBridge: ModConfigSpec.ConfigValue<Boolean>? = null
    var admin_roles: ModConfigSpec.ConfigValue<List<String>>? = null
    var mob_filter: ModConfigSpec.ConfigValue<List<String>>? = null
    var whitelisted_roles: ModConfigSpec.ConfigValue<List<String>>? = null
    var enforce_whitelist: ModConfigSpec.ConfigValue<Boolean>? = null
    var max_whitelist_per_player: ModConfigSpec.ConfigValue<Int>? = null
    var use_ftbteams: ModConfigSpec.ConfigValue<Boolean>? = null
    var chat_format: ModConfigSpec.ConfigValue<String>? = null

    init {
        val configBuilder = ModConfigSpec.Builder()
        setupConfig(configBuilder)
        GENERAL_SPEC = configBuilder.build()
    }

    private fun setupConfig(builder: ModConfigSpec.Builder) {

//        Map<String, String> default_filter = new HashMap<>();
//
//        default_filter.put("minecraft:zombie", "minecraft:the_end");
        restartHours = builder.comment("How many hours after server startup should the server reboot?").define("server_restart_hours", 24)
        discordEnabled = builder.comment("Should discord features be enabled?").define("discord.enabled", false)
        botToken = builder.comment("The token for the discord bot").define("discord.bot_token", "")
        webhookURL = builder.comment("The Webhook URL for cross platform messaging").define("discord.webhook_url", "")
        chatChannel = builder.comment("The Channel to post all the messages to").define("discord.chat_channel", "")
        loggingChannel = builder.comment("The Channel to post all the log messages to").define("discord.logging_channel", "")
        enabledChatBridge = builder.comment("Whether to enable the chat bridge or not").define("discord.enable_chat_bridge", false)
        admin_roles = builder.comment("These roles are labeled as Admins to the bot").defineList("discord.admin_roles", mutableListOf("000000000000000")) { entry: Any? -> true }
        mob_filter_enabled = builder.comment("Should the mob filter be enabled?").define("mob_filter_enabled", false)
        mob_filter = builder.comment("This is the config for the mob filter. Mapped as modid:mob/modid:world,minecraft:world. Example: 'minecraft:zombie/minecraft:the_end'").defineList("mob_filter", mutableListOf("minecraft:zombie/minecraft:the_end")) { entry: Any? -> true }
        whitelisted_roles = builder.comment("Members with these roles are allowed to use the whitelist command.").define("server.whitelist.whitelist_roles", mutableListOf("000000000000000")) { entry: Any? -> true }
        enforce_whitelist = builder.comment("Should the member roles whitelist be enforced").define("server.whitelist.enforce_whitelist", false)
        max_whitelist_per_player = builder.comment("How many accounts can one person whitelist? [Excluding Admins]").define("server.whitelist.max_whitelist_per_player", 1)
    }
}

