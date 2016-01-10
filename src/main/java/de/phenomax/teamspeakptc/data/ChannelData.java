package de.phenomax.teamspeakptc.data;

import com.github.theholywaffle.teamspeak3.api.Codec;
import com.google.gson.Gson;
import de.phenomax.teamspeakptc.Main;
import lombok.Getter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by phenomax on 10.1.2015
 */
@Getter
public class ChannelData {

    private final static Gson gson = new Gson();
    private final static JSONParser parser = new JSONParser();

    private String name;
    private int owner;
    private HashMap<String, Object> settings = new HashMap<>();

    private static List<ChannelData> data = new ArrayList<>();

    @Getter
    private static Map<Integer, Integer> activeChannels = new ConcurrentHashMap<>();

    public ChannelData(String name, int owner, String password, String description, String filePath, String topic, int maxClients,
                       long iconID, int codecQuality, Codec codec, int talkPower) {
        this.name = name;
        this.owner = owner;
        settings.put("password", password);
        settings.put("description", description);
        settings.put("filePath", filePath);
        settings.put("topic", topic);
        settings.put("maxClients", maxClients);
        settings.put("iconID", iconID);
        settings.put("codecQuality", codecQuality);
        settings.put("codec", codec.toString());
        settings.put("talkPower", talkPower);
        data.add(this);
        save();
    }

    /**
     * Loads the ChannelData from the data.json and saves it in the data ArrayLisdt
     */
    public static void load() {

        Main.execute(() -> {

            try {
                File file = new File("files/", "data.json");
                if (!file.exists()) {
                    file.getParentFile().mkdir();
                    JSONObject write = new JSONObject();

                    FileWriter fileWriter = new FileWriter("files/data.json");
                    fileWriter.write(write.toJSONString());
                    fileWriter.flush();
                    fileWriter.close();

                    System.out.println("[TeamspeakPTC] Created new file data.json");
                    return;
                }

                Object obj = parser.parse(new FileReader("files/data.json"));
                JSONObject jsonObject = (JSONObject) obj;
                List<JSONObject> jsonObjectList = (List<JSONObject>) jsonObject.get("data");

                data.addAll(jsonObjectList.stream().map(ChannelData::deserialize).collect(Collectors.toList()));

                System.out.println("[TeamspeakPTC] Successfully loaded ChannelData from file data.json. Version: " + jsonObject.get("version"));

            } catch (IOException | ParseException e) {
                System.out.println("[TeamspeakPTC] Failed loading data.json in files/data.json !");
                e.printStackTrace();
            }
        });

    }

    /**
     * Deserializes the input to ChannelData
     *
     * @param input the serialized ChannelData
     * @return a new ChannelData object
     */
    public static ChannelData deserialize(String input) {
        JSONObject jsonObject = gson.fromJson(input, JSONObject.class);

        HashMap<String, Object> settings = (HashMap<String, Object>) jsonObject.get("settings");
        return new ChannelData(jsonObject.get("name").toString(),
                Integer.valueOf(jsonObject.get("owner").toString()),
                settings.get("password").toString(),
                settings.get("description").toString(),
                settings.get("filePath").toString(),
                settings.get("topic").toString(),
                Integer.valueOf(settings.get("maxClients").toString()),
                Long.valueOf(settings.get("iconID").toString()),
                Integer.valueOf(settings.get("codecQuality").toString()),
                Codec.valueOf(settings.get("codec").toString()),
                Integer.valueOf(settings.get("talkPower").toString()));
    }

    /**
     * Deserializes the input to ChannelData
     *
     * @param input the serialized ChannelData
     * @return a new ChannelData object
     */
    public static ChannelData deserialize(JSONObject input) {

        HashMap<String, Object> settings = (HashMap<String, Object>) input.get("settings");

        return new ChannelData(input.get("name").toString(),
                Integer.valueOf(input.get("owner").toString()),
                settings.get("password").toString(),
                settings.get("description").toString(),
                settings.get("filePath").toString(),
                settings.get("topic").toString(),
                Integer.valueOf(settings.get("maxClients").toString()),
                Long.valueOf(settings.get("iconID").toString()),
                Integer.valueOf(settings.get("codecQuality").toString()),
                Codec.valueOf(settings.get("codec").toString()),
                Integer.valueOf(settings.get("talkPower").toString()));
    }

    /**
     * Serializes the ChannelData
     *
     * @return the formatted ChannelData as a JSONObject
     */
    public JSONObject serialize() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", this.name);
        jsonObject.put("owner", this.owner);
        jsonObject.put("settings", this.settings);

        return jsonObject;
    }

    /**
     * Checks whether the specified channel is a temporary user channel or not
     *
     * @param name the name of the channel
     * @return whether it is true or false
     */
    public static boolean isChannel(String name) {
        for (ChannelData channelData : data) {
            if (channelData.getName().equals(name))
                return true;
        }
        return false;
    }

    /**
     * Checks whether the client already has created a temporary channel, which is saved in the data.json
     *
     * @param clientID the client
     * @return whether it is true or false
     */
    public static boolean hasChannel(int clientID) {
        for (ChannelData channelData : data) {
            if (channelData.getOwner() == clientID)
                return true;
        }
        return false;
    }

    /**
     * Checks whether the client's temporary channel does already exist
     *
     * @param clientID the client
     * @return whether it is true or false
     */
    public static boolean doesExist(int clientID) {
        String channel = "";
        for (ChannelData channelData : data) {
            if (channelData.getOwner() == clientID)
                channel = channelData.getName();
        }
        return Main.getTs3api().getChannelByNameExact(channel, false) != null;

    }

    /**
     * Gets the client's ChannelData
     *
     * @param clientID the client
     * @return the ChannelData
     */
    public static ChannelData get(int clientID) {
        for (ChannelData channelData : data) {
            if (channelData.getOwner() == clientID)
                return channelData;
        }
        return null;
    }

    /**
     * Gets the client's ChannelData
     *
     * @param channelName the name of the channel
     * @return the ChannelData
     */
    public static ChannelData get(String channelName) {
        for (ChannelData channelData : data) {
            if (channelData.getName().equals(channelName))
                return channelData;
        }
        return null;
    }


    /**
     * Saves the current ChannelData to the JSON file
     */
    public void save() {

        Main.execute(() -> {
            JSONObject write = new JSONObject();
            write.put("version", 0.1);
            write.put("data", data);

            try {
                FileWriter fileWriter = new FileWriter("files/data.json");
                fileWriter.write(write.toJSONString());
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Removes the current ChannelData and saves the changes to the data.json
     */
    public void remove() {
        data.remove(this);
        save();
    }
}
