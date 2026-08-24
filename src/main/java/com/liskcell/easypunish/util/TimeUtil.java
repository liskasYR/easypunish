package com.liskcell.easypunish.util;

public class TimeUtil {

    public static long parseTime(String input) {
        if (input == null || input.isEmpty()) {
            return 0L;
        }
        input = input.trim().toLowerCase();
        try {
            if (input.endsWith("m")) {
                long val = Long.parseLong(input.substring(0, input.length() - 1));
                return val * 60 * 1000L;
            } else if (input.endsWith("h")) {
                long val = Long.parseLong(input.substring(0, input.length() - 1));
                return val * 60 * 60 * 1000L;
            } else if (input.endsWith("d")) {
                long val = Long.parseLong(input.substring(0, input.length() - 1));
                return val * 24 * 60 * 60 * 1000L;
            } else if (input.endsWith("w")) {
                long val = Long.parseLong(input.substring(0, input.length() - 1));
                return val * 7 * 24 * 60 * 60 * 1000L;
            } else {
                return Long.parseLong(input) * 1000L;
            }
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    public static String formatTime(long ms) {
        if (ms <= 0) {
            return "Expired";
        }
        long seconds = ms / 1000L;
        long minutes = seconds / 60L;
        long hours = minutes / 60L;
        long days = hours / 24L;

        hours = hours % 24L;
        minutes = minutes % 60L;
        seconds = seconds % 60L;

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("d ");
        }
        if (hours > 0) {
            sb.append(hours).append("h ");
        }
        if (minutes > 0) {
            sb.append(minutes).append("m ");
        }
        if (sb.length() == 0 || (days == 0 && hours == 0 && minutes == 0)) {
            sb.append(seconds).append("s");
        }
        return sb.toString().trim();
    }
}
