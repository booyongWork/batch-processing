package com.example.batchprocessing;

import jakarta.annotation.PostConstruct;
import org.springframework.batch.core.BatchStatus;
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
//	@Scheduled(cron = "0 19 16 * * ?")
//	public void runBatchJob() throws Exception {
//		// 배치 작업 실행
//		JobLauncher jobLauncher = context.getBean(JobLauncher.class);
//		Job importUserJob = context.getBean("importUserJob", Job.class);
//		Job addressInsertJob = context.getBean("addressInsertJob", Job.class);
//
//		// importUserJob 실행
//		JobExecution importUserJobExecution = jobLauncher.run(importUserJob, new JobParametersBuilder().toJobParameters());
//
//		// addressInsertJob 실행
//		JobExecution addressInsertJobExecution = jobLauncher.run(addressInsertJob, new JobParametersBuilder().toJobParameters());
//
//		// importUserJob이 완료되었는지 확인하고 로그를 출력
//		if (importUserJobExecution.getStatus() == BatchStatus.COMPLETED) {
//			System.out.println("importUserJob 배치 작업이 완료되었습니다.");
//		} else {
//			System.out.println("importUserJob 배치 작업이 실패하였습니다.");
//		}
//
//		// addressInsertJob이 완료되었는지 확인하고 로그를 출력
//		if (addressInsertJobExecution.getStatus() == BatchStatus.COMPLETED) {
//			System.out.println("addressInsertJob 배치 작업이 완료되었습니다.");
//		} else {
//			System.out.println("addressInsertJob 배치 작업이 실패하였습니다.");
//		}
//
//	}
//}

//즉시 실행 로직
//================================================================================================================
@SpringBootApplication
public class BatchProcessingApplication {

	private final ConfigurableApplicationContext context;
	private final JobLauncher jobLauncher;
	private final Job importUserJob;
	private final Job addressInsertJob;

	@Autowired
	public BatchProcessingApplication(ConfigurableApplicationContext context, JobLauncher jobLauncher,
									  Job importUserJob, Job addressInsertJob) {
		this.context = context;
		this.jobLauncher = jobLauncher;
		this.importUserJob = importUserJob;
		this.addressInsertJob = addressInsertJob;
	}

	public static void main(String[] args) {
		SpringApplication.run(BatchProcessingApplication.class, args);
	}

	@PostConstruct
	public void runBatchJob() throws Exception {
		// importUserJob 실행
		JobExecution importUserJobExecution = jobLauncher.run(importUserJob, new JobParametersBuilder().toJobParameters());

		// addressInsertJob 실행
		JobExecution addressInsertJobExecution = jobLauncher.run(addressInsertJob, new JobParametersBuilder().toJobParameters());

		// importUserJob이 완료되었는지 확인하고 로그를 출력
		if (importUserJobExecution.getStatus() == BatchStatus.COMPLETED) {
			System.out.println("importUserJob 배치 작업이 완료되었습니다.");
		} else {
			System.out.println("importUserJob 배치 작업이 실패하였습니다.");
		}

		// addressInsertJob이 완료되었는지 확인하고 로그를 출력
		if (addressInsertJobExecution.getStatus() == BatchStatus.COMPLETED) {
			System.out.println("addressInsertJob 배치 작업이 완료되었습니다.");
		} else {
			System.out.println("addressInsertJob 배치 작업이 실패하였습니다.");
		}

	}
}
