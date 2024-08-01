package org.example.userService;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import org.example.bot.BotConstant;
import org.example.bot.MyBot;
import org.example.db.DB;
import org.example.entity.Bot;
import org.example.entity.TelegramUser;
import org.example.enums.BotState;
import org.example.enums.TelegramState;

public class UserController {

    public static void startUser(TelegramUser tgUser, Message message) {
        if (message.text().equals("/start")) {
            tgUser.addToDeleting(message.messageId());
            if (getBotState()) {
                UserService.stopMessage(tgUser);
                return;
            }
            UserService.acceptStartAskContact(tgUser, message);
        } else if (message.text().equals("/dasturchi")) {
            UserService.creator(tgUser);
        } else if (message.text().equals("/admin")) {
            UserService.admin(tgUser);
        }
    }

    private static boolean getBotState() {
        for (Bot bot : DB.BOTS) {
            if (bot.getState().equals(BotState.STOP)) {
                return true;
            }
        }
        return false;
    }

    public static void CallbackQueryUser(TelegramUser tgUser, CallbackQuery callbackQuery) {
        if (tgUser.checkState(TelegramState.CHECH_SUBSCRIBES)) {
            if (UserService.checkJoinedTheGroup(tgUser)) {
                if (callbackQuery.data().equals(BotConstant.TEKSHIRISH)) {
                    UserService.acceptSubscriberAskVote(tgUser);
                }
            }
        } else if (tgUser.checkState(TelegramState.VOTE_USER)) {
            if (DB.checkSchoolId(callbackQuery.data())) {
                UserService.addUserVote(tgUser, callbackQuery);
                UserService.editMarkup();
                MyBot.telegramBot.execute(UserUtils.generateAnswerCallbackPopUp(tgUser, callbackQuery));
            }
        }
    }

    public static void contactUser(TelegramUser tgUser, Message message) {
        if (tgUser.checkState(TelegramState.SHARE_CONTACT)) {
            UserService.acceptContactAskToSubscribe(tgUser, message);
        }
    }

    public static void PhotoUser(TelegramUser tgUser, Message message) {


    }
}
