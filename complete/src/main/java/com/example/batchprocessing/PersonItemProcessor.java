package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

	private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

	@Override
	public Person process(final Person person) {
		System.out.println("첫번째 배치 process 진행중");
		final String firstName = person.firstName().toUpperCase();
		final String lastName = person.lastName().toUpperCase();

		// 새로운 Person 객체를 생성할 때는 record 클래스의 생성자를 사용합니다.
		final Person transformedPerson = new Person(firstName, lastName, person.gender(), person.married(), person.age(), person.address());

//		log.info("대문자변환중 (" + person + ") -> (" + transformedPerson + ")");

		return transformedPerson;
	}

}
