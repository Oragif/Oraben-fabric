package oragif.oraben.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import oragif.oraben.Oraben;

import java.io.*;

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

        //Validate data is within threshold
        public void validate() {
            if (tpaLvlRequired < 0) { tpaLvlRequired = 10; }
            if (tpaLvlPerBlock < 0) { tpaLvlPerBlock = 1; }
            if (tpaBlockModifier < 0) { tpaBlockModifier = 16; }
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
        data.tpaSendToMsg = getValueOrDefault(data.tpaSendToMsg, "Tpa request send to {Player}");
        data.tpaCancelledFromMsg = getValueOrDefault(data.tpaCancelledFromMsg, "Tpa request cancelled to {Player}");
        data.tpaCancelledToMsg = getValueOrDefault(data.tpaCancelledToMsg, "Tpa request cancelled from {Player}");
        data.tpaAcceptedMsg = getValueOrDefault(data.tpaAcceptedMsg, "Tpa request to {Player} accepted, cost: {Levels}");
        data.tpaWrongDimension = getValueOrDefault(data.tpaWrongDimension, "Not in the same dimension, {Player} is in {Dimension}");
        data.Tpa_module_end = "-- Tpa module end --";

        return data;
    }

    Config() {
        if (!this.load()) {
            this.configData = addMissing(new ConfigData());
            this.configData.validate();
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
