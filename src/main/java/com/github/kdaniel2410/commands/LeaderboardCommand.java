package com.github.kdaniel2410.commands;

import com.github.kdaniel2410.Constants;
import com.github.kdaniel2410.handlers.DatabaseHandler;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.util.logging.ExceptionLogger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LeaderboardCommand implements CommandExecutor {

    private static final Logger logger = LogManager.getLogger();
    private final DatabaseHandler databaseHandler;

    public LeaderboardCommand(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    @Command(aliases = {">leaderboard"}, privateMessages = false)
    public String onCommand(DiscordApi api, Server server, ServerTextChannel channel) {
        try {
            if (!databaseHandler.getServerMembers(server.getId()).next()) {
                return ":warning: No one has opted in on this server";
            }
            ResultSet resultSet = databaseHandler.getServerMembersSorted(server.getId(), "voiceMinutes");
            StringBuilder description = new StringBuilder();
            int i = 1;
            while (resultSet.next()) {
                int finalI = i;
                api.getUserById(resultSet.getLong("userId")).thenAccept(user -> {
                    try {
                        description.append(String.format("%s: %s with %s minutes\n", finalI, user.getName(), resultSet.getInt("voiceMinutes")));
                    } catch (SQLException e) {
                        logger.error(e);
                    }
                });
                i++;
            }
            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(Constants.EMBED_COLOR)
                    .setThumbnail(api.getYourself().getAvatar())
                    .setDescription(description.toString());
            channel.sendMessage(embed).exceptionally(ExceptionLogger.get());
        } catch (SQLException e) {
            logger.error(e);
        }
        return null;
    }
}
