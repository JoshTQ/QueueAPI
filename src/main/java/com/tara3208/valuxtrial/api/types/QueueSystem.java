package com.tara3208.valuxtrial.api.types;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import com.tara3208.valuxtrial.main.Queues;

/**
 * Created by Tara3208 on 7/15/17.
 * This has been created privately.
 * Copyright applies. Breach of this is not warranted
 *
 * Modified by DaeM0nS on 6/03/19.
 * Line61 : Change server (needed to be final) to serverInfo (private field)
 */
public class QueueSystem {

    private java.util.Queue queue;

    private ServerInfo serverInfo;

    private TimeUnit timeUnit;

    private int delay;

    private int players;


    public QueueSystem(ServerInfo server, TimeUnit timeUnit, int delay, int players) {
        this.queue = new LinkedList();
        this.serverInfo = server;
        this.timeUnit = timeUnit;
        this.delay = delay;
        this.players = players;


        Queues.getInstance().getProxy().getScheduler().schedule(Queues.getInstance(), new Runnable() {

            @Override
            public void run() {

                if (queue.isEmpty()) return;

                ProxiedPlayer proxiedPlayer = getDonorPlayer();

                if (proxiedPlayer == null) {

                    Object toMove = queue.element();

                    proxiedPlayer = (ProxiedPlayer) toMove;
                }

                proxiedPlayer.connect(serverInfo);

                queue.remove(proxiedPlayer);

            }
        }, 0, delay, timeUnit);

    }

    public void addToQueue(ProxiedPlayer proxiedPlayer) {
        if (players >= ProxyServer.getInstance().getPlayers().size()) {
            if (queue.contains(proxiedPlayer)) return;

            queue.add(proxiedPlayer);
            proxiedPlayer.sendMessage(Queues.getInstance().chatMessageType, new TextComponent(ChatColor.translateAlternateColorCodes('&',
                    Queues.getInstance().message)
                    .replaceAll("%position%", String.valueOf(getPosition(proxiedPlayer)))
                    .replaceAll("%size%", String.valueOf(getQueues().size()))));

        }
    }

    public boolean inQueue(ProxiedPlayer proxiedPlayer) {
        return queue.contains(proxiedPlayer);
    }

    public void reboot() {
        queue.clear();
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public Queue getQueues() {
        return queue;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public int getPosition(ProxiedPlayer proxiedPlayer) {
        int position = 1;
        for (Object player : queue) {
            if (player.equals(proxiedPlayer)) {
                return position;
            }

            position++;
        }

        return -1;
    }

    public int getPlayers() {
        return players;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    public ProxiedPlayer getDonorPlayer() {
        for (Object objectPlayer : queue) {
            ProxiedPlayer proxiedPlayer = (ProxiedPlayer) objectPlayer;

            if (proxiedPlayer.hasPermission("queuesystem.donor"))
                return proxiedPlayer;
        }

        return null;
    }

}
