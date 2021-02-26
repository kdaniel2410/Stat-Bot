package com.github.kdaniel2410.listeners;

import com.github.kdaniel2410.handlers.DatabaseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.sql.SQLException;

public class MessageCreateListenerImpl implements MessageCreateListener {

    private static final Logger logger = LogManager.getLogger();
    private final DatabaseHandler databaseHandler;

    public MessageCreateListenerImpl(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (event.getServer().map(DiscordEntity::getId).isPresent()) {
            try {
                databaseHandler.incrementField(event.getMessageAuthor().getId(), event.getServer().map(DiscordEntity::getId).get(), "messageCount");
            } catch (SQLException e) {
                logger.error(e);
            }
        }
    }
}
