package dev.turtlebongo.ThatGuyJustin.ServerTools.util;

import com.mojang.logging.LogUtils;

public class Logger {

    private static final org.slf4j.Logger LOGGER = LogUtils.getLogger();

    private static String prefix = "&7&l[&a&lServer&b&lTools&7&l] &r";

    private static void log(String message, Type type, boolean prefix) {
        LOGGER.info(StringUtils.color((prefix ? Logger.prefix : "") + (type.getPrefix()) + message));
    }

    private static void log(String prefix, String message, Type type) {
        LOGGER.info((prefix == null ? "" : prefix) + (type.getPrefix() + message));
    }


    private enum Type {
        NORMAL(""),
        INFO("&7&l[&b&lINFO&7&l] &r"),
        WARNING("&7&l[&c&lWARNING&7&l] &r"),
        SEVERE("&7&l[&4&lSEVERE&7&l] &r"),
        ERROR("&7&l[&4&lERROR&7&l] &r"),
        DEBUG("&7&l[&4&lDEBUG&7&l] &r");

        private String prefix;

        Type(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }
    }

    public static void debug(String message) {
        log(message, Type.DEBUG, false);
    }

    public static void log(String message, boolean modPrefix) {
        log(message, Type.NORMAL, modPrefix);
    }

    public static void info(String message, boolean modPrefix) {
        log(message, Type.INFO, modPrefix);
    }

    public static void warning(String message, boolean modPrefix) {
        log(message, Type.WARNING, modPrefix);
    }

    public static void severe(String message, boolean modPrefix) {
        log(message, Type.SEVERE, modPrefix);
    }

    public static void error(String message, boolean modPrefix) {
        log(message, Type.ERROR, modPrefix);
    }

}
