package com.github.kdaniel2410.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class DatabaseHandler {

    private static final Logger logger = LogManager.getLogger();
    private Connection connection;

    public DatabaseHandler() {
        try {
            String statement = "create table if not exists stats" +
                    "(userId bigint not null, " +
                    "serverId bigint not null, " +
                    "voiceMinutes int default 0, " +
                    "messageCount int default 0, " +
                    "reactionCount int default 0)";
            connection = DriverManager.getConnection("jdbc:h2:./stats");
            connection.createStatement().executeUpdate(statement);
        } catch (SQLException e) {
            logger.error(e);
        }
    }

    public void addServerMember(long userId, long serverId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("insert into stats (userId, serverId) values (?, ?)");
        statement.setLong(1, userId);
        statement.setLong(2, serverId);
        statement.executeUpdate();
    }

    public void removeServerMember(long userId, long serverId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("delete from stats where userId = ? and serverId = ?");
        statement.setLong(1, userId);
        statement.setLong(2, serverId);
        statement.executeUpdate();
    }

    public void removeServer(long serverId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("delete from stats where serverId = ?");
        statement.setLong(1, serverId);
        statement.executeUpdate();
    }

    public ResultSet getServerMember(long userId, long serverId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("select * from stats where userId = ? and serverId = ?");
        statement.setLong(1, userId);
        statement.setLong(2, serverId);
        return statement.executeQuery();
    }

    public ResultSet getServerMembers(long serverId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("select * from stats where serverId = ?");
        statement.setLong(1, serverId);
        return statement.executeQuery();
    }

    public ResultSet getServerMembersSorted(long serverId, String sort) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(String.format("select * from stats where serverId = ? order by %s desc", sort));
        statement.setLong(1, serverId);
        return statement.executeQuery();
    }

    public void incrementField(long userId, long serverId, String field) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(String.format("update stats set %s = %s + 1 where userId = ? and serverId = ?", field, field));
        statement.setLong(1, userId);
        statement.setLong(2, serverId);
        statement.executeUpdate();
    }
}
