package de.phenomax.teamspeakpts.data;

import com.google.gson.Gson;
import lombok.Getter;
import org.json.simple.JSONObject;

/**
 * Created by phenomax on 10.1.2015
 */
@Getter
public class ChannelData {

    private String name;
    private String password;
    private String owner;
    private String description;
    private final static Gson gson = new Gson();

    public ChannelData(String name, String owner, String password, String description) {
        this.name = name;
        this.owner = owner;
        this.password = password;
        this.description = description;
    }

    public static void load() {

    }

    /**
     * Deserializes the input to ChannelData
     *
     * @param input the serialied ChannelData
     * @return a new ChannelData object
     */
    public static ChannelData deserialize(String input) {
        JSONObject jsonObject = gson.fromJson(input, JSONObject.class);

        return new ChannelData(jsonObject.get("name").toString(),
                jsonObject.get("password").toString(),
                jsonObject.get("owner").toString(),
                jsonObject.get("description").toString());
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
        jsonObject.put("password", this.password);
        jsonObject.put("description", this.description);

        return jsonObject;
    }
}
