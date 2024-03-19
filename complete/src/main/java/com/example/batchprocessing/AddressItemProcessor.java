package com.example.batchprocessing;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

public class AddressItemProcessor implements ItemProcessor<Person, Address> {
    private final JdbcTemplate jdbcTemplate;

    public AddressItemProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Address process(final Person person) {
        System.out.println("두번째 배치 Address Insert process 진행중");

        // Address 정보 추출
        String fullAddress = person.address();
        String[] addressParts = fullAddress.split("\\s+", 3);
        String city = addressParts[0].trim();
        String state = addressParts[1].trim();
        String street = addressParts[2].trim();

        // Person의 person_id 가져오기
        long personId = 0;
        List<Map<String, Object>> personAddresses = jdbcTemplate.queryForList("SELECT person_id FROM people WHERE first_name = ? AND last_name = ?",
                new Object[]{person.firstName(), person.lastName()});

        if (!personAddresses.isEmpty()) {
            personId = (Long) personAddresses.get(0).get("person_id111");
        }

        // Address 객체 생성 및 반환
        return new Address(street, city, state, personId);
    }
}
