package com.example.batchprocessing;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/batch")
public class BatchController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job manualImportTodayUserJob; // importTodayUserJob의 Job Bean을 주입

    @GetMapping("/execute")
    public String executeBatch() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis()) // 시간 기반 JobParameter 추가
                .toJobParameters();
        jobLauncher.run(manualImportTodayUserJob, jobParameters); // 배치 실행
        return "redirect:/"; // 실행 후 홈 페이지로 리다이렉트
    }
}
