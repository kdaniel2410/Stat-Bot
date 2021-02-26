package com.github.kdaniel2410.listeners;

import com.github.kdaniel2410.handlers.DatabaseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.Nameable;
import org.javacord.api.event.server.ServerLeaveEvent;
import org.javacord.api.listener.server.ServerLeaveListener;

import java.sql.SQLException;

public class ServerLeaveListenerImpl implements ServerLeaveListener {

    private static final Logger logger = LogManager.getLogger();
    private final DatabaseHandler databaseHandler;

    public ServerLeaveListenerImpl(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }


    @Override
    public void onServerLeave(ServerLeaveEvent event) {
        String server = event.getServer().getName();
        String owner = event.getServer().getOwner().map(Nameable::getName).orElse("unknown");
        logger.info("Left server " + server + " owned by " + owner);
        try {
            databaseHandler.removeServer(event.getServer().getId());
        } catch (SQLException e) {
            logger.error(e);
        }
    }
}
