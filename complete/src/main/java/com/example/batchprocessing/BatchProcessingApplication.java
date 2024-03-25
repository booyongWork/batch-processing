package com.example.batchprocessing;

import jakarta.annotation.PostConstruct;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
public class BatchProcessingApplication {

	private final ConfigurableApplicationContext context;

	public BatchProcessingApplication(ConfigurableApplicationContext context) {
		this.context = context;
	}

	public static void main(String[] args) {
		SpringApplication.run(BatchProcessingApplication.class, args);
	}
}