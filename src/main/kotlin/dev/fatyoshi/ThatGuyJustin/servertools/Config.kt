package dev.fatyoshi.thatguyjustin.servertools

import net.neoforged.neoforge.common.ModConfigSpec;


object Config {
    var GENERAL_SPEC: ModConfigSpec? = null
    var enableRestartTimer: ModConfigSpec.ConfigValue<Boolean>? = null
    var restartTime: ModConfigSpec.ConfigValue<String>? = null
    var restartTimerNotices: ModConfigSpec.ConfigValue<List<String>>? = null
    var discordEnabled: ModConfigSpec.ConfigValue<Boolean>? = null
    var discordRegisterCommands: ModConfigSpec.ConfigValue<Boolean>? = null
    var mob_filter_enabled: ModConfigSpec.ConfigValue<Boolean>? = null
    var botToken: ModConfigSpec.ConfigValue<String>? = null
    var webhookURL: ModConfigSpec.ConfigValue<String>? = null
    var avatarSkinProxy: ModConfigSpec.ConfigValue<String>? = null
    var chatChannel: ModConfigSpec.ConfigValue<String>? = null
    var loggingChannel: ModConfigSpec.ConfigValue<String>? = null
    var enabledChatBridge: ModConfigSpec.ConfigValue<Boolean>? = null
    var admin_roles: ModConfigSpec.ConfigValue<List<String>>? = null
    var mob_filter: ModConfigSpec.ConfigValue<List<String>>? = null
    var whitelisted_roles: ModConfigSpec.ConfigValue<List<String>>? = null
    var enforce_whitelist: ModConfigSpec.ConfigValue<Boolean>? = null
    var max_whitelist_per_player: ModConfigSpec.ConfigValue<Int>? = null

    var notificationSound: ModConfigSpec.ConfigValue<String>? = null
    var notificationVolume: ModConfigSpec.ConfigValue<Float>? = null
    var notificationPitch: ModConfigSpec.ConfigValue<Float>? = null

    val defaultMessagesConfig = listOf(
        "time=1h,message=<gold>Server reboot in <bold><yellow>1 hour<gold><bold>!",
        "time=30m,message=<gold>Server reboot in <bold><yellow>30 Minutes<gold><bold>!",
        "time=10m,message=<gold>Server reboot in <bold><yellow>10 Minutes<gold><bold>!",
        "time=5m,message=<gold>Server reboot in <bold><yellow>5 Minutes<gold><bold>!",
        "time=1m,message=<gold>Server reboot in <bold><yellow>1 Minute<gold><bold>!",
        "time=10s,message=<gold>Server rebooting... <bold><yellow>10<gold><bold>!",
        "time=9s,message=<gold>Server rebooting... <bold><yellow>9<gold><bold>!",
        "time=8s,message=<gold>Server rebooting... <bold><yellow>8<gold><bold>!",
        "time=7s,message=<gold>Server rebooting... <bold><yellow>7<gold><bold>!",
        "time=6s,message=<gold>Server rebooting... <bold><yellow>6<gold><bold>!",
        "time=5s,message=<gold>Server rebooting... <bold><yellow>5<gold><bold>!",
        "time=4s,message=<gold>Server rebooting... <bold><yellow>4<gold><bold>!",
        "time=3s,message=<gold>Server rebooting... <bold><yellow>3<gold><bold>!",
        "time=2s,message=<gold>Server rebooting... <bold><yellow>2<gold><bold>!",
        "time=1s,message=<gold>Server rebooting... <bold><yellow>1<gold><bold>!",
        "time=0s,message=<gold>Server rebooting... <bold><yellow>NOW<gold><bold>!",
    )

    init {
        val configBuilder = ModConfigSpec.Builder()
        setupConfig(configBuilder)
        GENERAL_SPEC = configBuilder.build()
    }

    private fun setupConfig(builder: ModConfigSpec.Builder) {

//        Map<String, String> default_filter = new HashMap<>();
//
//        default_filter.put("minecraft:zombie", "minecraft:the_end");
        enableRestartTimer = builder.comment("Enable the server restart timer.").define("enable_restart_timer", true)
        restartTime = builder.comment("How much time after server startup should the server reboot? (Example: 24h, or 10h 30m)").define("restart_timer_time", "24h")
        restartTimerNotices = builder.comment("Configuration for time offsets and messages to send at times before server restart.").defineList("restart_timer_notices", defaultMessagesConfig) { entry: Any? -> true }
        discordEnabled = builder.comment("Should discord features be enabled?").define("discord.enabled", false)
        discordRegisterCommands = builder.comment("Should the bot register commands?").define("discord.register_commands", false)
        botToken = builder.comment("The token for the discord bot").define("discord.bot_token", "")
        webhookURL = builder.comment("The Webhook URL for cross platform messaging").define("discord.webhook_url", "")
        avatarSkinProxy = builder.comment("The URL used to get webhook avatars for player messages. || Replaces %uuid% -> UUID with Dashes %uuid-no-dash% --> UUID without").define("discord.avatar_skin_proxy", "https://mc-heads.net/head/%uuid%")
        chatChannel = builder.comment("The Channel to post all the messages to").define("discord.chat_channel", "")
        loggingChannel = builder.comment("The Channel to post all the log messages to").define("discord.logging_channel", "")
        enabledChatBridge = builder.comment("Whether to enable the chat bridge or not").define("discord.enable_chat_bridge", false)
        admin_roles = builder.comment("These roles are labeled as Admins to the bot").defineList("discord.admin_roles", mutableListOf("000000000000000")) { entry: Any? -> true }
        mob_filter_enabled = builder.comment("Should the mob filter be enabled?").define("mob_filter_enabled", false)
        mob_filter = builder.comment("This is the config for the mob filter. Mapped as modid:mob/modid:world,minecraft:world. Example: 'minecraft:zombie/minecraft:the_end'").defineList("mob_filter", mutableListOf("minecraft:zombie/minecraft:the_end")) { entry: Any? -> true }
        whitelisted_roles = builder.comment("Members with these roles are allowed to use the whitelist command.").define("server.whitelist.whitelist_roles", mutableListOf("000000000000000")) { entry: Any? -> true }
        enforce_whitelist = builder.comment("Should the member roles whitelist be enforced").define("server.whitelist.enforce_whitelist", false)
        max_whitelist_per_player = builder.comment("How many accounts can one person whitelist? [Excluding Admins]").define("server.whitelist.max_whitelist_per_player", 1)

        notificationSound = builder.comment("The sound to play for notifications").define("notifications.sound", "minecraft:entity.experience_orb.pickup")
        notificationVolume = builder.comment("The volume for the notification sound").define("notifications.volume", 1.0f)
        notificationPitch = builder.comment("The pitch for the notification sound").define("notifications.pitch", 1.0f)
    }
}

