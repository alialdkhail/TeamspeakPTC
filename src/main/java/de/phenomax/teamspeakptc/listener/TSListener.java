package de.phenomax.teamspeakptc.listener;

import com.github.theholywaffle.teamspeak3.api.event.*;
import de.phenomax.teamspeakptc.Main;
import de.phenomax.teamspeakptc.data.ChannelData;
import de.phenomax.teamspeakptc.util.ChannelHelper;

/**
 * Created by phenomax on 10.1.2015
 */
public class TSListener {

    public static TS3Listener getListeners() {

        return new TS3Listener() {
            @Override
            public void onTextMessage(TextMessageEvent textMessageEvent) {

            }

            @Override
            public void onClientJoin(ClientJoinEvent clientJoinEvent) {

            }

            @Override
            public void onClientLeave(ClientLeaveEvent clientLeaveEvent) {

            }

            @Override
            public void onServerEdit(ServerEditedEvent serverEditedEvent) {

            }

            @Override
            public void onChannelEdit(ChannelEditedEvent channelEditedEvent) {

            }

            @Override
            public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent channelDescriptionEditedEvent) {

            }

            @Override
            public void onClientMoved(ClientMovedEvent clientMovedEvent) {

                // when the client joins in the specified temp channel creator channel
                if (clientMovedEvent.getTargetChannelId() == Main.getConfig().getChannel()) {

                    int clientID = clientMovedEvent.getClientId();
                    if (ChannelData.doesExist(clientID)) {
                        Main.getTs3api().sendPrivateMessage(clientID, "Your private channel has already been created/loaded!");
                        return;
                    }

                    // if we already have information about the client's private channel
                    if (ChannelData.hasChannel(clientID)) {
                        ChannelHelper.load(clientID);
                        return;
                    }
                    // or create a new channel for the client
                    ChannelHelper.create(clientID);

                }
            }

            @Override
            public void onChannelCreate(ChannelCreateEvent channelCreateEvent) {

            }

            @Override
            public void onChannelDeleted(ChannelDeletedEvent channelDeletedEvent) {

                int channelID = channelDeletedEvent.getChannelId();
                String channelName = Main.getTs3api().getChannelInfo(channelID).getName();
                if (ChannelData.isChannel(channelName)) {
                    ChannelHelper.remove(channelID);
                    Main.execute(() -> Main.getTs3api().sendPrivateMessage(channelDeletedEvent.getInvokerId(), "The user's temporary channel was successfully deleted from the database"));
                }
            }

            @Override
            public void onChannelMoved(ChannelMovedEvent channelMovedEvent) {

            }

            @Override
            public void onChannelPasswordChanged(ChannelPasswordChangedEvent channelPasswordChangedEvent) {

            }

            @Override
            public void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent privilegeKeyUsedEvent) {

            }
        };
    }
}
