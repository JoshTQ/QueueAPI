package com.tara3208.valuxtrial.main;

import com.tara3208.valuxtrial.api.FileUtils;
import com.tara3208.valuxtrial.api.managers.QueueManager;
import com.tara3208.valuxtrial.api.types.QueueSystem;
import com.tara3208.valuxtrial.main.events.Connection;
import com.tara3208.valuxtrial.main.events.Disconnect;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tara3208 on 7/15/17.
 * This has been created privately.
 * Copyright applies. Breach of this is not warranted
 *
 * Modified by DaeM0nS on 6/03/19.
 * Add serversize list, change the line 81 to set specified size for each servers
 * Add serversize in the config file
 * Add some test fields in the default config (lists)
 */
public class Queues extends Plugin {

    public static String message = "";
    public static int size = 2;
    public static int players = 0;
    public static List servers;
    public static List serverssize;
    public static ChatMessageType chatMessageType;
    private static Queues instance;
    private static QueueManager queueManager;

    public static Queues getInstance() {
        return instance;
    }

    public static QueueManager getQueueManagement() {
        return queueManager;
    }

    @Override
    public void onEnable() {
        instance = this;
        queueManager = new QueueManager();
        make();
        registerListeners();
        registerQueues();
    }

    @Override
    public void onDisable() {
        setNull();

    }

    public void setNull() {
        queueManager = null;
        instance = null;
        message = null;
        size = -1;
        servers = null;
        serverssize = null;
        chatMessageType = null;
    }

    private void registerListeners() {
        ProxyServer.getInstance().getPluginManager().registerListener(this, new Connection());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new Disconnect());
    }

    private void registerQueues() {
        ProxyServer.getInstance().getConsole().sendMessage(new TextComponent("Successfully added a queue for: "));

        Configuration configuration = FileUtils.getConfiguration("config.yml");

        configuration.getStringList("servers").forEach(serverName -> {
            QueueSystem hub = new QueueSystem(ProxyServer.getInstance().getServerInfo(serverName), TimeUnit.SECONDS, size, Integer.valueOf(this.serverssize.get(this.servers.indexOf(serverName)).toString()));
            getQueueManagement().addQueue(hub);
            ProxyServer.getInstance().getConsole().sendMessage(new TextComponent("- " + serverName));
        });
    }

    private void make() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        Configuration configuration = FileUtils.getConfiguration("config.yml");

        if (configuration == null) {
            return;
        }


        // MESSAGE
        if (configuration.get("message") == null) {
            configuration.set("message", "&4[Queue] &7You have been added to a queue! Position: &c#%position%/%size%");
            this.message = "&4[Queue] &7You have been added to a queue! Position: &c#%position%/%size%";
        } else {
            this.message = configuration.getString("message");
        }

        // Delay

        if (configuration.get("delay") == null) {
            configuration.set("delay", 2);
            this.size = 2;
        } else {
            this.size = configuration.getInt("delay");
        }

        // Servers
        if (configuration.get("servers") == null) {
            configuration.set("servers", new String[]{"Server1","Server2","Server3"});
            this.servers = null;
        }else{
            this.servers = configuration.getStringList("servers");
        }

        // ServersSizes
        if (configuration.get("serverssize") == null) {
            configuration.set("serverssize", new String[]{"100","50","50"});
            this.serverssize = null;
        }else{
            this.serverssize = configuration.getStringList("serverssize");
        }


        if (configuration.get("messageType") == null) {
            configuration.set("messageType", "CHAT");
            this.chatMessageType = ChatMessageType.CHAT;
        } else {
            this.chatMessageType = ChatMessageType.valueOf(configuration.getString("messageType"));
        }

        FileUtils.save(configuration, "config.yml");
    }

}
