package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

//NOTE.
// 작업이 완료되면 피드백을 제공하기 위해 JobCompletionNotificationListener 작성
// afterJob 메서드는 Spring Batch의 JobExecutionListener 인터페이스의 일부다.
// 이 메서드는 배치 작업이 완료된 후에 호출
// Spring Batch는 배치 작업의 생명주기 이벤트에 따라 이러한 리스너 메서드를 호출하여 사용자가 필요한 작업을 수행
@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	//NOTE. JdbcTemplate - 은 스프링 프레임워크에서 제공하는 JDBC 추상화 계층
	// 이를 사용하면 JDBC를 사용하여 데이터베이스에 액세스하는 데 필요한 일반적인 작업을 더 간단하게 처리
	// SQL 쿼리 실행, SQL 쿼리 실행 시 파라미터 전달, SQL 결과 처리, 예외 처리 및 트랜잭션 관리
	private final JdbcTemplate jdbcTemplate;

	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		System.out.println("afterJob 실행");
/* NOTE.TEST로 Insert 성공 했는지 확인했던 select 문 */
// DataClassRowMapper는 각 행의 열을 Person 객체의 필드에 매핑. 따라서 데이터베이스에서 가져온 결과 집합의 각 행이 Person 객체로 변환

//		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
//			log.info("!!! JOB 완료! 결과확인");
//
//			jdbcTemplate
//					.query("SELECT first_name, last_name, gender, married, age FROM people", new DataClassRowMapper<>(Person.class))
//					.forEach(person -> log.info("데이터베이스에서 <{{}}> 찾았습니다.", person));
//		}

		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			// 배치 작업이 완료된 후 실행되는 메서드입니다.


			// CSV 파일에서 주소 정보를 추출하여 Address 객체를 생성하고 데이터베이스에 삽입합니다.
			List<Map<String, Object>> personAddresses = jdbcTemplate.queryForList("SELECT person_id,address FROM people");

			for (Map<String, Object> personAddress : personAddresses) {
				long person_Id = (Long) personAddress.get("person_id");
				String fullAddress = (String) personAddress.get("address");
				String[] addressParts = fullAddress.split(" "); // 예시: "서울특별시 강남구 역삼동 123번지"
				String city = addressParts[0].trim();
				String state = addressParts[1].trim();
				String street = addressParts[2].trim();

				// Address 객체 생성
				Address address = new Address(street, city, state);

				// Address 객체를 데이터베이스에 삽입
				jdbcTemplate.update("INSERT INTO address (street, city, state, person_Id) VALUES (?, ?, ?, ?)",
						address.street(), address.city(), address.state(), person_Id);
			}

			int totalPeople = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM people", Integer.class);
			int maleCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM people WHERE gender = 'M'", Integer.class);
			int femaleCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM people WHERE gender = 'F'", Integer.class);
			int marriedCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM people WHERE married = 1", Integer.class);
			int unmarriedCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM people WHERE married = 0", Integer.class);

			// 각 연령대별 인원의 백분율을 계산하는 쿼리
			String teenagePercentageQuery = "SELECT ROUND((COUNT(CASE WHEN age BETWEEN 10 AND 19 THEN 1 END) / COUNT(*)) * 100, 2) AS teenage_percentage FROM people";
			String twentiesPercentageQuery = "SELECT ROUND((COUNT(CASE WHEN age BETWEEN 20 AND 29 THEN 1 END) / COUNT(*)) * 100, 2) AS twenties_percentage FROM people";
			String thirtiesPercentageQuery = "SELECT ROUND((COUNT(CASE WHEN age BETWEEN 30 AND 39 THEN 1 END) / COUNT(*)) * 100, 2) AS thirties_percentage FROM people";
			String fortiesPercentageQuery = "SELECT ROUND((COUNT(CASE WHEN age BETWEEN 40 AND 49 THEN 1 END) / COUNT(*)) * 100, 2) AS forties_percentage FROM people";
			String fiftiesPercentageQuery = "SELECT ROUND((COUNT(CASE WHEN age BETWEEN 50 AND 59 THEN 1 END) / COUNT(*)) * 100, 2) AS fifties_percentage FROM people";

			// 각 연령대별 인원의 백분율을 조회하여 결과를 가져옴
			// NOTE. queryForObject - 데이터베이스에서 쿼리를 실행하여 단일 결과를 가져오는 데 사용
			double teenagePercentage = jdbcTemplate.queryForObject(teenagePercentageQuery, Double.class);
			double twentiesPercentage = jdbcTemplate.queryForObject(twentiesPercentageQuery, Double.class);
			double thirtiesPercentage = jdbcTemplate.queryForObject(thirtiesPercentageQuery, Double.class);
			double fortiesPercentage = jdbcTemplate.queryForObject(fortiesPercentageQuery, Double.class);
			double fiftiesPercentage = jdbcTemplate.queryForObject(fiftiesPercentageQuery, Double.class);

			log.info("=======통계==============================");
			log.info("전체 인원 수: {}", totalPeople);
			log.info("남성 수: {}", maleCount);
			log.info("여성 수: {}", femaleCount);
			log.info("결혼한 사람 수: {}", marriedCount);
			log.info("미혼인 사람 수: {}", unmarriedCount);

			log.info("10대 인원 퍼센트: {}%", teenagePercentage);
			log.info("20대 인원 퍼센트: {}%", twentiesPercentage);
			log.info("30대 인원 퍼센트: {}%", thirtiesPercentage);
			log.info("40대 인원 퍼센트: {}%", fortiesPercentage);
			log.info("50대 인원 퍼센트: {}%", fiftiesPercentage);
			log.info("=======================================");
		}

		}
}
