package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor implements ItemProcessor<PersonDTO, PersonDTO> {

	private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

	@Override
	public PersonDTO process(final PersonDTO person) {
//		System.out.println("첫번째 배치 process 진행중");
		final String firstName = person.getFirstName().toUpperCase();
		final String lastName = person.getLastName().toUpperCase();

		// 새로운 Person 객체를 생성할 때는 record 클래스의 생성자를 사용합니다.
//		final PersonDTO transformedPerson = new PersonDTO(person.getFirstName(), person.getLastName(), person.getGender(), person.isMarried(), person.getAge(), person.getAddress());

//		log.info("대문자변환중 (" + person + ") -> (" + transformedPerson + ")");

		return null;
	}

}
