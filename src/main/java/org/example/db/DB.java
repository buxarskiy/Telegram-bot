package org.example.db;

import lombok.SneakyThrows;
import org.example.bot.BotConstant;
import org.example.entity.*;
import org.example.entityService.SchoolService;
import org.example.utils.ConnectionUtil;

import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//6042916920L  O'zim
//1062030820L Admin
public interface DB {
    ConcurrentHashMap<Long, TelegramUser> TG_USER = new ConcurrentHashMap<>();
    Vector<UsersVote> VOTES = new Vector<>();
    List<Bot> BOTS = new ArrayList<>();
    List<Photo> PHOTOS = new ArrayList<>();
    Connection connection = ConnectionUtil.getConnection();
    List<ForText> FOR_TEXTS = new ArrayList<>(/*List.of(
            ForText.builder()
                    .text("""
                            🎗 Hurmatli tumandoshlar!
                                            
                            Vobkent tumani maktablarining o'tgan yil davomida faol va tashabbuskor bo'lgan o'qituvchisini aniqlash bo'yicha so'rovnomada ishtirok eting.
                                            
                            Ovoz berish 25-Avgust soat 22:00 gacha davom etadi.
                                            
                            *Ovozlar sonini kompyuter orqali kuzatib boring!
                            """)
                    .build()*/
    );

    List<School> SCHOOLS = new ArrayList<>(List.of(

            School.builder()
                    .schoolName("1-maktab")
                    .teacherName("Eshmatov Eshmat")
                    .build(),
            School.builder()
                    .schoolName("2-maktab")
                    .teacherName("Toshmatov Toshmat")
                    .build(),
            School.builder()
                    .schoolName("3-maktab")
                    .teacherName("Hikmatov Hikmat")
                    .build(),
            School.builder()
                    .schoolName("4-maktab")
                    .teacherName("Nusratov Nusrat")
                    .build(),
            School.builder()
                    .schoolName("5-maktab")
                    .teacherName("Qudratov Qudrat")
                    .build(),
            School.builder()
                    .schoolName("6-maktab")
                    .teacherName("Eshmatova Eshmatoy")
                    .build(),
            School.builder()
                    .schoolName("7-maktab")
                    .teacherName("Toshmatova Toshmatoy")
                    .build(),
            School.builder()
                    .schoolName("8-maktab")
                    .teacherName("Hikmatova Hikmatoy")
                    .build(),
            School.builder()
                    .schoolName("9-maktab")
                    .teacherName("Nusratova Nusratoy")
                    .build(),
            School.builder()
                    .schoolName("10-maktab")
                    .teacherName("Qudratova Qudratoy")
                    .build()
    ));

    static boolean checkSchoolId(String data) {
        if (data.equals(BotConstant.TEKSHIRISH)) {
            return false;
        }
        List<School> schools = SchoolService.getSchools();

        Optional<School> first = schools.stream().filter(item -> item.getId().equals(UUID.fromString(data)))
                .findFirst();
        return first.isPresent();
//        Optional<School> first = DB.SCHOOLS.stream()
//                .filter(item -> item.getId().equals(UUID.fromString(data)))
//                .findFirst();
//        return first.isPresent();
    }

    @SneakyThrows
    static void generateData() {

//        PreparedStatement statement = connection.prepareStatement("select * from add_user(?,?,?,?)");
//        statement.setLong(1, 6042916920L);
//        statement.setString(2, "Jahongir");
//        statement.setString(3, "hjkl");
//        statement.setString(4, String.valueOf(RoleUser.ADMIN));
//        ResultSet resultSet = statement.executeQuery();
//        resultSet.next();

//        Long chatId = 6042916920L;
//        TelegramUser telegramUser = TelegramUser.builder()
//                .roleUser(RoleUser.ADMIN)
//                .chatId(chatId)
//                .telegramState(TelegramState.START)
//                .build();
//        TG_USER.put(chatId, telegramUser);
    }
}
