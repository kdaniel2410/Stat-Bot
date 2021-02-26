package com.github.kdaniel2410.listeners;

import com.github.kdaniel2410.handlers.DatabaseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.event.server.member.ServerMemberBanEvent;
import org.javacord.api.listener.server.member.ServerMemberBanListener;

import java.sql.SQLException;

public class ServerMemberBanListenerImpl implements ServerMemberBanListener {

    private static final Logger logger = LogManager.getLogger();
    private final DatabaseHandler databaseHandler;

    public ServerMemberBanListenerImpl(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    @Override
    public void onServerMemberBan(ServerMemberBanEvent event) {
        try {
            databaseHandler.removeServerMember(event.getUser().getId(), event.getServer().getId());
        } catch (SQLException e) {
            logger.error(e);
        }
    }
}
