package com.example.batchprocessing;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StaticsPreparedStatementSetter implements ItemPreparedStatementSetter<StaticsDTO> {

    @Override
    public void setValues(StaticsDTO staticsDTO, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, staticsDTO.getJobNm());
        preparedStatement.setInt(2, staticsDTO.getTotalPeople());
        preparedStatement.setInt(3, staticsDTO.getMaleCount());
        preparedStatement.setInt(4, staticsDTO.getFemaleCount());
        preparedStatement.setInt(5, staticsDTO.getMarriedCount());
        preparedStatement.setInt(6, staticsDTO.getUnmarriedCount());
        preparedStatement.setDouble(7, staticsDTO.getTeenagePercentage());
        preparedStatement.setDouble(8, staticsDTO.getTwentiesPercentage());
        preparedStatement.setDouble(9, staticsDTO.getThirtiesPercentage());
        preparedStatement.setDouble(10, staticsDTO.getFortiesPercentage());
        preparedStatement.setDouble(11, staticsDTO.getFiftiesPercentage());
    }
}
