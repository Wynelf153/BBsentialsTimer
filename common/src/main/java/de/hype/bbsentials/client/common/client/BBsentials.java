package de.hype.bbsentials.client.common.client;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.client.Commands.Commands;
import de.hype.bbsentials.client.common.communication.BBsentialConnection;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BBsentials {
    public static Config config;
    public static BBsentialConnection connection;
    private static boolean initialised = false;
    public static Commands coms;
    public static ScheduledExecutorService executionService = Executors.newScheduledThreadPool(1000);
    public static List<Runnable> onServerJoin = new ArrayList<>();
    public static List<Runnable> onServerLeave = new ArrayList<>();
    public static SplashStatusUpdateListener splashStatusUpdateListener;
    public static Thread bbthread;
    public static Chat chat = new Chat();
    public static Thread debugThread;

    public static Config getConfig() {
        return config;
    }

    public static void connectToBBserver() {
        connectToBBserver(config.connectToBeta);
    }

    /**
     * Checks if still connected to the Server.
     *
     * @return true if it connected; false if old connection is kept.
     */
    public static boolean conditionalReconnectToBBserver() {
        if (!connection.isConnected()) {
            Chat.sendPrivateMessageToSelfInfo("Reconnecting");
            connectToBBserver(config.connectToBeta);
            return true;
        }
        return false;
    }

    public static void connectToBBserver(boolean beta) {
        executionService.execute(() -> {
            if (connection != null) {
                connection.close();
            }
            bbthread = new Thread(() -> {
                connection = new BBsentialConnection();
                coms = new Commands();
                if (beta) {
                    connection.connect(config.getBBServerURL(), 5011);
                }
                else {
                    connection.connect(config.getBBServerURL(), 5000);
                }
            });
            bbthread.start();
        });
    }

    /**
     * Runs the mod initializer on the client environment.
     */

    public synchronized static void onServerJoin() {
        onServerLeave();
        executionService.schedule(() -> {
            for (int i = 0; i < onServerJoin.size(); i++) {
                onServerJoin.remove(i).run();
            }
        }, 5, TimeUnit.SECONDS);
    }

    public static void onServerLeave() {
        Chat.sendPrivateMessageToSelfDebug(String.valueOf(EnvironmentCore.utils.getPlayerCount()));
        for (int i = 0; i < onServerLeave.size(); i++) {
            onServerLeave.remove(i).run();
        }
    }

    public static void init() {
        config = Config.load();
        debugThread = new Thread(
                EnvironmentCore.debug
        );
        debugThread.start();
        debugThread.setName("Debug Thread");
        if (Config.isBingoTime() || config.overrideBingoTime()) {
            connectToBBserver();
        }
        splashStatusUpdateListener = new SplashStatusUpdateListener(null, null);
        EnvironmentCore.mcUtils.registerSplashOverlay();

    }
}