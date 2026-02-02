package dev.fatyoshi.ThatGuyJustin.servertools.util

import com.google.common.collect.Maps
import java.util.regex.Pattern

/**
 * All supported color values for chat
 */
enum class ChatColor(
    /**
     * Gets the char value associated with this color
     *
     * @return A char value of this color code
     */
    val char: Char, private val intCode: Int,
    /**
     * Checks if this code is a format code as opposed to a color code.
     */
    val isFormat: Boolean = false
) {
    /**
     * Represents black
     */
    BLACK('0', 0x00),

    /**
     * Represents dark blue
     */
    DARK_BLUE('1', 0x1),

    /**
     * Represents dark green
     */
    DARK_GREEN('2', 0x2),

    /**
     * Represents dark blue (aqua)
     */
    DARK_AQUA('3', 0x3),

    /**
     * Represents dark red
     */
    DARK_RED('4', 0x4),

    /**
     * Represents dark purple
     */
    DARK_PURPLE('5', 0x5),

    /**
     * Represents gold
     */
    GOLD('6', 0x6),

    /**
     * Represents gray
     */
    GRAY('7', 0x7),

    /**
     * Represents dark gray
     */
    DARK_GRAY('8', 0x8),

    /**
     * Represents blue
     */
    BLUE('9', 0x9),

    /**
     * Represents green
     */
    GREEN('a', 0xA),

    /**
     * Represents aqua
     */
    AQUA('b', 0xB),

    /**
     * Represents red
     */
    RED('c', 0xC),

    /**
     * Represents light purple
     */
    LIGHT_PURPLE('d', 0xD),

    /**
     * Represents yellow
     */
    YELLOW('e', 0xE),

    /**
     * Represents white
     */
    WHITE('f', 0xF),

    /**
     * Represents magical characters that change around randomly
     */
    MAGIC('k', 0x10, true),

    /**
     * Makes the text bold.
     */
    BOLD('l', 0x11, true),

    /**
     * Makes a line appear through the text.
     */
    STRIKETHROUGH('m', 0x12, true),

    /**
     * Makes the text appear underlined.
     */
    UNDERLINE('n', 0x13, true),

    /**
     * Makes the text italic.
     */
    ITALIC('o', 0x14, true),

    /**
     * Resets all previous chat colors or formats.
     */
    RESET('r', 0x15);

    override fun toString(): String {
        return String(charArrayOf(COLOR_CHAR, char))
    }

    val isColor: Boolean
        /**
         * Checks if this code is a color code as opposed to a format code.
         */
        get() = !isFormat && this != RESET

    init {
//        toString = String(charArrayOf(COLOR_CHAR, char))
    }

    companion object {
        /**
         * The special character which prefixes all chat colour codes. Use this if
         * you need to dynamically convert colour codes from your custom format.
         */
        const val COLOR_CHAR = '\u00A7'
        private val STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR.toString() + "[0-9A-FK-OR]")
        private val BY_ID: MutableMap<Int, ChatColor> = Maps.newHashMap()
        private val BY_CHAR: MutableMap<Char, ChatColor> = Maps.newHashMap()
        fun getByChar(code: Char): ChatColor? {
            return BY_CHAR[code]
        }

        /**
         * Strips the given message of all color codes
         *
         * @param input String to strip of color
         * @return A copy of the input string, without any coloring
         */
        fun stripColor(input: String?): String? {
            return if (input == null) {
                null
            } else STRIP_COLOR_PATTERN.matcher(input).replaceAll("")
        }

        /**
         * Translates a string using an alternate color code character into a
         * string that uses the internal ChatColor.COLOR_CODE color code
         * character. The alternate color code character will only be replaced if
         * it is immediately followed by 0-9, A-F, a-f, K-O, k-o, R or r.
         *
         * @param altColorChar The alternate color code character to replace. Ex: &
         * @param textToTranslate Text containing the alternate color code character.
         * @return Text containing the ChatColor.COLOR_CODE color code character.
         */
        fun translateAlternateColorCodes(altColorChar: Char, textToTranslate: String): String {
            val b = textToTranslate.toCharArray()
            for (i in 0 until b.size - 1) {
                if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                    b[i] = COLOR_CHAR
                    b[i + 1] = b[i + 1].lowercaseChar()
                }
            }
            return String(b)
        }

        /**
         * Gets the ChatColors used at the end of the given input string.
         *
         * @param input Input string to retrieve the colors from.
         * @return Any remaining ChatColors to pass onto the next line.
         */
        fun getLastColors(input: String): String {
            var result = ""
            val length = input.length

            // Search backwards from the end as it is faster
            for (index in length - 1 downTo -1 + 1) {
                val section = input[index]
                if (section == COLOR_CHAR && index < length - 1) {
                    val c = input[index + 1]
                    val color = getByChar(c)
                    if (color != null) {
                        result = color.toString() + result

                        // Once we find a color or reset we can stop searching
                        if (color.isColor || color == RESET) {
                            break
                        }
                    }
                }
            }
            return result
        }

        init {
            for (color in entries) {
                BY_ID[color.intCode] = color
                BY_CHAR[color.char] = color
            }
        }
    }
}