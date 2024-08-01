package org.example;

import org.example.bot.MyBot;
import org.example.db.DB;
import org.example.entity.TelegramUser;
import org.example.myArrayList.CustomArraylist;

public class Main {

    public static void main(String[] args) {
        MyBot myBot = new MyBot();
        myBot.start();
    }
}