package org.example.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import lombok.SneakyThrows;
import org.example.adminService.AdminController;
import org.example.entity.TelegramUser;
import org.example.enums.RoleUser;
import org.example.enums.TelegramState;
import org.example.userService.UserController;
import org.example.userService.UserService;
import org.example.utils.ConnectionUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyBot {
    //6042916920
    static String token = "7164794547:AAF8Wf9iN85FW58dm8GPV1Y6ltdYYVeqnn0";
    public static final TelegramBot telegramBot = new TelegramBot(token);
    public static final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static final Connection connection = ConnectionUtil.getConnection();
    private static Logger logger = null;

    @SneakyThrows
    public static void log(String logText) {
        Path path = Path.of("ExceptionLog.txt");
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        if (logger == null) {
            FileHandler handler = new FileHandler(path.toString());
            handler.setFormatter(new SimpleFormatter());
            logger = Logger.getLogger("ExceptionLogger");
            logger.addHandler(handler);
        }
        logger.info(logText);
    }

    public void start() {
        telegramBot.setUpdatesListener((updates) -> {
            for (Update update : updates) {
                try {
                    handleUpdate(update);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, Throwable::printStackTrace);
    }


    private void handleUpdate(Update update) {
        if (update.message() != null) {
            Message message = update.message();
            Long chatId = message.chat().id();
            TelegramUser tgUser = getUser(chatId, message);
            if (message.text() != null) {
                try {
                    //checkIdForAdmin(tgUser, chatId);
                    if (tgUser.getRoleUser().equals(RoleUser.ADMIN)) {
                        AdminController.startAdmin(tgUser, message);
                    } else {
                        UserController.startUser(tgUser, message);
                    }
                } catch (Exception e) {
                    log(e + "\nMyBot.java \nQator:92");
                }
            } else if (message.contact() != null) {
                try {
                    if (tgUser.getRoleUser().equals(RoleUser.ADMIN)) {
                        AdminController.contactAdmin(tgUser, message);
                    } else {
                        UserController.contactUser(tgUser, message);
                    }
                } catch (Exception e) {
                    log(e + "\nMyBot.java \nQator:102");
                }

            } else if (message.photo() != null) {
                try {
                    if (tgUser.getRoleUser().equals(RoleUser.ADMIN)) {
                        AdminController.PhotoAdmin(tgUser, message);
                    } else {
                        UserController.PhotoUser(tgUser, message);
                    }
                } catch (Exception e) {
                    log(e + "\nMyBot.java \nQator:113");
                }
            }
        } else if (update.callbackQuery() != null) {
            CallbackQuery callbackQuery = update.callbackQuery();
            Long chatId = callbackQuery.from().id();
            TelegramUser tgUser = getUser(chatId, callbackQuery.message());
            try {
                if (tgUser.getRoleUser().equals(RoleUser.ADMIN)) {
                    AdminController.callbackQueryAdmin(tgUser, callbackQuery);
                } else {
                    UserController.CallbackQueryUser(tgUser, callbackQuery);
                }
            } catch (Exception e) {
                log(e + "\nMyBot.java \nQator:128");
            }
        }
    }

    private void checkIdForAdmin(TelegramUser tgUser, Long chatId) {
        if (chatId == 6042916920L) {
            //tgUser.setRoleUser(RoleUser.ADMIN);
            UserService.changeRole(RoleUser.ADMIN, chatId);
        }
    }

    @SneakyThrows
    public static TelegramUser getUser(Long chatId, Message message) {
        PreparedStatement statement = connection.prepareStatement("select * from get_user_by_chat_id(?)");
        statement.setLong(1, chatId);
        ResultSet resultSet = statement.executeQuery();
        TelegramUser user = new TelegramUser();

        while (resultSet.next()) {
            user.setId(resultSet.getObject("id", UUID.class));
            user.setPhotoPath(resultSet.getString("photo_path"));
            user.setRoleUser(RoleUser.valueOf(resultSet.getString("role")));
            user.setForText(resultSet.getString("for_text"));
            user.setTelegramState(TelegramState.valueOf(resultSet.getString("state")));
            user.setPhone(resultSet.getString("phone_number"));
            user.setFirstName(resultSet.getString("first_name"));
            user.setLastName(resultSet.getString("last_name"));
            user.setTeacherName(resultSet.getString("teacher_name"));
            user.setSchoolName(resultSet.getString("school_name"));
            user.setSchoolMessageId(resultSet.getInt("school_message_id"));
            user.setChatId(resultSet.getLong("chat_id"));
            return user;
        }
        return addUser(chatId, message);
//        TelegramUser tgUser = DB.TG_USER.getOrDefault(chatId, null);
//        if (tgUser != null) {
//            return tgUser;
//        } else {
//            TelegramUser newUser = TelegramUser.builder()
//                    .chatId(chatId)
//                    .roleUser(RoleUser.USER)
//                    .telegramState(TelegramState.START)
//                    .build();
//            DB.TG_USER.put(chatId, newUser);
//            return newUser;
//
    }

//    @SneakyThrows
//    private TelegramUser getUser(Long chatId, CallbackQuery callbackQuery) {
//
//        PreparedStatement statement = connection.prepareStatement("select * from get_user_by_chat_id(?)");
//
//        statement.setLong(1, chatId);
//        ResultSet resultSet = statement.executeQuery();
//
//        TelegramUser user = new TelegramUser();
//
//        while (resultSet.next()) {
//            user.setId(resultSet.getObject("id", UUID.class));
//            user.setPhotoPath(resultSet.getString("photo_path"));
//            user.setRoleUser(RoleUser.valueOf(resultSet.getString("role")));
//            user.setForText(resultSet.getString("for_text"));
//            user.setTelegramState(TelegramState.valueOf(resultSet.getString("state")));
//            user.setPhone(resultSet.getString("phone_number"));
//            user.setFirstName(resultSet.getString("first_name"));
//            user.setLastName(resultSet.getString("last_name"));
//            user.setTeacherName(resultSet.getString("teacher_name"));
//            user.setSchoolName(resultSet.getString("school_name"));
//            user.setSchoolMessageId(resultSet.getInt("school_message_id"));
//            user.setChatId(resultSet.getLong("chat_id"));
//
//            return user;
//        }
//
//
//        return addUser(chatId, callbackQuery);
//    }

    @SneakyThrows
    private static TelegramUser addUser(Long chatId, Message message) {

        TelegramUser user = TelegramUser.builder()
                .firstName(message.from().firstName())
                .lastName(message.from().lastName())
                .chatId(chatId).build();

        PreparedStatement statement = connection.prepareStatement("select * from add_user(?,?,?)");
        statement.setLong(1, chatId);
        statement.setString(2, message.from().firstName());
        statement.setString(3, message.from().lastName());
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();

        return user;
    }

//    @SneakyThrows
//    private TelegramUser addUser(Long chatId, CallbackQuery callbackQuery) {
//
//        TelegramUser user = TelegramUser.builder()
//                .firstName(callbackQuery.from().firstName())
//                .lastName(callbackQuery.from().lastName())
//                .roleUser(RoleUser.USER)
//                .chatId(chatId).build();
//
//        PreparedStatement statement = connection.prepareStatement("select * from add_user(?,?,?)");
//        statement.setLong(1, chatId);
//        statement.setString(2, callbackQuery.from().firstName());
//        statement.setString(3, callbackQuery.from().lastName());
//        ResultSet resultSet = statement.executeQuery();
//        resultSet.next();
//
//        return user;
//    }
}
