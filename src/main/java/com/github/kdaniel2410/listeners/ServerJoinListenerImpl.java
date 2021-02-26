package com.github.kdaniel2410.listeners;

import com.github.kdaniel2410.handlers.DatabaseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.Nameable;
import org.javacord.api.event.server.ServerJoinEvent;
import org.javacord.api.listener.server.ServerJoinListener;

public class ServerJoinListenerImpl implements ServerJoinListener {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void onServerJoin(ServerJoinEvent event) {
        String server = event.getServer().getName();
        String owner = event.getServer().getOwner().map(Nameable::getName).orElse("unknown");
        logger.info("Joined server " + server + " owned by " + owner);
    }
}
