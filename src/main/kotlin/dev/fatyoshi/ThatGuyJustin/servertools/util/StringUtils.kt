package dev.fatyoshi.thatguyjustin.servertools.util

import java.awt.Color
import kotlin.time.Duration

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

fun Duration.toHumanReadable(): String = this.toComponents { days, hours, minutes, seconds, nanoseconds ->
    ((if(days >= 1) "$days Day${if (days.toInt() != 1) "s" else ""} " else "") +
    (if(hours >= 1) "$hours Hour${if(hours != 1) "s" else ""} " else "") +
    (if(minutes >= 1 && days < 1) "$minutes Minute${if(minutes != 1) "s" else ""} " else "") +
    (if(hours < 1 && days < 1) "$seconds Second${if(seconds != 1) "s" else ""}" else "")).trimEnd()
}

