package com.example.batchprocessing;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.JdbcTemplate;

public class StaticstemProcessor implements ItemProcessor<Person, Statics> {

    private final JdbcTemplate jdbcTemplate;

    public StaticstemProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Statics process(final Person person) {
//        System.out.println("process");
        //TODO.Statics 에러 테스트
//        if (!person.firstName().equals("John")) {
//            throw new RuntimeException("에러 발생: 이름이 John입니다.");
//        }
        return null;
    }
}
