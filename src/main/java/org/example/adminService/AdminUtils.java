package org.example.adminService;

import com.pengrad.telegrambot.model.request.*;
import org.example.bot.BotConstant;
import org.example.entity.Bot;

public class AdminUtils {

    public static Keyboard generateButtons() {
        return new ReplyKeyboardMarkup(new KeyboardButton(BotConstant.SEE_THE_VOTES))
                .addRow(new KeyboardButton(BotConstant.NEW_KONKURS))
                .addRow(new KeyboardButton(BotConstant.CREATOR))
                .addRow(new KeyboardButton(BotConstant.STOPPED), new KeyboardButton(BotConstant.STARTED))
                .resizeKeyboard(true);
    }

    public static InlineKeyboardMarkup addToList() {
        InlineKeyboardMarkup button = new InlineKeyboardMarkup();
        return button.addRow(new InlineKeyboardButton(BotConstant.HA).callbackData(BotConstant.HA), new InlineKeyboardButton(BotConstant.YOQ).callbackData(BotConstant.YOQ));
    }

    public static InlineKeyboardMarkup generateButtonStopped() {
        InlineKeyboardMarkup button = new InlineKeyboardMarkup();
        return button.addRow(new InlineKeyboardButton(BotConstant.I_AGREE).callbackData(BotConstant.I_AGREE), new InlineKeyboardButton(BotConstant.NO).callbackData(BotConstant.NO));
    }

    public static Keyboard generateShowKonkursButtons() {
        return new ReplyKeyboardMarkup(new KeyboardButton(BotConstant.ADD_A_SCHOOL)).addRow(new KeyboardButton(BotConstant.IMMAGE_REPLACEMENT)).addRow(new KeyboardButton(BotConstant.TEXT)).addRow(new KeyboardButton(BotConstant.BACK)).resizeKeyboard(true);
    }

    //image replacement

    public static InlineKeyboardMarkup writeTheText() {
        InlineKeyboardMarkup button = new InlineKeyboardMarkup();
        return button.addRow(new InlineKeyboardButton(BotConstant.HA).callbackData(BotConstant.HA), new InlineKeyboardButton(BotConstant.YOQ).callbackData(BotConstant.YOQ));
    }

    public static InlineKeyboardMarkup seePhoto() {
        InlineKeyboardMarkup button = new InlineKeyboardMarkup();
        return button.addRow(new InlineKeyboardButton(BotConstant.HA).callbackData(BotConstant.HA), new InlineKeyboardButton(BotConstant.YOQ).callbackData(BotConstant.YOQ));
    }

    public static InlineKeyboardMarkup generateButtonStarted() {
        InlineKeyboardMarkup button = new InlineKeyboardMarkup();
        return button.addRow(new InlineKeyboardButton(BotConstant.I_AGREE).callbackData(BotConstant.I_AGREE), new InlineKeyboardButton(BotConstant.NO).callbackData(BotConstant.NO));
    }
}
