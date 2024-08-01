package org.example.userService;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import org.example.bot.BotConstant;
import org.example.entity.School;
import org.example.entity.TelegramUser;
import org.example.entity.UsersVote;
import org.example.entityService.SchoolService;
import org.example.entityService.UserVoteService;

import java.util.List;

public class UserUtils {
    public static Keyboard generateContactButton() {
        KeyboardButton keyboardButton = new KeyboardButton("Kontakt qoldirish☎\uFE0F");
        keyboardButton.requestContact(true);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardButton);
        replyKeyboardMarkup.resizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public static Keyboard generateLinkToSubscribe() {
        InlineKeyboardButton button = new InlineKeyboardButton("VOBKENT.UZ | Rasmiy kanali");
        InlineKeyboardButton button1 = new InlineKeyboardButton("VOBKENTLIKLAR");
        button.url(BotConstant.VOBKENT_UZ_LINK);
        button1.url(BotConstant.TEST_GROUP_LINK);
        return new InlineKeyboardMarkup(button.callbackData("url"))
                .addRow(button1.callbackData("url"))
                .addRow(new InlineKeyboardButton(BotConstant.TEKSHIRISH).callbackData(BotConstant.TEKSHIRISH));
    }

    public static InlineKeyboardMarkup generateVoteButtons() {
        List<School> schools = SchoolService.getSchools();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (School school : schools) {
            markup.addRow(new InlineKeyboardButton(school.getSchoolName() + " " + school.getTeacherName() +
                    " " + school.getVoteCount() + " ")
                    .callbackData(String.valueOf(school.getId())));
        }
        return markup;

//        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
//        for (School school : DB.SCHOOLS) {
//            markup.addRow(new InlineKeyboardButton(school.getSchoolName() + " " + school.getTeacherName() +
//                    " " + school.getVoteCount() + " ")
//                    .callbackData(String.valueOf(school.getId())));
//        }
//        return markup;
    }

    public static AnswerCallbackQuery generateAnswerCallbackPopUp(TelegramUser tgUser, CallbackQuery callbackQuery) {
        StringBuilder str = new StringBuilder();
        SchoolService.getSchools().forEach(
                school -> {
                    UsersVote vote = UserVoteService.getByChatId(tgUser, callbackQuery);
                    if (vote.getChatId().equals(tgUser.getChatId()) && school.getId().equals(vote.getSchoolId())) {
                        str.append("Siz ").append(school.getSchoolName()).append(" ").append(school.getTeacherName()).append(" ga ovoz berdingiz");
                    }
                }
        );
//        DB.SCHOOLS.forEach((school -> {
//            DB.VOTES.stream().filter(usersVote -> {
//                if (usersVote.getChatId().equals(tgUser.getChatId()) && school.getId().equals(usersVote.getSchoolId())) {
//                    str.append("Siz ").append(school.getSchoolName()).append(" ").append(school.getTeacherName()).append(" ga ovoz berdingiz");
//                }
//                return true;
//            }).toList();
//        }));

        AnswerCallbackQuery query = new AnswerCallbackQuery(
                callbackQuery.id()
        );
        query.showAlert(true);
        query.text(str.toString());
        return query;
    }
}
