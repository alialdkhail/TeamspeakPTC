package de.phenomax.teamspeakptc;

import com.github.theholywaffle.teamspeak3.*;
import de.phenomax.teamspeakptc.data.*;
import de.phenomax.teamspeakptc.listener.*;
import lombok.*;

import java.io.*;
import java.util.concurrent.*;
import java.util.logging.Level;

/**
 * Created by phenomax on 10.1.2015
 */
public class Main {

    @Getter
    public static Main instance;
    @Getter
    private static ExecutorService executorService = Executors.newCachedThreadPool();
    @Getter
    private static Config config;
    @Getter
    private static TS3Api ts3api;

    public static void main(String[] args) {
        ChannelData.load();
        executorService.execute(() -> {
            Config.load(new File("files", "config.json"));
            config = Config.getInstance();

            // initialize ts3 api
            final TS3Config tsConfig = new TS3Config();
            tsConfig.setHost(config.getHost());
            tsConfig.setDebugLevel(config.isDebug() ? Level.ALL : Level.WARNING);
            tsConfig.setQueryPort(config.getQueryPort());
            tsConfig.setFloodRate(config.isWhitelisted() ? TS3Query.FloodRate.UNLIMITED : TS3Query.FloodRate.DEFAULT);

            final TS3Query query = new TS3Query(tsConfig);
            query.connect();

            ts3api = query.getApi();
            ts3api.setNickname(config.getNickname());
            ts3api.selectVirtualServerById(config.getVirtualServerID());
            ts3api.registerAllEvents();
            ts3api.addTS3Listeners(TSListener.getListeners());
        });

    }

    public static void execute(Runnable task) {
        executorService.execute(task);
    }
}
