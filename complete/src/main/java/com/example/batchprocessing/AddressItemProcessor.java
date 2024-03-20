package com.example.batchprocessing;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.dao.EmptyResultDataAccessException;
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

        // Person 테이블에서 모든 사람의 personId와 address 값을 가져옵니다.
        List<Map<String, Object>> personAddresses = jdbcTemplate.queryForList("SELECT person_id, address FROM people");

        for (Map<String, Object> personAddr : personAddresses) {
            // person_id와 address 값을 추출합니다.
            long personId = (Long) personAddr.get("person_id");
            String address = (String) personAddr.get("address");

            // address를 쉼표(,)를 기준으로 나누어 각각의 값을 추출합니다.
            String[] addressParts = address.split(" ");
            String street = addressParts[0].trim();
            String city = addressParts[1].trim();
            String state = addressParts[2].trim();

            // 각 주소에 대한 Address 객체를 생성하여 반환합니다.
            Address addressObject = new Address(street, city, state, personId);
            // 만든 주소 객체를 반환합니다.
            return addressObject;
        }

        // 만약에 처리할 데이터가 없다면 null을 반환합니다.
        return null;
    }
}
