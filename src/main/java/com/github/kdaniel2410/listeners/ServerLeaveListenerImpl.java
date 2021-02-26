package com.github.kdaniel2410.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.Nameable;
import org.javacord.api.event.server.ServerLeaveEvent;
import org.javacord.api.listener.server.ServerLeaveListener;

public class ServerLeaveListenerImpl implements ServerLeaveListener {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void onServerLeave(ServerLeaveEvent event) {
        String server = event.getServer().getName();
        String owner = event.getServer().getOwner().map(Nameable::getName).orElse("unknown");
        logger.info("Left server " + server + " owned by " + owner);
    }
}
