package org.example.entityService;

import lombok.SneakyThrows;
import org.example.entity.Photo;
import org.example.utils.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PhotoService {
    private static Connection connection = ConnectionUtil.getConnection();

    @SneakyThrows
    public static void savePhoto(Photo photo) {
        System.out.println(photo.getPhoto());
        PreparedStatement statement = connection.prepareStatement("insert into photo values (?)");
        statement.setString(1, photo.getPhoto());
        statement.executeUpdate();
    }

    @SneakyThrows
    public static String getPhotoPath() {
        PreparedStatement statement = connection.prepareStatement("select * from photo LIMIT 1");
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();

        return resultSet.getString("path");
    }

    @SneakyThrows
    public static void addText(String text) {
        PreparedStatement statement = connection.prepareStatement("update photo set text = ? where id = ?");
        statement.setString(1, text);
        statement.setInt(2, 1);
        statement.executeUpdate();
    }

    @SneakyThrows
    public static String getText() {
        PreparedStatement statement = connection.prepareStatement("select text from photo where id = ?");
        statement.setInt(1, 1);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getString("text");
    }
}
