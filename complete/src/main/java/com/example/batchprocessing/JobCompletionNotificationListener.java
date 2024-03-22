package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.dao.DataAccessException;
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
	private String jobName;

	//NOTE. JdbcTemplate - 은 스프링 프레임워크에서 제공하는 JDBC 추상화 계층
	// 이를 사용하면 JDBC를 사용하여 데이터베이스에 액세스하는 데 필요한 일반적인 작업을 더 간단하게 처리
	// SQL 쿼리 실행, SQL 쿼리 실행 시 파라미터 전달, SQL 결과 처리, 예외 처리 및 트랜잭션 관리
	private final JdbcTemplate jdbcTemplate;
	private final BatchLogger batchLogger;

	// importUserJob() 메서드에서 Job 이름을 받아와서 저장
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate, BatchLogger batchLogger) {
		this.jdbcTemplate = jdbcTemplate;
		this.batchLogger = batchLogger;
	}


	@Override
	public void afterJob(JobExecution jobExecution) {
		// 배치 작업이 완료된 후 실행되는 메서드입니다.
		String currentJobName = jobExecution.getJobInstance().getJobName();

		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			if ("importUserJob".equals(currentJobName)) {
				System.out.println("첫번째 배치 process 진행중");
				try {
					// CSV 파일에서 주소 정보를 추출하여 Address 객체를 생성하고 데이터베이스에 삽입
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
					System.out.println("importUserJob 배치 작업이 완료되었습니다.");
					} catch (DataAccessException e) {
						System.out.println("importUserJob 배치에 실패했습니다: " + e.getMessage());
						e.printStackTrace(); // 데이터베이스 접근 예외 처리
					}
//				batchLogger.logBatchCompletion();
			}

			else if("staticsInsertJob".equals(currentJobName)){
				try {
					System.out.println("두번째 배치 process 진행중");
					int totalPeople = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM people", Integer.class);
					int maleCount = jdbcTemplate.queryForObject("SELECT COUNT(gender) FROM people WHERE gender = 'M'", Integer.class);
					int femaleCount = jdbcTemplate.queryForObject("SELECT COUNT(gender) FROM people WHERE gender = 'F'", Integer.class);
					int marriedCount = jdbcTemplate.queryForObject("SELECT COUNT(married) FROM people WHERE married = 1", Integer.class);
					int unmarriedCount = jdbcTemplate.queryForObject("SELECT COUNT(married) FROM people WHERE married = 0", Integer.class);

					// 각 연령대별 인원의 백분율을 계산하는 쿼리
					// NOTE. 검색 속도로 풀스캔 X, CASE, BETWEEN 사용안함. age컬럼에 idx_age 만들어 놓음
					//전체 수에서 나누기 10대 수 한 후 에 100곱해서 퍼센트로 쿼리 조회
					String teenagePercentageQuery = "SELECT ROUND(((sub.teenage_count) / sub.total_count) * 100, 2) AS teenage_percentage " +
							"FROM ( " +
							// 10이상이면서 20미만 경우 1 최종합계를 구함
							"    SELECT " +
							"        COUNT(*) AS total_count, " +
							"        SUM(IF(age >= 10 AND age < 20, 1, 0)) AS teenage_count " +
							"    FROM people " +
							") AS sub " +
							"GROUP BY sub.total_count;";

					String twentiesPercentageQuery = "SELECT ROUND(((sub.teenage_count) / sub.total_count) * 100, 2) AS teenage_percentage " +
							"FROM ( " +
							"    SELECT " +
							"        COUNT(*) AS total_count, " +
							"        SUM(IF(age >= 20 AND age < 30, 1, 0)) AS teenage_count " +
							"    FROM people " +
							") AS sub " +
							"GROUP BY sub.total_count;";

					String thirtiesPercentageQuery = "SELECT ROUND(((sub.teenage_count) / sub.total_count) * 100, 2) AS teenage_percentage " +
							"FROM ( " +
							"    SELECT " +
							"        COUNT(*) AS total_count, " +
							"        SUM(IF(age >= 30 AND age < 40, 1, 0)) AS teenage_count " +
							"    FROM people " +
							") AS sub " +
							"GROUP BY sub.total_count;";

					String fortiesPercentageQuery = "SELECT ROUND(((sub.teenage_count) / sub.total_count) * 100, 2) AS teenage_percentage " +
							"FROM ( " +
							"    SELECT " +
							"        COUNT(*) AS total_count, " +
							"        SUM(IF(age >= 40 AND age < 50, 1, 0)) AS teenage_count " +
							"    FROM people " +
							") AS sub " +
							"GROUP BY sub.total_count;";

					String fiftiesPercentageQuery = "SELECT ROUND(((sub.teenage_count) / sub.total_count) * 100, 2) AS teenage_percentage " +
							"FROM ( " +
							"    SELECT " +
							"        COUNT(*) AS total_count, " +
							"        SUM(IF(age >= 50 AND age < 60, 1, 0)) AS teenage_count " +
							"    FROM people " +
							") AS sub " +
							"GROUP BY sub.total_count;";

					// 각 연령대별 인원의 백분율을 조회하여 결과를 가져옴
					// NOTE. queryForObject - 데이터베이스에서 쿼리를 실행하여 단일 결과를 가져오는 데 사용
					double teenagePercentage = jdbcTemplate.queryForObject(teenagePercentageQuery, Double.class);
					double twentiesPercentage = jdbcTemplate.queryForObject(twentiesPercentageQuery, Double.class);
					double thirtiesPercentage = jdbcTemplate.queryForObject(thirtiesPercentageQuery, Double.class);
					double fortiesPercentage = jdbcTemplate.queryForObject(fortiesPercentageQuery, Double.class);
					double fiftiesPercentage = jdbcTemplate.queryForObject(fiftiesPercentageQuery, Double.class);

					// statics 테이블에 값 삽입
					jdbcTemplate.update("INSERT INTO statics (job_nm, total_people, male_count, female_count, married_count, unmarried_count, teenagePercentage, twentiesPercentage, thirtiesPercentage, fortiesPercentage, fiftiesPercentage) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
							jobName, totalPeople, maleCount, femaleCount, marriedCount, unmarriedCount, teenagePercentage, twentiesPercentage, thirtiesPercentage, fortiesPercentage, fiftiesPercentage);

					System.out.println("staticsInsertJob 배치 작업이 완료되었습니다.");
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
				} catch (DataAccessException e) {
					System.out.println("staticsInsertJob 배치에 실패했습니다: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}else{
			if ("importUserJob".equals(currentJobName)) {
				System.out.println("importUserJob 배치에 실패했습니다");
			}else if("staticsInsertJob".equals(currentJobName)){
				System.out.println("staticsInsertJob 배치에 실패했습니다");
				jdbcTemplate.update("UPDATE batch_job_execution SET STATUS = 'FAILED' WHERE JOB_EXECUTION_ID = ?", jobExecution.getId());
			}
		}
	}
}
