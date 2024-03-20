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
	private final BatchLogger batchLogger;

	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate, BatchLogger batchLogger) {
		this.jdbcTemplate = jdbcTemplate;
		this.batchLogger = batchLogger;
	}


	@Override
	public void afterJob(JobExecution jobExecution) {
		System.out.println("afterJob 실행");

		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {

			// 배치 작업이 완료된 후 실행되는 메서드입니다.
			String currentJobName = jobExecution.getJobInstance().getJobName();
			if ("importUserJob".equals(currentJobName)) {

				// CSV 파일에서 주소 정보를 추출하여 Address 객체를 생성하고 데이터베이스에 삽입합니다.
				List<Map<String, Object>> personAddresses = jdbcTemplate.queryForList("SELECT DISTINCT person_id FROM people");

				for (Map<String, Object> personAddress : personAddresses) {
					long person_Id = (Long) personAddress.get("person_id");

					// 중복 여부 확인을 위한 쿼리 실행
					int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM address WHERE person_Id = ?", Integer.class, person_Id);

					// 중복된 person_id가 없는 경우에만 삽입 수행
					if (count == 0) {
						// 해당 person_id에 대한 주소 정보 가져오기
						Map<String, Object> addressMap = jdbcTemplate.queryForMap("SELECT address FROM people WHERE person_id = ?", person_Id);
						String fullAddress = (String) addressMap.get("address");
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
				}


				List<Map<String, Object>> personAges = jdbcTemplate.queryForList("SELECT person_id, age FROM people");

				for (Map<String, Object> personAge : personAges) {
					long personId = (Long) personAge.get("person_id");
					int age = (Integer) personAge.get("age");
					// 중복 여부 확인을 위한 쿼리 실행
					int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM age WHERE person_id = ?", Integer.class, personId);

					// personId가 중복되지 않은 경우에만 삽입 수행
					if (count == 0) {
						// 나이 그룹 계산
						String ageGroup;
						if (age < 20) {
							ageGroup = "Teens";
						} else if (age < 30) {
							ageGroup = "Twenties";
						} else if (age < 40) {
							ageGroup = "Thirties";
						} else if (age < 50) {
							ageGroup = "Forties";
						} else if (age < 60) {
							ageGroup = "Fifties";
						} else {
							ageGroup = "Sixties";
						}
						// age 테이블에 해당 정보 삽입
						jdbcTemplate.update("INSERT INTO age (person_id, age, ageGroup) VALUES (?, ?, ?)", personId, age, ageGroup);
					}
				}
				batchLogger.logBatchCompletion();
			}
		}
	}
}
