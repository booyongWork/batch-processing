package com.example.batchprocessing;

import org.springframework.batch.core.JobInstance;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.JdbcTemplate;

public class StaticstemProcessor implements ItemProcessor<Person, Statics> {

    private final JdbcTemplate jdbcTemplate;

    public StaticstemProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Statics process(final Person person) {


        // Job 이름을 batch_job_instance 테이블에서 가져옴
        String jobName = getJobNameFromJobInstance();

        // 이미 해당 job이 실행되어 통계가 삽입되었는지 확인
        boolean jobExists = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM statics WHERE job_nm = ?", Integer.class, jobName) > 0;
        if (!jobExists) {
            System.out.println("두번째 배치 statics process 진행중");

            // 통계 정보를 계산하여 삽입
            int totalPeople = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM people", Integer.class);
            int maleCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM people WHERE gender = 'M'", Integer.class);
            int femaleCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM people WHERE gender = 'F'", Integer.class);
            int marriedCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM people WHERE married = 1", Integer.class);
            int unmarriedCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM people WHERE married = 0", Integer.class);

            // 각 연령대별 인원의 백분율을 계산하는 쿼리
            String teenagePercentageQuery = "SELECT ROUND((COUNT(*) / (SELECT COUNT(*) FROM people)) * 100, 2) AS teenage_percentage FROM people WHERE age >= 10 AND age < 20";
            String twentiesPercentageQuery = "SELECT ROUND((COUNT(*) / (SELECT COUNT(*) FROM people)) * 100, 2) AS teenage_percentage FROM people WHERE age >= 20 AND age < 30";
            String thirtiesPercentageQuery = "SELECT ROUND((COUNT(*) / (SELECT COUNT(*) FROM people)) * 100, 2) AS teenage_percentage FROM people WHERE age >= 30 AND age < 40";
            String fortiesPercentageQuery = "SELECT ROUND((COUNT(*) / (SELECT COUNT(*) FROM people)) * 100, 2) AS teenage_percentage FROM people WHERE age >= 40 AND age < 50";
            String fiftiesPercentageQuery = "SELECT ROUND((COUNT(*) / (SELECT COUNT(*) FROM people)) * 100, 2) AS teenage_percentage FROM people WHERE age >= 50 AND age < 60";

            // 각 연령대별 인원의 백분율을 조회하여 결과를 가져옴
            double teenagePercentage = jdbcTemplate.queryForObject(teenagePercentageQuery, Double.class);
            double twentiesPercentage = jdbcTemplate.queryForObject(twentiesPercentageQuery, Double.class);
            double thirtiesPercentage = jdbcTemplate.queryForObject(thirtiesPercentageQuery, Double.class);
            double fortiesPercentage = jdbcTemplate.queryForObject(fortiesPercentageQuery, Double.class);
            double fiftiesPercentage = jdbcTemplate.queryForObject(fiftiesPercentageQuery, Double.class);

            // Statics 객체 생성하여 반환
            return new Statics(jobName, totalPeople, maleCount, femaleCount, marriedCount, unmarriedCount,
                    teenagePercentage, twentiesPercentage, thirtiesPercentage, fortiesPercentage, fiftiesPercentage);
        }

        // 이미 통계가 삽입되었을 경우 null 반환
        return null;
    }

    // Job 이름을 batch_job_instance 테이블에서 가져오는 메서드
    private String getJobNameFromJobInstance() {
        // 가장 최근에 실행된 JobInstance를 가져옴
        JobInstance lastJobInstance = jdbcTemplate.queryForObject(
                "SELECT * FROM batch_job_instance ORDER BY job_instance_id DESC LIMIT 1",
                (rs, rowNum) -> {
                    long jobInstanceId = rs.getLong("job_instance_id");
                    String jobName = rs.getString("job_name");
                    return new JobInstance(jobInstanceId, jobName);
                });

        return lastJobInstance.getJobName();
    }
}
