package dev.fatyoshi.thatguyjustin.servertools.util

import java.awt.Color

class StringUtils {
    companion object {
        /**
         * Color a message using & color codes
         *
         * @param message message
         * @return colored message
         */
        fun color(message: String): String {
            return ChatColor.Companion.translateAlternateColorCodes('&', message)
        }
    }
}

fun Color.toHex(): String = "#${Integer.toHexString(this.rgb and 0xFFFFFF)}"

