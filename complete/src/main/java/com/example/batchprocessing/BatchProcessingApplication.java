package com.example.batchprocessing;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling // 스케줄러를 활성화
public class BatchProcessingApplication {

	private final ConfigurableApplicationContext context;

	public BatchProcessingApplication(ConfigurableApplicationContext context) {
		this.context = context;
	}

	//배치 완료 후 서버 종료
//	public static void main(String[] args) throws Exception {
//		ConfigurableApplicationContext context = SpringApplication.run(BatchProcessingApplication.class, args);
//		System.exit(SpringApplication.exit(context));
//	}

	public static void main(String[] args) {
		SpringApplication.run(BatchProcessingApplication.class, args);
	}


	@Scheduled(cron = "0 52 14 * * ?")
	public void runBatchJob() throws Exception {
		// 배치 작업 실행
		JobLauncher jobLauncher = context.getBean(JobLauncher.class);
		Job job = context.getBean("importUserJob", Job.class);
		jobLauncher.run(job, new JobParametersBuilder().toJobParameters());
	}
}
