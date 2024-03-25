package com.example.batchprocessing;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TodayRegisteredUserPreparedStatementSetter implements ItemPreparedStatementSetter<TodayRegisteredUser> {

    @Override
    public void setValues(TodayRegisteredUser todayRegisteredUser, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, todayRegisteredUser.getFirstName());
        preparedStatement.setString(2, todayRegisteredUser.getLastName());
        preparedStatement.setString(3, todayRegisteredUser.getGender());
        preparedStatement.setBoolean(4, todayRegisteredUser.isMarried());
        preparedStatement.setInt(5, todayRegisteredUser.getAge());
        preparedStatement.setString(6, todayRegisteredUser.getAddress());
    }
}