package dev.fatyoshi.thatguyjustin.servertools.util

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
