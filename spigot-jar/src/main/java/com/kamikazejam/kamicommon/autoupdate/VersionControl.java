package com.kamikazejam.kamicommon.autoupdate;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;

@SuppressWarnings({"SameParameterValue"})
public class VersionControl {
    @Setter
    @Getter
    public static class Version {
        private boolean loaded;
        private String name;
        private String versionStr;
        private Instant buildDate;

        public Version() {
            loaded = false;
            name = null;
            versionStr = null;
            buildDate = null;
        }
    }

    private static Version version = null;

    public static Version getVersion(Plugin plugin) {
        if (version == null) {
            setup(plugin.getClass().getClassLoader().getResourceAsStream("version.json"));
        }
        return version;
    }

    public static void setup(InputStream inputStream) {
        version = new Version();

        try (InputStream in = inputStream) {
            Preconditions.checkNotNull(in);
            // This is fine using UTF-8, as the file is generated by the build script with no special chars
            JsonElement e = (new JsonParser()).parse(new InputStreamReader(in, StandardCharsets.UTF_8));
            JsonObject object = (JsonObject) e;

            if (object.has("name")) {
                version.setName(object.get("name").getAsString());
            }

            if (object.has("version")) {
                version.setVersionStr(object.get("version").getAsString());
            }

            if (object.has("date")) {
                version.setBuildDate(Instant.parse(object.get("date").getAsString()));
            }

            version.setLoaded(true);
        } catch (Exception ex) {
            Bukkit.getLogger().severe("Could not check version.json");
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public static void sendDetails(Plugin plugin, CommandSender sender) {
        Version version = getVersion(plugin);
        if (version.isLoaded()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bName: " + version.getName()));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bVersion: " + version.getVersionStr()));

            Instant buildDate = version.getBuildDate();
            if (buildDate != null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bBuild ago: " + formatDateDiff(buildDate.getEpochSecond())));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bBuild date: " + formatDate(buildDate)));
            }

            if (AutoUpdate.hasPluginUpdates()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bA new version of the plugin is queued for next restart!"));
            }
        }
    }

    public static String formatDateDiff(long unixTimestamp) {
        long now = System.currentTimeMillis() / 1000L;
        return format(unixTimestamp - now);
    }

    public static String formatDate(Instant instant) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }



    //Utility methods below for dates

    private static String format(long seconds) {
        Calendar from = new GregorianCalendar();
        from.setTimeInMillis(0);

        Calendar to = new GregorianCalendar();
        to.setTimeInMillis(seconds * 1000L);

        final String[] names = new String[]{"year", "years", "month", "months", "day", "days", "hour", "hours", "minute", "minutes", "second", "seconds"};
        return dateDiff(from, to, 4, names, false);
    }


    private static final int[] CALENDAR_TYPES = new int[]{
            Calendar.YEAR,
            Calendar.MONTH,
            Calendar.DAY_OF_MONTH,
            Calendar.HOUR_OF_DAY,
            Calendar.MINUTE,
            Calendar.SECOND
    };

    private static String dateDiff(Calendar from, Calendar to, int maxAccuracy, String[] names, boolean concise) {
        if (to.equals(from)) {
            return "now";
        }

        boolean future = to.after(from);

        // Temporary 50ms time buffer added to avoid display truncation due to code execution delays
        to.add(Calendar.MILLISECOND, future ? 50 : -50);

        StringBuilder sb = new StringBuilder();
        int accuracy = 0;
        for (int i = 0; i < CALENDAR_TYPES.length; i++) {
            if (accuracy > maxAccuracy) {
                break;
            }

            int diff = dateDiff(CALENDAR_TYPES[i], from, to, future);
            if (diff > 0) {
                int plural = diff > 1 ? 1 : 0;
                String unit = names[i * 2 + plural];

                sb.append(" ");
                sb.append(diff);
                if (!concise) {
                    sb.append(" ");
                }
                sb.append(unit);

                accuracy++;
            }
        }

        // Preserve correctness in the original date object by removing the extra buffer time
        to.add(Calendar.MILLISECOND, future ? -50 : 50);

        if (sb.length() == 0) {
            return "now";
        }

        return sb.toString().trim();
    }

    private static final int MAX_YEARS = 100000;
    private static int dateDiff(int type, Calendar fromDate, Calendar toDate, boolean future) {
        int fromYear = fromDate.get(Calendar.YEAR);
        int toYear = toDate.get(Calendar.YEAR);
        if (Math.abs(fromYear - toYear) > MAX_YEARS) {
            toDate.set(Calendar.YEAR, fromYear + (future ? MAX_YEARS : -MAX_YEARS));
        }

        int diff = 0;
        long savedDate = fromDate.getTimeInMillis();
        while (future && !fromDate.after(toDate) || !future && !fromDate.before(toDate)) {
            savedDate = fromDate.getTimeInMillis();
            fromDate.add(type, future ? 1 : -1);
            diff++;
        }

        diff--;
        fromDate.setTimeInMillis(savedDate);
        return diff;
    }
}
