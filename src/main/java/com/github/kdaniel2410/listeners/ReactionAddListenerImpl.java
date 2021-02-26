package com.github.kdaniel2410.listeners;

import com.github.kdaniel2410.handlers.DatabaseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;

import java.sql.SQLException;

public class ReactionAddListenerImpl implements ReactionAddListener {

    private static final Logger logger = LogManager.getLogger();
    private final DatabaseHandler databaseHandler;

    public ReactionAddListenerImpl(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    @Override
    public void onReactionAdd(ReactionAddEvent event) {
        if (event.getServer().map(DiscordEntity::getId).isPresent()) {
            try {
                databaseHandler.incrementField(event.getUserId(), event.getServer().map(DiscordEntity::getId).get(), "reactionCount");
            } catch (SQLException e) {
                logger.error(e);
            }
        }
    }
}
