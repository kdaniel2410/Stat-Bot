package com.github.kdaniel2410;

import com.github.kdaniel2410.commands.LeaderboardCommand;
import com.github.kdaniel2410.commands.OptCommand;
import com.github.kdaniel2410.commands.StatsCommand;
import com.github.kdaniel2410.handlers.DatabaseHandler;
import com.github.kdaniel2410.listeners.*;
import de.btobastian.sdcf4j.CommandHandler;
import de.btobastian.sdcf4j.handler.JavacordHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.Nameable;
import org.javacord.api.entity.permission.Permissions;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        if ((System.getenv("token")) == null) {
            logger.error("Please provide the bot token as an environment variable (e.g export token=your-bot-token-here)");
            System.exit(-1);
        }

        DiscordApi api = new DiscordApiBuilder()
                .setToken(System.getenv("token"))
                .setAllIntents()
                .login()
                .join();

        logger.info("Logged in as {}", api.getYourself().getName());
        logger.info("Use the following link to invite me to your server {}", api.createBotInvite(Permissions.fromBitmask(18496)));
        logger.info("Serving {} server(s)", api.getServers().size());

        api.getServers().forEach(server -> logger.info("Loaded server {} owned by {} with {} members", server.getName(), server.getOwner().map(Nameable::getName).orElse("unknown user"), server.getMemberCount()));

        DatabaseHandler databaseHandler = new DatabaseHandler();
        api.addServerJoinListener(new ServerJoinListenerImpl());
        api.addServerLeaveListener(new ServerLeaveListenerImpl(databaseHandler));
        api.addServerMemberLeaveListener(new ServerMemberLeaveListenerImpl(databaseHandler));
        api.addServerMemberBanListener(new ServerMemberBanListenerImpl(databaseHandler));
        api.addMessageCreateListener(new MessageCreateListenerImpl(databaseHandler));
        api.addReactionAddListener(new ReactionAddListenerImpl(databaseHandler));
        CommandHandler commandHandler = new JavacordHandler(api);
        commandHandler.registerCommand(new OptCommand(databaseHandler));
        commandHandler.registerCommand(new StatsCommand(databaseHandler));
        commandHandler.registerCommand(new LeaderboardCommand(databaseHandler));

        Runnable runnable = () -> api.getServers().forEach(server -> server.getMembers().forEach(member -> {
            if (member.getConnectedVoiceChannel(server).isPresent()) {
                try {
                    databaseHandler.incrementField(member.getId(), server.getId(), "voiceMinutes");
                } catch (SQLException e) {
                    logger.error(e);
                }
            }

        }));

        api.getThreadPool().getScheduler().scheduleWithFixedDelay(runnable, 1, 1, TimeUnit.MINUTES);
    }
}
