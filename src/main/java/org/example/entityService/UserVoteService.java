package org.example.entityService;

import com.pengrad.telegrambot.model.CallbackQuery;
import lombok.SneakyThrows;
import org.example.entity.TelegramUser;
import org.example.entity.UsersVote;
import org.example.utils.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserVoteService {
    private static Connection connection = ConnectionUtil.getConnection();

    @SneakyThrows
    public static UsersVote getByChatId(TelegramUser tgUser, CallbackQuery callbackQuery) {
        PreparedStatement statement = connection.prepareStatement("select * from user_vote where user_chat_id = ? and id = ?");
        statement.setLong(1, tgUser.getChatId());
        statement.setObject(2, tgUser.getId());
        ResultSet resultSet = statement.executeQuery();

        UsersVote vote = new UsersVote();

        while (resultSet.next()) {
            vote.setId(resultSet.getObject("id", UUID.class));
            vote.setVoteUserName(resultSet.getString("user_vote_name"));
            vote.setChatId(resultSet.getLong("user_chat_id"));
            vote.setPhoneNumber(resultSet.getString("vote_phone_number"));
            vote.setSchoolId(resultSet.getObject("school_id", UUID.class));
            vote.setStartTime(resultSet.getObject("created_date", LocalDateTime.class));
            return vote;
        }
        return addUserVote(tgUser, callbackQuery);
    }

    @SneakyThrows
    public static UsersVote addUserVote(TelegramUser tgUser, CallbackQuery callbackQuery) {
        UsersVote usersVote = UsersVote.builder()
                .id(tgUser.getId())
                .chatId(tgUser.getChatId())
                .voteUserName(tgUser.getFullName())
                .phoneNumber(tgUser.getPhone())
                .schoolId(UUID.fromString(callbackQuery.data()))
                .build();

        PreparedStatement statement = connection.prepareStatement("select * from add_vote_user(?,?,?,?,?)");
        statement.setObject(1, tgUser.getId());
        statement.setLong(2, tgUser.getChatId());
        statement.setString(3, tgUser.getFullName());
        statement.setString(4, tgUser.getPhone());
        statement.setObject(5, UUID.fromString(callbackQuery.data()));
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();

        return usersVote;
    }

    @SneakyThrows
    public static boolean isavAilable(TelegramUser userVote) {

        PreparedStatement statement = connection.prepareStatement("select * from user_exists(?)");
        statement.setLong(1, userVote.getChatId());
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();

        return resultSet.getBoolean("user_exists");
    }
}
