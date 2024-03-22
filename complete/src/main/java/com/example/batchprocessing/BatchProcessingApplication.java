package com.example.batchprocessing;

import jakarta.annotation.PostConstruct;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

//@SpringBootApplication
//@EnableScheduling // 스케줄러를 활성화
//public class BatchProcessingApplication {
//
//	private final ConfigurableApplicationContext context;
//
//	public BatchProcessingApplication(ConfigurableApplicationContext context) {
//		this.context = context;
//	}
//
//	//배치 완료 후 서버 종료
////	public static void main(String[] args) throws Exception {
////		ConfigurableApplicationContext context = SpringApplication.run(BatchProcessingApplication.class, args);
////		System.exit(SpringApplication.exit(context));
////	}
//
//	public static void main(String[] args) {
//		SpringApplication.run(BatchProcessingApplication.class, args);
//	}
//
//
//	@Scheduled(cron = "0 02 15 * * ?")
//	public void runBatchJob() throws Exception {
//		// 배치 작업 실행
//		JobLauncher jobLauncher = context.getBean(JobLauncher.class);
//		Job importUserJob = context.getBean("importUserJob", Job.class);
//		Job staticsInsertJob = context.getBean("staticsInsertJob", Job.class);
//
//		// importUserJob 실행
//		JobExecution importUserJobExecution = jobLauncher.run(importUserJob, new JobParametersBuilder().toJobParameters());
//
//		// staticsInsertJob 실행
//		JobExecution staticsInsertJobExecution = jobLauncher.run(staticsInsertJob, new JobParametersBuilder().toJobParameters());
//	}
//}

//즉시 실행 로직
//================================================================================================================
@SpringBootApplication
public class BatchProcessingApplication {

	private final ConfigurableApplicationContext context;
	private final JobLauncher jobLauncher;
	private final Job importUserJob;
	private final Job staticsInsertJob;

	@Autowired
	public BatchProcessingApplication(ConfigurableApplicationContext context, JobLauncher jobLauncher,
									  Job importUserJob, Job staticsInsertJob) {
		this.context = context;
		this.jobLauncher = jobLauncher;
		this.importUserJob = importUserJob;
		this.staticsInsertJob = staticsInsertJob;
	}

	public static void main(String[] args) {
		SpringApplication.run(BatchProcessingApplication.class, args);
	}

	@PostConstruct
	public void runBatchJob() throws Exception {
		// importUserJob 실행
		JobExecution importUserJobExecution = jobLauncher.run(importUserJob, new JobParametersBuilder().toJobParameters());

		// staticsInsertJob 실행
		JobExecution staticsInsertJobExecution = jobLauncher.run(staticsInsertJob, new JobParametersBuilder().toJobParameters());
	}
}
