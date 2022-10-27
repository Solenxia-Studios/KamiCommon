package com.kamikazejamplugins.kamicommon.yaml;


import lombok.Getter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

@SuppressWarnings("unused")
public class YamlHandler {
    private final File configFile;
    private final String fileName;
    private final YamlConfiguration config;
    public YamlHandler(File configFile, String fileName) {
        this.configFile = configFile;
        this.fileName = fileName;
        this.config = null;
    }

    public YamlConfiguration loadConfig(boolean addDefaults) {
        try {
            if (!configFile.exists()) {
                if (!configFile.getParentFile().exists()) {
                    if (!configFile.getParentFile().mkdirs()) {
                        System.out.println("Could not create config file dirs, stopping");
                    }
                }
                if (!configFile.createNewFile()) {
                    System.out.println("Could not create config file, stopping");
                    System.exit(0);
                }
            }

            InputStream configStream = Files.newInputStream(configFile.toPath());
            LinkedHashMap<String, Object> data = (new Yaml()).load(configStream);
            if (data == null) {
                data = new LinkedHashMap<>();
            }

            if (addDefaults) {
                return (new YamlConfiguration(addDefaults(data), configFile)).save();
            }else {
                return (new YamlConfiguration(data, configFile)).save();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return new YamlConfiguration(new LinkedHashMap<>(), configFile);
    }

    private void save() {
        if (config != null) {
            config.save();
        }
    }

    private LinkedHashMap<String, Object> addDefaults(LinkedHashMap<String, Object> config) {
        InputStream defConfigStream = getClass().getResourceAsStream(File.separator + fileName);
        if (defConfigStream == null) {
            System.out.println(ANSI.RED
                    + "Warning: Could NOT find config resource (" + configFile.getPath() + "), could not add defaults!"
                    + ANSI.RESET);
            save();
            return config;
        }
        Map<String, Object> defConfig = (new Yaml()).load(defConfigStream);

        if (!defConfig.isEmpty()) {
            for (String key : defConfig.keySet()) {
                if (!config.containsKey(key)) {
                    config.put(key, defConfig.get(key));
                }
            }
        }
        save();
        return config;
    }

    @SuppressWarnings({"unused", "unchecked"})
    public static class YamlConfiguration {
        @Getter private final LinkedHashMap<String, Object> data;
        private final File configFile;
        public YamlConfiguration(LinkedHashMap<String, Object> data, File configFile) {
            this.data = data;
            this.configFile = configFile;
        }

        public void put(String key, Object value) {
            String[] keys = key.split("\\.");
            if (keys.length <= 1) { data.put(key, value); return; }

            LinkedHashMap<String, Object> dataRecent = data;
            for (int i = 0; i < keys.length-1; i++) {
                String k = keys[i];
                if (dataRecent.containsKey(k)) {
                    if (dataRecent.get(k) instanceof LinkedHashMap) {
                        dataRecent = (LinkedHashMap<String, Object>) dataRecent.get(k);
                    }else {
                        StringBuilder builder = new StringBuilder();
                        for (int j = 0; j <= i; j++) {
                            builder.append(keys[j]).append(" ");
                        }
                        String newKey = builder.toString().trim().replaceAll(" ", ".");
                        if (key.equalsIgnoreCase(newKey)) {
                            System.out.println(ANSI.RED + "Equal Keys: " + key);
                        }
                        put(newKey, new LinkedHashMap<>());

                        dataRecent = new LinkedHashMap<>();
                    }
                }else if (i != keys.length - 1){
                    StringBuilder builder = new StringBuilder();
                    for (int j = 0; j <= i; j++) {
                        builder.append(keys[j]).append(" ");
                    }
                    String newKey = builder.toString().trim().replaceAll(" ", ".");
                    put(newKey, new LinkedHashMap<>());

                    dataRecent = new LinkedHashMap<>();
                }
            }
            dataRecent.put(keys[keys.length - 1], value);

            LinkedHashMap<String, Object> newData = dataRecent;
            newData.put(keys[keys.length - 1], value);
            for (int j = keys.length - 2; j >= 1; j--){
                LinkedHashMap<String, Object> temp = data;
                for (int k = 0; k < j; k++) {
                    temp = (LinkedHashMap<String, Object>) temp.get(keys[k]);
                }
                temp.put(keys[j], newData);
                newData = temp;
            }

            data.put(keys[0], newData);
        }

        public Object get(String key) {
            String[] keys = key.split("\\.");
            LinkedHashMap<String, Object> map = data;
            for (int i = 0; i < keys.length-1; i++) {
                if (map.containsKey(keys[i])) {
                    map = (LinkedHashMap<String, Object>) map.get(keys[i]);
                } else {
                    return null;
                }
            }
            return map.get(keys[keys.length-1]);
        }

        public void putString(String key, String value) {
            put(key, value);
        }

        public void putBoolean(String key, boolean value) {
            put(key, value);
        }

        public void putInteger(String key, int value) {
            put(key, value);
        }

        public void putLong(String key, long value) {
            put(key, value);
        }

        public void putDouble(String key, double value) {
            put(key, value);
        }

        public ConfigurationSection getConfigurationSection(String key) {
            return new ConfigurationSection((Map<String, Object>) data.get(key));
        }

        public String getString(String key) {
            return (String) get(key);
        }

        public int getInteger(String key) {
            return Integer.parseInt(get(key).toString());
        }

        public long getLong(String key) {
            return Long.parseLong(get(key).toString());
        }

        public boolean getBoolean(String key) {
            if (data.containsKey(key)) {
                return (Boolean) get(key);
            }
            return false;
        }

        public List<String> getStringList(String key) {
            if (data.containsKey(key)) {
                return (List<String>) get(key);
            }else {
                return new ArrayList<>();
            }
        }

        public double getDouble(String key) {
            return (Double) get(key);
        }

        public Set<String> getKeys() {
            return data.keySet();
        }

        public boolean contains(String key) { return data.containsKey(key); }

        public YamlConfiguration save() {
            try {
                DumperOptions options = new DumperOptions();
                options.setIndent(2);
                options.setPrettyFlow(true);
                options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                options.setAllowUnicode(true);

                Yaml yaml = new Yaml(options);
                InputStream is = Files.newInputStream(configFile.toPath());
                Map<String, Object> data = yaml.load(is); is.close();

                StringWriter writer = new StringWriter();
                yaml.dump(data, writer);
                BufferedWriter bw = Files.newBufferedWriter(configFile.toPath());
                bw.write(writer.toString());
                bw.close();
                //new Yaml(options).dump(data, new FileWriter(configFile));
            } catch(IOException e) {
                e.printStackTrace();
            }
            return this;
        }
    }

    @SuppressWarnings("unchecked")
    public static class ConfigurationSection {
        @Getter private final Map<String, Object> data;
        public ConfigurationSection(Map<String, Object> data) {
            this.data = data;
        }

        public Object get(String key) {
            return data.get(key);
        }

        public ConfigurationSection getConfigurationSection(String key) {
            return new ConfigurationSection((Map<String, Object>) data.get(key));
        }

        public String getString(String key) {
            return (String) data.get(key);
        }

        public int getInteger(String key) {
            return Integer.parseInt(data.get(key).toString());
        }

        public long getLong(String key) {
            return Long.parseLong(data.get(key).toString());
        }

        public boolean getBoolean(String key) {
            if (data.containsKey(key)) {
                return (Boolean) data.get(key);
            }
            return false;
        }

        public List<String> getStringList(String key) {
            if (data.containsKey(key)) {
                return (List<String>) data.get(key);
            }else {
                return new ArrayList<>();
            }
        }

        public double getDouble(String key) {
            return (Double) data.get(key);
        }

        public Set<String> getKeys() {
            return data.keySet();
        }

        public boolean contains(String key) { return data.containsKey(key); }
    }

    public static class ANSI {
        public static final String RESET = "\u001B[0m";
        public static final String BLACK = "\u001B[30m";
        public static final String RED = "\u001B[31m";
        public static final String GREEN = "\u001B[32m";
        public static final String YELLOW = "\u001B[33m";
        public static final String BLUE = "\u001B[34m";
        public static final String PURPLE = "\u001B[35m";
        public static final String CYAN = "\u001B[36m";
        public static final String WHITE = "\u001B[37m";
    }
}
