package com.github.kdaniel2410.commands;

import com.github.kdaniel2410.handlers.DatabaseHandler;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OptCommand implements CommandExecutor {

    private static final Logger logger = LogManager.getLogger();
    private final DatabaseHandler databaseHandler;

    public OptCommand(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    @Command(aliases = {">opt"}, usage = ">opt <in/out>", privateMessages = false)
    public String onCommand(String[] args, User user, Server server) {
        if (args.length != 1) {
            return ":warning: Not enough, or too many arguments provided";
        } else if (args[0].equalsIgnoreCase("out")) {
            try {
                ResultSet resultSet = databaseHandler.getServerMember(user.getId(), server.getId());
                if (!resultSet.next()) {
                    return ":warning: You are not opted in";
                }
                databaseHandler.removeServerMember(user.getId(), server.getId());
                return ":x: No longer tracking you stats, all saved values have been deleted";
            } catch (SQLException e) {
                logger.error(e);
                return ":warning: There was an error executing that command" +
                        "```" +
                        e +
                        "```";
            }
        } else if (args[0].equalsIgnoreCase("in")) {
            try {
                ResultSet resultSet = databaseHandler.getServerMember(user.getId(), server.getId());
                if (resultSet.next()) {
                    return ":warning: You are already opted in";
                }
                databaseHandler.addServerMember(user.getId(), server.getId());
                return ":white_check_mark: Now keeping a track of your stats";
            } catch (SQLException e) {
                logger.error(e);
                return ":warning: There was an error executing that command" +
                        "```" +
                        e +
                        "```";
            }
        } else {
            return ":warning: Invalid arguments";
        }
    }
}
