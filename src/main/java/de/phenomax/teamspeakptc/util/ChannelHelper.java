package de.phenomax.teamspeakptc.util;

import com.github.theholywaffle.teamspeak3.*;
import com.github.theholywaffle.teamspeak3.api.*;
import com.github.theholywaffle.teamspeak3.api.wrapper.*;
import de.phenomax.teamspeakptc.Main;
import de.phenomax.teamspeakptc.data.ChannelData;

import java.util.*;

/**
 * Created by phenomax on 10.1.2015
 */
public class ChannelHelper {

    /**
     * Creates a new channel for the specified client
     *
     * @param clientID the client's id
     */
    public static void create(int clientID) {

        Main.execute(() -> {
            final HashMap<ChannelProperty, String> properties = new HashMap<>();
            final TS3Api api = Main.getTs3api();

            String loginName = api.getClientInfo(clientID).getLoginName();

            properties.put(ChannelProperty.CHANNEL_FLAG_TEMPORARY, "1");
            properties.put(ChannelProperty.CHANNEL_DESCRIPTION, "private channel of" + loginName);
            properties.put(ChannelProperty.CHANNEL_ORDER, "0");
            int channelID = api.createChannel(loginName, properties);

            // now get all of the channel's properies and save this to the ChannelData
            ChannelInfo info = api.getChannelInfo(channelID);
            new ChannelData(loginName, clientID, info.getPassword(), info.getDescription(), info.getFilePath(), info.getTopic(), info.getMaxClients(), info.getIconId(),
                    info.getCodecQuality(), info.getCodec(), info.getNeededTalkPower());
        });
    }

    /**
     * Loads the specified client's channel and moves him into the loaded channel
     *
     * @param clientID the client's id
     */
    public static void load(int clientID) {
        Main.execute(() -> {
            final ChannelData channelData = ChannelData.get(clientID);
            final TS3Api api = Main.getTs3api();
            final HashMap<ChannelProperty, String> properties = new HashMap<>();

            // get and set channel info
            properties.put(ChannelProperty.CHANNEL_FLAG_TEMPORARY, "1");
            properties.put(ChannelProperty.CHANNEL_ORDER, "0");
            properties.put(ChannelProperty.CHANNEL_FLAG_PASSWORD, channelData.getSettings().get("password").equals("") ? "0" : "1");
            properties.put(ChannelProperty.CHANNEL_PASSWORD, channelData.getSettings().get("password").toString());
            properties.put(ChannelProperty.CHANNEL_ICON_ID, channelData.getSettings().get("iconID").toString());
            properties.put(ChannelProperty.CHANNEL_CODEC, channelData.getSettings().get("codec").toString());
            properties.put(ChannelProperty.CHANNEL_CODEC_QUALITY, channelData.getSettings().get("codecQuality").toString());
            properties.put(ChannelProperty.CHANNEL_NEEDED_TALK_POWER, channelData.getSettings().get("talkPower").toString());
            properties.put(ChannelProperty.CHANNEL_MAXCLIENTS, channelData.getSettings().get("maxClients").toString());
            properties.put(ChannelProperty.CHANNEL_TOPIC, channelData.getSettings().get("topic").toString());
            properties.put(ChannelProperty.CHANNEL_FILEPATH, channelData.getSettings().get("filePath").toString());

            int channelID = api.createChannel(channelData.getName(), properties);
            api.moveClient(clientID, channelID);
            api.sendPrivateMessage(clientID, "Welcome in your personal temporary channel!");
        });
    }

    public static void remove(int channelID) {
        Main.execute(() -> {
            String name = Main.getTs3api().getChannelInfo(channelID).getName();
            ChannelData.get(name).remove();

        });
    }

}
