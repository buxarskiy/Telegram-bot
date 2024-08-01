package org.example.userService;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Contact;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.GetChatMemberResponse;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.SneakyThrows;
import org.example.bot.BotConstant;
import org.example.bot.MyBot;
import org.example.entity.TelegramUser;
import org.example.entityService.PhotoService;
import org.example.entityService.UserVoteService;
import org.example.enums.RoleUser;
import org.example.enums.TelegramState;
import org.example.utils.ConnectionUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserService {
    private static final Long developChatId = 6042916920L;
    private static final Connection connection = ConnectionUtil.getConnection();

    public static void acceptStartAskContact(TelegramUser tgUser, Message message) {
        tgUser.clearDeletingMessages();
        String firstName = message.from().firstName();
        String lastName = message.from().lastName();
        tgUser.setFirstName(firstName);
        tgUser.setLastName(lastName);
        SendMessage sendMessage = new SendMessage(
                tgUser.getChatId(), """
                Ассалому алейкум ҳурматли %s
                Илтимос ботдан тўлиқ фойдаланиш учун телефон рақамингизни қолдиринг
                Телефон рақами қолдириш тугмасини босинг
                               
                """.formatted(tgUser.getFullName())
        );
        sendMessage.replyMarkup(UserUtils.generateContactButton());
        UserService.changeState(TelegramState.SHARE_CONTACT, tgUser);
        SendResponse res = MyBot.telegramBot.execute(sendMessage);
        tgUser.addToDeleting(res);
    }

    public static void acceptContactAskToSubscribe(TelegramUser tgUser, Message message) {
        Contact contact = message.contact();
        tgUser.setPhone(contact.phoneNumber());
        UserService.changeState(TelegramState.CHECH_SUBSCRIBES, tgUser);
        sendTgLink(tgUser);
    }

    private static void sendTgLink(TelegramUser tgUser) {
        tgUser.clearDeletingMessages();
        SendPhoto sendPhoto = new SendPhoto(tgUser.getChatId(), new File("files/photos/rasm.png"));
        sendPhoto.caption("""
                 Сўровномада қатнашиш учун қуйидаги каналларга обуна бўлинг, сўнгра ТЕКШИРИШ тугмасини босинг.
                """);
        sendPhoto.replyMarkup(new ReplyKeyboardRemove());
        sendPhoto.replyMarkup(UserUtils.generateLinkToSubscribe());
        SendResponse res = MyBot.telegramBot.execute(sendPhoto);
        tgUser.addToDeleting(res.message().messageId());
    }

    public static boolean checkJoinedTheGroup(TelegramUser tgUser) {
        boolean isMember1 = isUserMemberOfGroup1(tgUser);
        boolean isMember2 = isUserMemberOfGroup2(tgUser);
        if (isMember1 && isMember2) {
            return true;
        } else {
            SendResponse res = MyBot.telegramBot.execute(new SendMessage(tgUser.getChatId(),
                    "Siz barcha gruhlarga qo'shilmadingiz \uD83D\uDE0A. Iltimos gruhlarga qo'shilib keyin '' "
                            + BotConstant.TEKSHIRISH + " '' tugmasini bosing!"));
            tgUser.addToDeleting(res);
            return false;
        }
    }

    private static boolean isUserMemberOfGroup1(TelegramUser tgUser) {
        GetChatMember getChatMember = new GetChatMember(
                -1002000595315L,
                tgUser.getChatId()
        );
        GetChatMemberResponse resp = MyBot.telegramBot.execute(getChatMember);
        if (resp.chatMember().status().name().equals("member")) {
            return true;
        } else return resp.chatMember().status().name().equals("creator");
    }

    private static boolean isUserMemberOfGroup2(TelegramUser tgUser) {
        GetChatMember getChatMember = new GetChatMember(
                -1002003053036L,
                tgUser.getChatId()
        );
        GetChatMemberResponse resp = MyBot.telegramBot.execute(getChatMember);

        if (resp.chatMember().status().name().equals("member")) {
            return true;
        } else
            return resp.chatMember().status().name().equals("administrator") || resp.chatMember().status().name().equals("creator");
    }

    public static void acceptSubscriberAskVote(TelegramUser tgUser) {
        tgUser.clearDeletingMessages();
//        Photo photo = DB.PHOTOS.get(0);
        //ForText forText = DB.FOR_TEXTS.get(0);
        String photoPath = PhotoService.getPhotoPath();
        SendPhoto sendPhoto = getSendPhoto(tgUser, photoPath);
        sendPhoto.replyMarkup(UserUtils.generateVoteButtons());
        UserService.changeState(TelegramState.VOTE_USER, tgUser);
        SendResponse res = MyBot.telegramBot.execute(sendPhoto);
        tgUser.setSchoolMessageId(res.message().messageId());
        UserService.setSchoolMessageId(tgUser, res.message().messageId());
    }

    @NotNull
    private static SendPhoto getSendPhoto(TelegramUser tgUser, String photoPath) {
        SendPhoto sendPhoto = new SendPhoto(tgUser.getChatId(), new File(/*"files/photos/rasm1.png"*/photoPath));
        sendPhoto.caption(PhotoService.getText());

        /*sendPhoto.caption("""
                🎗 Hurmatli tumandoshlar!


                Vobkent tumani maktablarining o'tgan yil davomida faol va tashabbuskor bo'lgan o'qituvchisini aniqlash bo'yicha so'rovnomada ishtirok eting.

                Ovoz berish 20 - Avgust soat 22:00 gacha davom etadi.

                *Ovozlar sonini kompyuter orqali kuzatib boring!
                """);*/
        return sendPhoto;
    }


    public static void editMarkup() {
        List<TelegramUser> userList = getUserList();
        for (TelegramUser user : userList) {
            if (user.getSchoolMessageId() != null) {
                EditMessageReplyMarkup replyMarkup = new EditMessageReplyMarkup(
                        user.getChatId(),
                        user.getSchoolMessageId()
                );
                replyMarkup.replyMarkup(UserUtils.generateVoteButtons());
                MyBot.telegramBot.execute(replyMarkup);
            }
        }
//        DB.TG_USER.forEach((key, value) -> {
//            if (value.getSchoolMessageId() != null) {
//                EditMessageReplyMarkup replyMarkup = new EditMessageReplyMarkup(
//                        value.getChatId(),
//                        value.getSchoolMessageId()
//                );
//                replyMarkup.replyMarkup(UserUtils.generateVoteButtons());
//                MyBot.telegramBot.execute(replyMarkup);
//            }
//        });
    }

    @SneakyThrows
    public static List<TelegramUser> getUserList() {
        PreparedStatement statement = connection.prepareStatement("select * from telegram_user");
        ResultSet resultSet = statement.executeQuery();

        List<TelegramUser> users = new ArrayList<>();

        while (resultSet.next()) {
            TelegramUser user = new TelegramUser();
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
            user.setSchoolMessageId(resultSet.getInt("message_id"));
            user.setChatId(resultSet.getLong("chat_id"));
            users.add(user);
        }
        return users;
    }

    public static void addUserVote(TelegramUser tgUser, CallbackQuery callbackQuery) {

        if (!UserVoteService.isavAilable(tgUser)) {
            addCount(callbackQuery.data());
            UserVoteService.getByChatId(tgUser, callbackQuery);
        }
//        Optional<UsersVote> first = DB.VOTES.stream()
//                .filter((usersVote -> usersVote.getChatId().equals(tgUser.getChatId())))
//                .findFirst();
//        if (first.isPresent()) {
//            return;
//        }
//        UsersVote vote = UsersVote.builder()
//                .chatId(tgUser.getChatId())
//                .schoolId(UUID.fromString(callbackQuery.data()))
//                .build();
//        DB.VOTES.add(vote);
//        addCount(tgUser, callbackQuery.data());
    }

    @SneakyThrows
    private synchronized static void addCount(String data) {

        PreparedStatement statement = connection.prepareStatement("update school set count = count+1 where id = ?");
        statement.setObject(1, UUID.fromString(data));
        statement.executeUpdate();
    }

    public static void creator(TelegramUser tgUser) {
        SendMessage sendMessage = new SendMessage(
                tgUser.getChatId(),
                """
                         Assalomu Aleykum %s 🙋‍♂️
                         Sizga telegram bot 🤖 kerak bo'lsa
                         yoki dasturlashga oid savollar bo'lsa
                         men bilan bog'lanishingiz mumkin.
                         Telefon 📞: +998904148727 Jahongir 👨‍💻
                         Telegram: https://t.me/buxarskiy_joxa
                        """.formatted(tgUser.getFullName())
        );
        MyBot.telegramBot.execute(sendMessage);
    }

    public static void stopMessage(TelegramUser tgUser) {
        SendMessage sendMessage = new SendMessage(tgUser.getChatId(), "Bot faoliyatini to`xtatgan");
        MyBot.telegramBot.execute(sendMessage);
    }

    public static void admin(TelegramUser tgUser) {
        SendMessage sendMessage = new SendMessage(
                tgUser.getChatId(),
                """
                        Admin: Bexruz SHAROPOV
                        Telegram: https://t.me/Sharopov_officia1
                        """
        );
        MyBot.telegramBot.execute(sendMessage);

    }

    @SneakyThrows
    public static void changeState(TelegramState tegState, TelegramUser tgUser) {
        PreparedStatement statement = connection.prepareStatement("update telegram_user set state = ? where chat_id = ?");
        statement.setString(1, String.valueOf(tegState));
        statement.setLong(2, tgUser.getChatId());
        statement.executeUpdate();
    }

    @SneakyThrows
    public static void changeRole(RoleUser roleUser, Long chatId) {
        PreparedStatement statement = connection.prepareStatement("update telegram_user set role = ? where chat_id = ?");
        statement.setString(1, String.valueOf(roleUser));
        statement.setLong(2, chatId);
        statement.executeUpdate();
    }

    @SneakyThrows
    private static void setSchoolMessageId(TelegramUser tgUser, Integer messageId) {
        PreparedStatement statement = connection.prepareStatement("update telegram_user set message_id = ? where chat_id = ?");
        statement.setInt(1, messageId);
        statement.setLong(2, tgUser.getChatId());
        statement.executeUpdate();
    }
}