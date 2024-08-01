package org.example.adminService;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import org.example.bot.BotConstant;
import org.example.entity.Bot;
import org.example.entity.TelegramUser;
import org.example.enums.TelegramState;
import org.example.userService.UserService;

public class AdminController {
    static String Start = "/start";

    public static void startAdmin(TelegramUser tgUser, Message message) {
        if (message.text().equals(Start)) {
            AdminService.acceptStartShowButtons(tgUser, message);
        } else if (tgUser.checkState(TelegramState.WAS_ANNOUNCED)) {
            if (message.text().equals(BotConstant.SEE_THE_VOTES)) {
                AdminService.SeeShowVotes(tgUser);
            } else if (message.text().equals(BotConstant.NEW_KONKURS)) {
                AdminService.newKonkurs(tgUser);
            } else if (message.text().equals(BotConstant.CREATOR) || message.text().equals("/dasturchi")) {
                AdminService.creator(tgUser);
            } else if (message.text().equals(BotConstant.STOPPED)) {
                AdminService.botStopped(tgUser);
            } else if (message.text().equals(BotConstant.STARTED)) {
                AdminService.botStarted(tgUser);
            } else if (message.text().equals("/admin")) {
                UserService.admin(tgUser);
            }
        } else if (tgUser.checkState(TelegramState.NEW_KONKURS)) {
            if (message.text().equals(BotConstant.ADD_A_SCHOOL)) {
                AdminService.enterSchoolName(tgUser);
            } else if (message.text().equals(BotConstant.BACK)) {
                AdminService.back(tgUser, message);
            } else if (message.text().equals(BotConstant.TEXT)) {
                AdminService.enterForText(tgUser);
            } else if (message.text().equals(BotConstant.IMMAGE_REPLACEMENT)) {
                AdminService.immageReplacement(tgUser);
            }
        } else if (tgUser.checkState(TelegramState.ENTER_SCHOOL_NAME)) {
            AdminService.enterTeacherName(tgUser, message);
        } else if (tgUser.checkState(TelegramState.ENTER_TEACHER_NAME)) {
            AdminService.checkAddToList(tgUser, message);
        } else if (tgUser.checkState(TelegramState.TEXT)) {
            AdminService.timeAcceptance(tgUser, message);
        }
    }

    public static void callbackQueryAdmin(TelegramUser tgUser, CallbackQuery callbackQuery) {
        if (tgUser.checkState(TelegramState.ADD_TO_LIST)) {
            AdminService.addToList(tgUser, callbackQuery);
        } else if (tgUser.checkState(TelegramState.ADD_TEXT_TO_LIST)) {
            AdminService.addTextToList(tgUser, callbackQuery);
        } else if (tgUser.checkState(TelegramState.ADD_PHOTO_TO_LIST)) {
            AdminService.addPhotoToList(tgUser, callbackQuery);
        } else if (tgUser.checkState(TelegramState.STOPPED_THE_BOT)) {
            AdminService.checkBotStopped(callbackQuery, tgUser);
        } else if (tgUser.checkState(TelegramState.STARTED_THE_BOT)) {
            AdminService.checkBotStarted(callbackQuery, tgUser);
        }
    }


    public static void contactAdmin(TelegramUser tgUser, Message message) {

    }

    public static void PhotoAdmin(TelegramUser tgUser, Message message) {
        if (tgUser.checkState(TelegramState.SEND_A_PHOTO)) {
            AdminService.sendPhoto(tgUser, message);
        }
    }
}
