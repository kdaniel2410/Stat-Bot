package com.github.kdaniel2410.commands;

import com.github.kdaniel2410.Constants;
import com.github.kdaniel2410.handlers.DatabaseHandler;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.util.logging.ExceptionLogger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatsCommand implements CommandExecutor {

    private static final Logger logger = LogManager.getLogger();
    private final DatabaseHandler databaseHandler;

    public StatsCommand(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    @Command(aliases = {">stats"}, privateMessages = false)
    public String onCommand(User user, Server server, ServerTextChannel channel) {
        try {
            ResultSet resultSet = databaseHandler.getServerMember(user.getId(), server.getId());
            if (!resultSet.next()) {
                return ":warning: You have not opted in and therefore your stats are not being tracked";
            }
            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(Constants.EMBED_COLOR)
                    .setThumbnail(user.getAvatar())
                    .setDescription(String.format("Username: %s\n" +
                            "User ID: %d\n" +
                            "Voice Time: %d minutes\n" +
                            "Message: %d messages\n" +
                            "Reaction: %d reactions\n",
                            user.getName(),
                            resultSet.getLong("userId"),
                            resultSet.getInt("voiceMinutes"),
                            resultSet.getInt("messageCount"),
                            resultSet.getInt("reactionCount")));
            channel.sendMessage(embed).exceptionally(ExceptionLogger.get());
            return null;
        } catch (SQLException e) {
            logger.error(e);
            return ":warning: There was an error executing that command" +
                    "```" +
                    e +
                    "```";
        }
    }
}
