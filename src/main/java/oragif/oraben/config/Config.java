package oragif.oraben.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import oragif.oraben.Oraben;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class Config {
    private static Config instance;
    private final File configFolder = new File("config");
    private final File configFile = new File(configFolder, "Oraben.json");
    public ConfigData configData;

    public static class ConfigData {
        private String Tpa_module;
        public Boolean tpaEnabled;
        public Integer tpaLvlRequired;
        public Integer tpaLvlPerBlock;
        public Integer tpaBlockModifier;
        public Integer tpaTimeout;
        public String tpaNotEnoughLevelsMsg;
        public String tpaTimeoutMsg;
        public String tpaRequestMsg;
        public String tpaPendingMsg;
        public String tpaSendToMsg;
        public String tpaCancelledToMsg;
        public String tpaCancelledFromMsg;
        public String tpaAcceptedMsg;
        public String tpaWrongDimension;
        private String Tpa_module_end;

        private String Sleep_module;
        public Boolean sleepEnabled;
        public Integer sleepForcePermissionLevel;
        public Integer sleepPercentage;
        public Integer sleepWakeUpTime;
        public Boolean sleepClearWeatherEnabled;
        public String sleepSkipNightMsg;
        public String sleepClearWeatherMsg;
        public String sleepStartSleepMsg;
        public String sleepStopSleepMsg;
        public String sleepToEarlyMsg;
        private String Sleep_module_end;

        private String Mob_Egg_module;
        public Boolean mobEggEnabled;
        public Boolean mobEggSurvivalSpawnerChange;
        public List<List<String>> mobEggList;
        private String Mob_Egg_module_end;

        private String Misc;
        public Boolean signEditor;
        public Integer restockTime;
        private String Misc_end;

        //Validate data is within threshold
        public void validate() {
            if (tpaLvlRequired < 0) { tpaLvlRequired = 10; }
            if (tpaLvlPerBlock < 0) { tpaLvlPerBlock = 1; }
            if (tpaBlockModifier < 0) { tpaBlockModifier = 16; }
            if (0 > sleepPercentage || sleepPercentage > 100) { sleepPercentage = 50; }
            if (0 > sleepWakeUpTime || sleepWakeUpTime > 24000) { sleepWakeUpTime = 1000; }
            if (0 > restockTime) { restockTime = 12000; }
        }
    }

    //Adds default values if missing
    public ConfigData addMissing(ConfigData data) {
        data.Tpa_module = "-- Tpa module --";
        data.tpaEnabled = getValueOrDefault(data.tpaEnabled, true);
        data.tpaLvlRequired = getValueOrDefault(data.tpaLvlRequired, 10);
        data.tpaLvlPerBlock = getValueOrDefault(data.tpaLvlPerBlock, 1);
        data.tpaBlockModifier = getValueOrDefault(data.tpaBlockModifier, 16);
        data.tpaTimeout = getValueOrDefault(data.tpaTimeout, 60);
        data.tpaNotEnoughLevelsMsg = getValueOrDefault(data.tpaNotEnoughLevelsMsg, "Not enough levels, required: {Required}");
        data.tpaTimeoutMsg = getValueOrDefault(data.tpaTimeoutMsg, "Request to {Player} timed out");
        data.tpaRequestMsg = getValueOrDefault(data.tpaRequestMsg, "Tpa requested from {Player}");
        data.tpaPendingMsg = getValueOrDefault(data.tpaPendingMsg, "Tpa request already pending to {Player}");
        data.tpaSendToMsg = getValueOrDefault(data.tpaSendToMsg, "Tpa request send to {Player}, levels required: {Required}");
        data.tpaCancelledFromMsg = getValueOrDefault(data.tpaCancelledFromMsg, "Tpa request cancelled to {Player}");
        data.tpaCancelledToMsg = getValueOrDefault(data.tpaCancelledToMsg, "Tpa request cancelled from {Player}");
        data.tpaAcceptedMsg = getValueOrDefault(data.tpaAcceptedMsg, "Tpa request to {Player} accepted, cost: {Levels}");
        data.tpaWrongDimension = getValueOrDefault(data.tpaWrongDimension, "Not in the same dimension, {Player} is in {Dimension}");
        data.sleepToEarlyMsg = getValueOrDefault(data.sleepToEarlyMsg, "Too early to sleep");
        data.Tpa_module_end = "-- Tpa module end --";

        data.Sleep_module = "-- Sleep module --";
        data.sleepEnabled = getValueOrDefault(data.sleepEnabled, true);
        data.sleepForcePermissionLevel = getValueOrDefault(data.sleepForcePermissionLevel, 4);
        data.sleepPercentage = getValueOrDefault(data.sleepPercentage, 50);
        data.sleepWakeUpTime = getValueOrDefault(data.sleepWakeUpTime, 1000);
        data.sleepClearWeatherEnabled = getValueOrDefault(data.sleepClearWeatherEnabled, true);
        data.sleepSkipNightMsg = getValueOrDefault(data.sleepSkipNightMsg, "Skipping night");
        data.sleepClearWeatherMsg = getValueOrDefault(data.sleepClearWeatherMsg, "Weather cleared");
        data.sleepStartSleepMsg = getValueOrDefault(data.sleepStartSleepMsg, "{Player} is now sleeping {Sleeping}/{Required}");
        data.sleepStopSleepMsg = getValueOrDefault(data.sleepStopSleepMsg, "{Player} is no longer sleeping {Sleeping}/{Required}");
        data.Sleep_module_end = "-- Sleep module end --";

        data.Mob_Egg_module = "-- Mob egg module --";
        data.mobEggEnabled = getValueOrDefault(data.mobEggEnabled, true);
        data.mobEggSurvivalSpawnerChange = getValueOrDefault(data.mobEggSurvivalSpawnerChange, false);
        data.mobEggList = getValueOrDefault(data.mobEggList, List.of(Arrays.asList("minecraft:villager", "minecraft:diamond")));
        data.Mob_Egg_module_end = "-- Mob egg module end --";

        data.Misc = "-- Misc --";
        data.signEditor = getValueOrDefault(data.signEditor, true);
        data.restockTime = getValueOrDefault(data.restockTime, 12000);
        data.Misc_end = "-- Misc end --";

        return data;
    }

    Config() {
        if (!this.load()) {
            this.configData = addMissing(new ConfigData());
            this.configData.validate();
            this.save();
        }
    }

    public void save() {
        try {
            if (!configFolder.exists()) { configFolder.mkdirs(); }
            if (!configFile.exists()) { configFile.createNewFile(); }

            FileWriter fw = new FileWriter(this.configFile, false);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            fw.write(gson.toJson(this.configData));
            fw.flush();
            fw.close();

            Oraben.log("Saved Config File");
        } catch (IOException error) {
            Oraben.log("Failed to create config");
        }
    }

    public boolean load() {
        try {
            FileReader fw = new FileReader(this.configFile);
            JsonReader getLocalJsonFile = new JsonReader(fw);
            Gson gson = new Gson();

            ConfigData data = gson.fromJson(getLocalJsonFile, ConfigData.class);
            this.configData = addMissing(data);
            this.configData.validate();
            fw.close();
            Oraben.log("Config loaded");
            save();
            return true;
        } catch (IOException e) {
            Oraben.log("Failed to load config");
            return false;
        }
    }

    public static Config get() {
        if (Config.instance == null) {
            Config.instance = new Config();
        }
        return Config.instance;
    }

    public static <T> T getValueOrDefault(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }
}