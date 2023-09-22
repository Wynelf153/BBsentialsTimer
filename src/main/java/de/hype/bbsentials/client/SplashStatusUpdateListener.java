package de.hype.bbsentials.client;

import de.hype.bbsentials.communication.BBsentialConnection;
import de.hype.bbsentials.packets.packets.SplashNotifyPacket;
import de.hype.bbsentials.packets.packets.SplashUpdatePacket;

import java.util.concurrent.TimeUnit;

import static de.hype.bbsentials.client.BBsentials.executionService;
import static de.hype.bbsentials.client.BBsentials.splashLobby;

public class SplashStatusUpdateListener implements Runnable {
    BBsentialConnection connection;
    SplashNotifyPacket packet;
    private String status = SplashUpdatePacket.STATUS_WAITING;
    public String newStatus = SplashUpdatePacket.STATUS_WAITING;
    public boolean splashed = false;
    public boolean full = false;

    public SplashStatusUpdateListener(BBsentialConnection connection, SplashNotifyPacket packet) {
        this.connection = connection;
        this.packet = packet;
    }

    @Override
    public void run() {
        BBsentials.splashLobby = true;
        while (BBsentials.splashLobby) {
            int maxPlayerCount = BBUtils.getMaximumPlayerCount() - 5;
            while (splashLobby && !(status.equals(SplashUpdatePacket.STATUS_DONE) || status.equals(SplashUpdatePacket.STATUS_CANCELED))) {
                if (!full&&(BBUtils.getPlayerCount() >= maxPlayerCount)) {
                    newStatus = SplashUpdatePacket.STATUS_FULL;
                    full=true;
                }
                if (!status.equals(newStatus)) {
                    status = newStatus;
                    connection.sendPacket(new SplashUpdatePacket(packet.splashId, status));
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
        if (splashed) {
            newStatus = SplashUpdatePacket.STATUS_DONE;
        }
        else {
            newStatus = SplashUpdatePacket.STATUS_CANCELED;
        }
        if (!status.equals(newStatus)) {
            status = newStatus;
            connection.sendPacket(new SplashUpdatePacket(packet.splashId, status));
        }
    }

    public void setStatus(String newStatus) {
        this.newStatus = newStatus;
        if (newStatus.equals(SplashUpdatePacket.STATUS_SPLASHING)) {
            executionService.schedule(() -> {
                splashed=true;
                setStatus(SplashUpdatePacket.STATUS_DONE);
                splashLobby = false;
            }, 1, TimeUnit.MINUTES);
        }
    }
}