[![Build Status](https://github.com/ThatGuyJustin/ServerTools/actions/workflows/gradle-publish.yml/badge.svg)](https://github.com/ThatGuyJustin/ServerTools-1.18.2/actions/workflows/gradle-publish.yml)

# ServerTools 1.21.1 NeoForge
A mod I made for small private SMP Servers.

# Setup
1) Download [Kotlin for Forge](https://www.curseforge.com/minecraft/mc-mods/kotlin-for-forge)
2) Download the jar
3) Place both mods into mods folder
4) Configure the mod, reboot server!

# Minecraft Commands
| Command | Permission        | Description                   |
| ----------- |-------------------|-------------------------------|
| `/timer` | `servertools.timer` | Commands to control the timer |
| `/uptime` | `servertools.uptime` | Gives you the server start time and current time remaining until reboot. |

# Discord Commands

Discord commands are bulk registered to the bot on startup. So use a fresh bot account for this, as if you have other commands registered, they will go poof.

**The slash commands are as follows**

| Command | Description |
| ----------- | ----------- |
| /ping | Pong! |
| /tps | Shows current TPS. |
| /server | Same as above command. |
| /list | Gives you the online list. |
| /disconnect | Allows you to force disconnect yourself if your client crashes but are still connected to the server. |
| /whitelist | Whitelist yourself to the server |

# Features
* Configurable Auto Restart timer.
* Discord log to show server startup/shutdown as well as logging the disconnect command
* Self whitelisting command from discord
* Minecraft <-> Discord Chat bridge (With support RGB for Users role color)

## Potential Future Features:
* More Logging?
* /shrug