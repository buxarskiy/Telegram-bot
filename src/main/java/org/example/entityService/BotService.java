package org.example.entityService;

import lombok.SneakyThrows;
import org.example.entity.Bot;
import org.example.enums.BotState;
import org.example.utils.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BotService {
    private static Connection connection = ConnectionUtil.getConnection();

    @SneakyThrows
    public static void stopBot(Bot bot) {
        PreparedStatement statement = connection.prepareStatement("insert into bot values (?)");
        statement.setString(1, String.valueOf(bot.getState()));
        statement.executeUpdate();
    }

    @SneakyThrows
    public static void startBot() {
        PreparedStatement statement = connection.prepareStatement("update bot set bot_state = ? where bot_state = ?");
        statement.setString(1, String.valueOf(BotState.START));
        statement.setString(2, String.valueOf(BotState.STOP));
        statement.executeUpdate();
    }

    @SneakyThrows
    public static String getState() {
        PreparedStatement statement = connection.prepareStatement("select bot_state from bot where bot_state = ?");
        statement.setString(1, "STOP");
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();

        return resultSet.getString("bot_state");
    }
}
