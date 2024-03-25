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
	private String jobName; // Job 이름을 저장할 변수 추가

	//NOTE. JdbcTemplate - 은 스프링 프레임워크에서 제공하는 JDBC 추상화 계층
	// 이를 사용하면 JDBC를 사용하여 데이터베이스에 액세스하는 데 필요한 일반적인 작업을 더 간단하게 처리
	// SQL 쿼리 실행, SQL 쿼리 실행 시 파라미터 전달, SQL 결과 처리, 예외 처리 및 트랜잭션 관리
	private final JdbcTemplate jdbcTemplate;

	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	// importUserJob() 메서드에서 Job 이름을 받아와서 저장
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		System.out.println("afterJob 실행");

	}
}
