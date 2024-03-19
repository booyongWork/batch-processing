package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class BatchLogger {

    private static final Logger log = LoggerFactory.getLogger(BatchLogger.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BatchLogger(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 배치 작업이 완료된 후 호출되어 로그를 출력하는 메서드
    public void logBatchCompletion() {
        // 원하는 로그 출력 형식에 맞게 수정 가능
        log.info("##############배치 작업이 완료되었습니다##############");

//        System.out.println("Batch Job Execution Summary:");
//
//        List<Map<String, Object>> jobExecutionInfo = jdbcTemplate.queryForList(
//                "SELECT " +
//                        "    b.JOB_NAME, " +
//                        "    a.JOB_INSTANCE_ID, " +
//                        "    a.START_TIME, " +
//                        "    a.END_TIME, " +
//                        "    a.STATUS, " +
//                        "    a.EXIT_CODE, " +
//                        "    a.EXIT_MESSAGE " +
//                        "FROM " +
//                        "    batch_job_execution a " +
//                        "        JOIN " +
//                        "    batch_job_instance b ON a.JOB_INSTANCE_ID = b.JOB_INSTANCE_ID"
//        );
//
//        for (Map<String, Object> row : jobExecutionInfo) {
//            System.out.println("Job Name: " + row.get("JOB_NAME"));
//            System.out.println("Job Instance ID: " + row.get("JOB_INSTANCE_ID"));
//            System.out.println("Start Time: " + row.get("START_TIME"));
//            System.out.println("End Time: " + row.get("END_TIME"));
//            System.out.println("Status: " + row.get("STATUS"));
//            System.out.println("Exit Code: " + row.get("EXIT_CODE"));
//            System.out.println("Exit Message: " + row.get("EXIT_MESSAGE"));
//            System.out.println("----------------------------------------");
//
//        }
    }
}
