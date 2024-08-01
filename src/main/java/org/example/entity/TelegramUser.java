package org.example.entity;

import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.bot.MyBot;
import org.example.db.DB;
import org.example.enums.RoleUser;
import org.example.enums.TelegramState;
import org.example.myArrayList.CustomArraylist;
import org.example.userService.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TelegramUser {
    private UUID id;
    private Long chatId;
    private String firstName;
    private String lastName;
    private String phone;
    private String teacherName;
    private Integer schoolMessageId;
    private String schoolName;
    private String forText;
    private String photoPath;
    private RoleUser roleUser;
    private TelegramState telegramState;
    public static final CustomArraylist<Integer> deletingMessages = new CustomArraylist<>();

    public String getFullName() {
        if (firstName == null) {
            return lastName;
        } else if (lastName == null) {
            return firstName;
        } else {
            return firstName + " " + lastName;
        }
    }

    public boolean checkState(TelegramState telegramState) {
        return this.telegramState.equals(telegramState);
    }

    public void addToDeleting(SendResponse res) {
        System.out.println(res.message().text());
        addToDeleting(res.message().messageId());
    }

    public void addToDeleting(Integer messageId) {
        deletingMessages.add(messageId);
    }

    public void clearDeletingMessages() {

        MyBot.executorService.execute(() -> {
            for (Integer item : deletingMessages) {
                DeleteMessage deleteMessage = new DeleteMessage(
                        chatId,
                        item
                );
                MyBot.telegramBot.execute(deleteMessage);
            }
            deletingMessages.clear();
        });

        /*MyBot.executorService.execute(() -> {
            try {
                for (Integer message : deletingMessages) {
                    DeleteMessage deleteMessage = new DeleteMessage(
                            chatId,
                            message
                    );

                    MyBot.telegramBot.execute(deleteMessage);
                }
            } catch (Exception e) {

            }
            deletingMessages.clear();
        });*/
    }
}
