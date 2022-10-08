[![Build Status](https://github.com/ThatGuyJustin/ServerTools-1.18.2/actions/workflows/gradle-publish.yml/badge.svg)](https://github.com/ThatGuyJustin/ServerTools-1.18.2/actions/workflows/gradle-publish.yml)

# ServerTools-1.18.2
A mod I made for my small private SMP Server.

# Setup
1) Download [Kotlin for Forge](https://www.curseforge.com/minecraft/mc-mods/kotlin-for-forge)
2) Download the jar
3) Place both mods into mods folder
4) Configure the mod, reboot server!

# Commands
`!mc-register` - Registers all the discord /commands

**The slash commands are as follows**

| Command | Description |
| ----------- | ----------- |
| /ping | Pong! |
| /tps | Shows current TPS. |
| /server | Same as above command. |
| /list | Gives you the online list. |
| /time | Shows time until restart. |
| /lilly | 👇⏬⬇ |
| /disconnect | Allows you to force disconnect yourself if your client crashes but are still connected to the server. |

# Features
* The server auto-restarts every 6 hours as that's typically recommended for MC Forge.
* Discord log to show server startup/shutdown as well as logging the disconnect command
* Timer for the auto restart
* Minecraft <-> Discord Chat bridge (With support RGB for Users role color)
* Makes player invulnerable upon login until the player either moves or interacts with the world to avoid death due to slow/laggy logins

## Potential Future Features:
* Minecraft -> Discord connection
* Server side commands
* More Logging?
* /shrug