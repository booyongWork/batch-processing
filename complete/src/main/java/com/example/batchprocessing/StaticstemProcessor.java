package com.example.batchprocessing;

import org.springframework.batch.core.JobInstance;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.JdbcTemplate;

public class StaticstemProcessor implements ItemProcessor<Person, Statics> {

    private final JdbcTemplate jdbcTemplate;

    public StaticstemProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Statics process(final Person person) {
        System.out.println("process");
        return null;
    }
}
