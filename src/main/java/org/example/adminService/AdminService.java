package org.example.adminService;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.GetFileResponse;
import lombok.SneakyThrows;
import org.example.bot.BotConstant;
import org.example.bot.MyBot;
import org.example.db.DB;
import org.example.entity.*;
import org.example.enums.BotState;
import org.example.enums.TelegramState;
import org.example.entityService.BotService;
import org.example.entityService.PhotoService;
import org.example.entityService.SchoolService;
import org.example.userService.UserService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringJoiner;

public class AdminService {
    private static String school_name;
    private static String teacher_name;
    private static String photoPath;
    private static String text;


    public static void acceptStartShowButtons(TelegramUser tgUser, Message message) {
        String firstName = message.from().firstName();
        String lastName = message.from().lastName();
        tgUser.setFirstName(firstName);
        tgUser.setLastName(lastName);
        SendMessage sendMessage = new SendMessage(
                tgUser.getChatId(),
                "Assalomu Aleykum admin %s janoblari. ".formatted(tgUser.getFullName())
        );
        sendMessage.replyMarkup(AdminUtils.generateButtons());
        UserService.changeState(TelegramState.WAS_ANNOUNCED, tgUser);
        MyBot.telegramBot.execute(sendMessage);
    }

    public static void SeeShowVotes(TelegramUser tgUser) {
        String messageOfVotes = showVotesInfo();
        SendMessage sendMessage = new SendMessage(
                tgUser.getChatId(),
                messageOfVotes
        );
        UserService.changeState(TelegramState.WAS_ANNOUNCED, tgUser);
        MyBot.telegramBot.execute(sendMessage);
    }

    private static String showVotesInfo() {
        StringJoiner stringJoiner = new StringJoiner("\n\uD83C\uDFEB ", "\uD83C\uDFEB ", "");
        for (School school : SchoolService.getSchools()) {
            stringJoiner.add("%s  \uD83D\uDC68\u200D\uD83C\uDFEB  %s  %d  \uD83D\uDCC8 ".formatted(school.getSchoolName(), school.getTeacherName(), school.getVoteCount()));
        }
        return stringJoiner.toString();
    }

    public static void newKonkurs(TelegramUser tgUser) {
        SendMessage sendMessage = new SendMessage(
                tgUser.getChatId(),
                "Yangi konkurs boshlashingiz uchun barcha Ma'lumotlarni pastdagi tugmalar orqali shu yerda kiritib olishingiz mumkin \uD83D\uDE07"
        );
        sendMessage.replyMarkup(AdminUtils.generateShowKonkursButtons());
        UserService.changeState(TelegramState.NEW_KONKURS, tgUser);
        MyBot.telegramBot.execute(sendMessage);
    }

    public static void enterSchoolName(TelegramUser tgUser) {
        SendMessage sendMessage = new SendMessage(
                tgUser.getChatId(),
                """
                        Maktab raqamini kiriting...
                                                
                        [Masalan: 1-maktab]
                        """
        );
        UserService.changeState(TelegramState.ENTER_SCHOOL_NAME, tgUser);
        MyBot.telegramBot.execute(sendMessage);

    }

    public static void enterTeacherName(TelegramUser tgUser, Message message) {
        school_name = message.text();
        SendMessage sendMessage = new SendMessage(
                tgUser.getChatId(),
                """
                        O'qituvchi ismi va familiyasini kiriting...
                                                
                        [masalan: Eshmat Eshmatov]
                        """
        );
        MyBot.telegramBot.execute(sendMessage);
        UserService.changeState(TelegramState.ENTER_TEACHER_NAME, tgUser);
    }

    public static void checkAddToList(TelegramUser tgUser, Message message) {
        teacher_name = message.text();
        SendMessage sendMessage = new SendMessage(
                tgUser.getChatId(),
                showSchoolNameAndTeacherName().toString()
        );
        sendMessage.replyMarkup(AdminUtils.addToList());
        UserService.changeState(TelegramState.ADD_TO_LIST, tgUser);
        MyBot.telegramBot.execute(sendMessage);
    }

    private static StringBuilder showSchoolNameAndTeacherName() {
        StringBuilder str = new StringBuilder();
        str.append(school_name).append(" ").append(teacher_name).append("\n Ma'lumotni  qo'shmoqchimisiz ❓");
        return str;
    }

    public static void creator(TelegramUser tgUser) {
        SendMessage sendMessage = new SendMessage(
                tgUser.getChatId(),
                """
                         Assalomu Aleykum %s 🙋‍
                         Sizga telegram bot 🤖 kerak bo'lsa
                         yoki dasturlashga oid savollar bo'lsa
                         men bilan bog'lanishingiz mumkin.
                         Telefon 📞: +998904148727 Jahongir 👨‍💻
                         Telegram: https://t.me/buxarskiy_joxa
                        """.formatted(tgUser.getFullName())
        );
        MyBot.telegramBot.execute(sendMessage);
    }

    public static void botStopped(TelegramUser tgUser) {
        SendMessage sendMessage = new SendMessage(
                tgUser.getChatId(), "Botni ishdan to'xtatishga rozimisiz ❓"
        );
        sendMessage.replyMarkup(AdminUtils.generateButtonStopped());
        UserService.changeState(TelegramState.STOPPED_THE_BOT, tgUser);
        MyBot.telegramBot.execute(sendMessage);
    }

    public static void botStarted(TelegramUser tgUser) {
        SendMessage sendMessage = new SendMessage(
                tgUser.getChatId(), "Botni ishga tushirishga rozimisiz ❓"
        );
        sendMessage.replyMarkup(AdminUtils.generateButtonStarted());
        tgUser.setTelegramState(TelegramState.STARTED_THE_BOT);
        UserService.changeState(TelegramState.STARTED_THE_BOT, tgUser);
        MyBot.telegramBot.execute(sendMessage);
    }

    public static void addToList(TelegramUser tgUser, CallbackQuery callbackQuery) {
        if (callbackQuery.data().equals(BotConstant.HA)) {
            School newSchool = School.builder()
                    .schoolName(school_name)
                    .teacherName(teacher_name)
                    .build();
            SchoolService.addSchool(newSchool);
            SendMessage sendMessage = new SendMessage(tgUser.getChatId(), "Ma'lumot qo'shildi ✅");
            MyBot.telegramBot.execute(sendMessage);
        } else if (callbackQuery.data().equals(BotConstant.YOQ)) {
            SendMessage sendMessage = new SendMessage(tgUser.getChatId(), "Ma'lumot qo'shilmadi ❌");
            MyBot.telegramBot.execute(sendMessage);
        }
        UserService.changeState(TelegramState.NEW_KONKURS, tgUser);
    }

    public static void checkBotStopped(CallbackQuery callbackQuery, TelegramUser tgUser) {
        if (callbackQuery.data().equals(BotConstant.I_AGREE)) {
            Bot bot = Bot.builder().state(BotState.STOP).build();
            BotService.stopBot(bot);
            SeeShowVotes(tgUser);
            SendMessage sendMessage = new SendMessage(tgUser.getChatId(), "Bot o'z faoliyatini to'xtatdi ✅");
            clearTg_User(tgUser);
            DB.VOTES.clear();
            DB.FOR_TEXTS.clear();
            DB.PHOTOS.clear();
            DB.SCHOOLS.clear();
            MyBot.telegramBot.execute(sendMessage);
        } else if (callbackQuery.data().equals(BotConstant.NO)) {
            SendMessage sendMessage = new SendMessage(tgUser.getChatId(), "Bot o'z faoliyatini to'xtatmadi \uD83D\uDEAB");
            MyBot.telegramBot.execute(sendMessage);
        }
        tgUser.setTelegramState(TelegramState.WAS_ANNOUNCED);
        UserService.changeState(TelegramState.WAS_ANNOUNCED, tgUser);
    }

    private static void clearTg_User(TelegramUser tgUser) {
        if (tgUser.getChatId().equals(1062030820L)) {
            DB.TG_USER.clear();
        }
    }


    public static void back(TelegramUser tgUser, Message message) {

        SendMessage sendMessage = new SendMessage(tgUser.getChatId(), "Menu");
        sendMessage.replyMarkup(AdminUtils.generateButtons());
        UserService.changeState(TelegramState.WAS_ANNOUNCED, tgUser);
        MyBot.telegramBot.execute(sendMessage);
    }

    public static void enterForText(TelegramUser tgUser) {
        SendPhoto sendPhoto = new SendPhoto(tgUser.getChatId(), new File("files/photos/rasm3.png"));
        sendPhoto.caption(""" 
                ‼️Eslatma:  So'rovnoma maqsadi va so'rovnoma tugash vaqtini kiritish esdan chiqmasin.
                                
                Quyidagi rasmda ko'rsatilganidek so'rovnoma vaqtida ovoz beriladigan tugmalarni tepasida chiqadigan textni kiritasiz...
                               
                """);
        MyBot.telegramBot.execute(sendPhoto);
        UserService.changeState(TelegramState.TEXT, tgUser);
    }

    public static void timeAcceptance(TelegramUser tgUser, Message message) {
        text = message.text();
//        String forText = message.text();
//        if (!forText.isEmpty()) {
//            DB.FOR_TEXTS.clear();
//        }
//        tgUser.setForText(forText);
        SendMessage sendMessage = new SendMessage(
                tgUser.getChatId(),
                text + "\n\n❕Iltimos yozgan textingizni o'qib chiqing xatolik bo'lsa yo'q tugmachasini bosing!!!  \n Textni qo'shishga rozimisiz ❓"
        );
        sendMessage.replyMarkup(AdminUtils.writeTheText());
        UserService.changeState(TelegramState.ADD_TEXT_TO_LIST, tgUser);
        MyBot.telegramBot.execute(sendMessage);
    }

    public static void addTextToList(TelegramUser tgUser, CallbackQuery callbackQuery) {
        if (callbackQuery.data().equals(BotConstant.HA)) {
            PhotoService.addText(text);
            SendMessage sendMessage = new SendMessage(tgUser.getChatId(), "Ma'lumot qo'shildi ✅");
            MyBot.telegramBot.execute(sendMessage);
        } else if (callbackQuery.data().equals(BotConstant.YOQ)) {
            SendMessage sendMessage = new SendMessage(tgUser.getChatId(), "Ma'lumot qo'shilmadi ❌");
            MyBot.telegramBot.execute(sendMessage);
        }
        UserService.changeState(TelegramState.NEW_KONKURS, tgUser);
    }

    public static void immageReplacement(TelegramUser tgUser) {
        SendPhoto sendPhoto = new SendPhoto
                (tgUser.getChatId(),
                        new File("files/photos/rasm4.png"));
        sendPhoto.caption("""
                Quyidagi rasmga qarang...
                Xozir siz yuboradigan rasmingiz so'rovnomani shu qismida joylanadi.
                                
                ‼️Eslatma:  Iltimos e'tiborli bo'ling keyin rasmni almashtirib bo'lmaydi.
                """);
        UserService.changeState(TelegramState.SEND_A_PHOTO, tgUser);
        MyBot.telegramBot.execute(sendPhoto);
    }

    @SneakyThrows
    public static void sendPhoto(TelegramUser tgUser, Message message) {
        PhotoSize[] photo = message.photo();
        PhotoSize photoSize = photo[photo.length - 1];
        GetFile getFile = new GetFile(photoSize.fileId());
        GetFileResponse response = MyBot.telegramBot.execute(getFile);
        com.pengrad.telegrambot.model.File file = response.file();
        byte[] fileContent = MyBot.telegramBot.getFileContent(file);
        Path path = Path.of("files/photos/%s.png".formatted(tgUser.getChatId().toString()));
        if (Files.exists(path)) {
            Files.delete(path);
        }
        Files.createFile(path);
        Files.write(path, fileContent);
        photoPath = String.valueOf(path);
        SendPhoto sendPhoto = new SendPhoto(tgUser.getChatId(), new File(photoPath));
        sendPhoto.caption("Ma'lumot qabul qilinsinmi ❓");
        sendPhoto.replyMarkup(AdminUtils.seePhoto());
        MyBot.telegramBot.execute(sendPhoto);
        UserService.changeState(TelegramState.ADD_PHOTO_TO_LIST, tgUser);
    }

    public static void addPhotoToList(TelegramUser tgUser, CallbackQuery callbackQuery) {
        if (callbackQuery.data().equals(BotConstant.HA)) {
            Photo build = Photo.builder().photo(photoPath).build();
            PhotoService.savePhoto(build);
            SendMessage sendMessage = new SendMessage(tgUser.getChatId(), "Rasm qabul qilindi ✅");
            MyBot.telegramBot.execute(sendMessage);
        } else if (callbackQuery.data().equals(BotConstant.YOQ)) {
            SendMessage sendMessage = new SendMessage(tgUser.getChatId(), "Rasm qabul qilinmadi \uD83D\uDEAB");
            MyBot.telegramBot.execute(sendMessage);
        }
        UserService.changeState(TelegramState.NEW_KONKURS, tgUser);
    }


    public static void checkBotStarted(CallbackQuery callbackQuery, TelegramUser tgUser) {
        if (callbackQuery.data().equals(BotConstant.I_AGREE)) {
//            for (Bot bot : DB.BOTS) {
//                if (bot.getState().equals(BotState.STOP)) {
//                    bot.setState(BotState.START);
//                }
//            }
            String stopState = BotService.getState();
            if (stopState.equals(BotState.STOP.name())) {
                BotService.startBot();
                SendMessage sendMessage = new SendMessage(tgUser.getChatId(), "Bot o'z faoliyatini boshladi ✅");
                MyBot.telegramBot.execute(sendMessage);
            }
        } else if (callbackQuery.data().equals(BotConstant.NO)) {
            SendMessage sendMessage = new SendMessage(tgUser.getChatId(), "Bot o'z faoliyatini boshlamadi \uD83D\uDEAB");
            MyBot.telegramBot.execute(sendMessage);
        }
        UserService.changeState(TelegramState.WAS_ANNOUNCED, tgUser);
    }
}

