package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
	private String jobName; // Job 이름을 저장할 변수 추가
	private final JdbcTemplate jdbcTemplate;

	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	// importUserJob() 메서드에서 Job 이름을 받아와서 저장
//	public void setJobName(String jobName) {
//		this.jobName = jobName;
//	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		String jobName;
		// 배치 작업이 완료된 후 실행되는 메서드입니다.
		String currentJobName = jobExecution.getJobInstance().getJobName();

		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			LocalDateTime startBatchTime = LocalDateTime.now();
			if ("importTodayUserJob".equals(currentJobName)) {
				int todayUserCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM people WHERE join_date = ?", Integer.class, LocalDate.now());

				//오늘 가입자가 없을경우
				if (todayUserCount == 0) {
					System.out.println("importTodayUserJob에 오늘 날짜에 등록된 사용자가 없으므로 staticsInsertJob 배치 실패");
					System.out.println("importTodayUserJob에 오늘 날짜에 등록된 사용자가 없으므로 ageAddressInsertJob 배치 실패");
					//  작업을 실패로 처리
					jobExecution.setStatus(BatchStatus.FAILED);
					LocalDateTime batchFailedTime = LocalDateTime.now();
					insertBatchHistory("importTodayUserJob", batchFailedTime, batchFailedTime, "F", "오늘 등록된 사용자가 없습니다.");
					insertBatchHistory("staticsInsertJob", batchFailedTime, batchFailedTime, "F", "importTodayUserJob 배치가 실패했습니다.");
					insertBatchHistory("ageAddressInsertJob", batchFailedTime, batchFailedTime, "F", "importTodayUserJob 배치가 실패했습니다.");

					//오늘 가입자가 있는 경우
				}else if(todayUserCount > 0){
					String startTimeString = jobExecution.getStartTime().toString().replaceFirst("\\.\\d+$", "");
					String endTimeString = jobExecution.getEndTime().toString().replaceFirst("\\.\\d+$", "");

					// Parsing LocalDateTime
					LocalDateTime startTime = LocalDateTime.parse(startTimeString);
					LocalDateTime endTime = LocalDateTime.parse(endTimeString);

					insertBatchHistory("importTodayUserJob", startTime, endTime, "S", "");
					jobName = "staticsInsertJob";
					System.out.println("importTodayUserJob에 오늘 날짜 가입자가 존재하기 때문에 staticsInsertJob 배치 실행");
					try {
						int totalPeople = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM today_reg_users", Integer.class);
						int maleCount = jdbcTemplate.queryForObject("SELECT COUNT(gender) FROM today_reg_users WHERE gender = 'M'", Integer.class);
						int femaleCount = jdbcTemplate.queryForObject("SELECT COUNT(gender) FROM today_reg_users WHERE gender = 'F'", Integer.class);
						int marriedCount = jdbcTemplate.queryForObject("SELECT COUNT(married) FROM today_reg_users WHERE married = 1", Integer.class);
						int unmarriedCount = jdbcTemplate.queryForObject("SELECT COUNT(married) FROM today_reg_users WHERE married = 0", Integer.class);

						// 각 연령대별 인원의 백분율을 계산하는 쿼리
						// NOTE. 검색 속도로 풀스캔 X, CASE, BETWEEN 사용안함. age컬럼에 idx_age 만들어 놓음
						String teenagePercentageQuery = "SELECT ROUND((SUM(sub.teenage_count) / sub.total_count) * 100, 2) AS teenage_percentage " +
								"FROM ( " +
								"    SELECT " +
								"        COUNT(*) AS total_count, " +
								"        SUM(IF(age >= 10 AND age < 20, 1, 0)) AS teenage_count " +
								"    FROM today_reg_users " +
								") AS sub " +
								"GROUP BY sub.total_count;";

						String twentiesPercentageQuery = "SELECT ROUND((SUM(sub.teenage_count) / sub.total_count) * 100, 2) AS teenage_percentage " +
								"FROM ( " +
								"    SELECT " +
								"        COUNT(*) AS total_count, " +
								"        SUM(IF(age >= 20 AND age < 30, 1, 0)) AS teenage_count " +
								"    FROM today_reg_users " +
								") AS sub " +
								"GROUP BY sub.total_count;";

						String thirtiesPercentageQuery = "SELECT ROUND((SUM(sub.teenage_count) / sub.total_count) * 100, 2) AS teenage_percentage " +
								"FROM ( " +
								"    SELECT " +
								"        COUNT(*) AS total_count, " +
								"        SUM(IF(age >= 30 AND age < 40, 1, 0)) AS teenage_count " +
								"    FROM today_reg_users " +
								") AS sub " +
								"GROUP BY sub.total_count;";

						String fortiesPercentageQuery = "SELECT ROUND((SUM(sub.teenage_count) / sub.total_count) * 100, 2) AS teenage_percentage " +
								"FROM ( " +
								"    SELECT " +
								"        COUNT(*) AS total_count, " +
								"        SUM(IF(age >= 40 AND age < 50, 1, 0)) AS teenage_count " +
								"    FROM today_reg_users " +
								") AS sub " +
								"GROUP BY sub.total_count;";

						String fiftiesPercentageQuery = "SELECT ROUND((SUM(sub.teenage_count) / sub.total_count) * 100, 2) AS teenage_percentage " +
								"FROM ( " +
								"    SELECT " +
								"        COUNT(*) AS total_count, " +
								"        SUM(IF(age >= 50 AND age < 60, 1, 0)) AS teenage_count " +
								"    FROM today_reg_users " +
								") AS sub " +
								"GROUP BY sub.total_count;";

						// 각 연령대별 인원의 백분율을 조회하여 결과를 가져옴
						// NOTE. queryForObject - 데이터베이스에서 쿼리를 실행하여 단일 결과를 가져오는 데 사용
						double teenagePercentage = jdbcTemplate.queryForObject(teenagePercentageQuery, Double.class);
						double twentiesPercentage = jdbcTemplate.queryForObject(twentiesPercentageQuery, Double.class);
						double thirtiesPercentage = jdbcTemplate.queryForObject(thirtiesPercentageQuery, Double.class);
						double fortiesPercentage = jdbcTemplate.queryForObject(fortiesPercentageQuery, Double.class);
						double fiftiesPercentage = jdbcTemplate.queryForObject(fiftiesPercentageQuery, Double.class);

						LocalDateTime currentDateTime = LocalDateTime.now();
						Timestamp sqlTimestamp = Timestamp.valueOf(currentDateTime);

						// statics 테이블에 값 삽입
						jdbcTemplate.update("INSERT INTO statics (job_nm, total_people, male_count, female_count, married_count, unmarried_count, teenagePercentage, twentiesPercentage, thirtiesPercentage, fortiesPercentage, fiftiesPercentage, work_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
								jobName, totalPeople, maleCount, femaleCount, marriedCount, unmarriedCount, teenagePercentage, twentiesPercentage, thirtiesPercentage, fortiesPercentage, fiftiesPercentage, sqlTimestamp);

						LocalDateTime endBatchTime = LocalDateTime.now();

						insertBatchHistory("staticsInsertJob", startBatchTime, endBatchTime, "S", "");
					} catch (DataAccessException e) {
						System.out.println("staticsInsertJob 배치에 실패했습니다: " + e.getMessage());
						e.printStackTrace();
					}

					try {
						System.out.println("importTodayUserJob이 완료되었기에 ageAddressInsertJob 배치 실행");
						jobName = "ageAddressInsertJob";
						// CSV 파일에서 주소 정보를 추출하여 Address 객체를 생성하고 데이터베이스에 삽입
						List<Map<String, Object>> personAddresses = jdbcTemplate.queryForList("SELECT DISTINCT person_id FROM today_reg_users");

						for (Map<String, Object> personAddress : personAddresses) {
							long person_Id = (Long) personAddress.get("person_id");

							// 중복 여부 확인을 위한 쿼리 실행
							int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM address WHERE person_Id = ?", Integer.class, person_Id);

							// 중복된 person_id가 없는 경우에만 삽입 수행
							if (count == 0) {
								// 해당 person_id에 대한 주소 정보 가져오기
								Map<String, Object> addressMap = jdbcTemplate.queryForMap("SELECT address FROM today_reg_users WHERE person_id = ?", person_Id);
								String fullAddress = (String) addressMap.get("address");
								String[] addressParts = fullAddress.split(" "); // 예시: "서울특별시 강남구 역삼동 123번지"
								String city = addressParts[0].trim();
								String state = addressParts[1].trim();
								String street = addressParts[2].trim();

								// Address 객체 생성
								Address address = new Address(street, city, state);

								LocalDateTime currentDateTime = LocalDateTime.now();
								Timestamp sqlTimestamp = Timestamp.valueOf(currentDateTime);

								// Address 객체를 데이터베이스에 삽입
								jdbcTemplate.update("INSERT INTO address (street, city, state, person_Id, work_date) VALUES (?, ?, ?, ?, ?)",
										address.street(), address.city(), address.state(), person_Id, sqlTimestamp);
							}
						}

						List<Map<String, Object>> personAges = jdbcTemplate.queryForList("SELECT person_id, age FROM today_reg_users");

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

								LocalDateTime currentDateTime = LocalDateTime.now();
								Timestamp sqlTimestamp = Timestamp.valueOf(currentDateTime);

								// age 테이블에 해당 정보 삽입
								jdbcTemplate.update("INSERT INTO age (person_id, age, ageGroup, work_date) VALUES (?, ?, ?, ?)", personId, age, ageGroup, sqlTimestamp);

							}
						}
						LocalDateTime endBatchTime = LocalDateTime.now();
						System.out.println("ageAddressInsertJob 배치 작업이 완료되었습니다.");
						insertBatchHistory(jobName, startBatchTime, endBatchTime, "S", "");
					} catch (DataAccessException e) {
						System.out.println("ageAddressInsertJob 배치에 실패했습니다: " + e.getMessage());
						e.printStackTrace(); // 데이터베이스 접근 예외 처리
					}
				}
			}
		}
	}

	private void insertBatchHistory(String jobName, LocalDateTime startTime, LocalDateTime endTime, String processStatus, String errorMessage) {
		jdbcTemplate.update("INSERT INTO batchhst (job_nm, start_dt, end_dt, proc_sts, err_msg) VALUES (?, ?, ?, ?, ?)",
				jobName, startTime, endTime, processStatus, errorMessage);
	}
}
