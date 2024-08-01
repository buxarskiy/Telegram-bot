package org.example.entityService;

import com.pengrad.telegrambot.model.Message;
import lombok.SneakyThrows;
import org.example.entity.School;
import org.example.entity.TelegramUser;
import org.example.utils.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SchoolService {
    private static Connection connection = ConnectionUtil.getConnection();

    @SneakyThrows
    public static List<School> getSchools() {

        List<School> schools = new ArrayList<>();
        PreparedStatement statement = connection.prepareStatement("select * from school");
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            School school = new School();
            school.setId(resultSet.getObject("id", UUID.class));
            school.setSchoolName(resultSet.getString("school_name"));
            school.setVoteCount(resultSet.getInt("count"));
            school.setTeacherName(resultSet.getString("teacher_name"));
            schools.add(school);
        }
        return schools;
    }

    @SneakyThrows
    public static void addSchool(School newSchool) {
        PreparedStatement statement = connection.prepareStatement("insert into school(school_name,teacher_name) values (?,?)");
        statement.setString(1, newSchool.getSchoolName());
        statement.setString(2, newSchool.getTeacherName());
        statement.executeUpdate();
    }
}
