package de.phenomax.teamspeakptc.data;

import com.google.gson.*;
import lombok.*;

import java.io.*;
import java.util.*;

/**
 * Created by phenomax on 10.1.2015
 */
@Getter
@Setter
public class Config {

    @Getter
    private static Config instance;

    private boolean debug;
    private String host;
    private int virtualServerID;
    private int queryPort;
    private int channel;
    private String nickname;
    private Map<String, String> login;
    private boolean whitelisted;

    /**
     * Defines the config's default values
     */
    public Config() {
        this.debug = true;
        this.host = "minora.io";
        this.virtualServerID = 1;
        this.queryPort = 10011;
        this.channel = 5;
        this.nickname = "TeamspeakPTC";
        this.login = new HashMap<>();
        this.login.put("username", "testbot");
        this.login.put("password", "X37kOEfs");
        this.whitelisted = false;
    }

    public static void load(File file) {

        instance = fromFile(file);

        // no config file found: create a new
        if (instance == null)

        {
            Config config = fromDefaults();
            instance = config;
            config.toFile(file);
            System.out.println("[TeamspeakPTC] Created new file config.json");
            System.out.println("[TeamspeakPTC] Please modify the config and restart the bot!");
        }

    }

    private static Config fromDefaults() {
        Config config = new Config();
        return config;
    }

    /**
     * Saves the config
     *
     * @param file the config file
     */
    public void toFile(File file) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonConfig = gson.toJson(this);
        FileWriter writer;
        try {
            writer = new FileWriter(file);
            writer.write(jsonConfig);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the config from the specified file
     *
     * @param configFile the file from which to load the config
     * @return a new instance of Config
     */
    private static Config fromFile(File configFile) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(configFile)));
            return gson.fromJson(reader, Config.class);
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
