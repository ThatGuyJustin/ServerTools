package dev.fatyoshi.thatguyjustin.servertools.util

import com.mojang.logging.LogUtils

class Logger {

    companion object {
        private val prefix = "&7&l[&a&lServer&b&lTools&7&l] &r"

        private val LOGGER = LogUtils.getLogger()
        private fun log(message: String, type: Type, prefix: Boolean) {
            LOGGER.info(StringUtils.color((if (prefix) Logger.prefix else "") + type.prefix + message))
        }

        private fun log(prefix: String?, message: String, type: Type) {
            LOGGER.info((prefix ?: "") + (type.prefix + message))
        }

        fun debug(message: String) {
            log(message, Type.DEBUG, false)
        }

        fun log(message: String, modPrefix: Boolean) {
            log(message, Type.NORMAL, modPrefix)
        }

        fun info(message: String, modPrefix: Boolean) {
            log(message, Type.INFO, modPrefix)
        }

        fun warning(message: String, modPrefix: Boolean) {
            log(message, Type.WARNING, modPrefix)
        }

        fun severe(message: String, modPrefix: Boolean) {
            log(message, Type.SEVERE, modPrefix)
        }

        fun error(message: String, modPrefix: Boolean) {
            log(message, Type.ERROR, modPrefix)
        }

        private enum class Type(val prefix: String) {
            NORMAL(""),
            INFO("&7&l[&b&lINFO&7&l] &r"),
            WARNING("&7&l[&c&lWARNING&7&l] &r"),
            SEVERE("&7&l[&4&lSEVERE&7&l] &r"),
            ERROR("&7&l[&4&lERROR&7&l] &r"),
            DEBUG("&7&l[&4&lDEBUG&7&l] &r")

        }
    }
}
