package com.example.batchprocessing;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TodayRegisteredUserPreparedStatementSetter implements ItemPreparedStatementSetter<TodayRegisteredUserDTO> {

    @Override
    public void setValues(TodayRegisteredUserDTO todayRegisteredUserDTO, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, todayRegisteredUserDTO.getFirstName());
        preparedStatement.setString(2, todayRegisteredUserDTO.getLastName());
        preparedStatement.setString(3, todayRegisteredUserDTO.getGender());
        preparedStatement.setBoolean(4, todayRegisteredUserDTO.isMarried());
        preparedStatement.setInt(5, todayRegisteredUserDTO.getAge());
        preparedStatement.setString(6, todayRegisteredUserDTO.getAddress());
        preparedStatement.setLong(7, todayRegisteredUserDTO.getPersonId());

        // LocalDate를 java.sql.Date로 변환
        Date joinDate = Date.valueOf(todayRegisteredUserDTO.getJoinDate());
        preparedStatement.setDate(8, joinDate);

        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);
        preparedStatement.setTimestamp(9, timestamp);
    }
}