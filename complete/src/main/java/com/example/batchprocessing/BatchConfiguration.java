package com.example.batchprocessing;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

//NOTE.
// ItemReader : 배치데이터를 읽어오는 인터페이스. DB뿐 아니라 File, XML 등 다양한 타입에서 읽어올 수 있다.
// ItemProcessor : 읽어온 데이터를 가공/처리 한다. 즉, 비즈니스 로직을 처리
// ItemWriter : 처리한 데이터를 DB에 저장
// Step : 기본구조(읽고, 처리하고, 저장한다)는 Step 이라는 객체에서 정의된다. Step은 읽고, 처리하고 저장하는 구조를 가지고 있는 가장 실질적인
// 배치처리를 담당하는 도메인 객체. 이 Step은 한 개 혹은 여러 개가 이루어 Job을 표현한다.
// Job : 한개 혹은 여러 개의 Step을 이루어 하나의 단위로 만들어 표현한 객체. 스프링 배치 처리에서 가장 윗 계층에 있는 객체

@Configuration
public class BatchConfiguration {
	@Autowired
	private DataSource dataSource;
	//NOTE. @Bean - 스프링 프레임워크에서 빈(Bean)으로 관리될 객체를 선언하는 데 사용되는 어노테이션
	// 특정 형식의 파일(여기서는 CSV 파일)을 읽어들이는 빈
	// reader() 함수에서 csv 파일에서 데이터를 읽어와서 객체안에 넣어놓는다.
	@Bean
	public JdbcCursorItemReader<PersonDTO> reader() {
		System.out.println("reader 실행");
		JdbcCursorItemReader<PersonDTO> reader = new JdbcCursorItemReader<>();
		reader.setDataSource(dataSource);
		reader.setSql("SELECT first_name, last_name, gender, married, age, address FROM people");
		reader.setRowMapper(new BeanPropertyRowMapper<>(PersonDTO.class));
		return reader;
	}

	// 대문자를 소문자로 치환작업
	@Bean
	public ItemProcessor<PersonDTO, TodayRegisteredUser> processor() {
		System.out.println("processor 실행");
		return new ItemProcessor<PersonDTO, TodayRegisteredUser>() {
			@Override
			public TodayRegisteredUser process(final PersonDTO person) {
				// Person을 TodayRegisteredUser로 변환하는 로직 추가
//				final String firstName = person.getFirstName().toUpperCase();
//				final String lastName = person.getLastName().toUpperCase();

				// 새로운 TodayRegisteredUser 객체 생성
				return new TodayRegisteredUser(person.getFirstName(), person.getLastName(), person.getGender(), person.isMarried(), person.getAge(), person.getAddress());
			}
		};
	}

	//NOTE. JdbcBatchItemWriter - 일반적으로 Spring Batch 작업의 마지막 단계에서 사용되며, 이전 단계에서 처리된 데이터를 최종적으로 데이터베이스에 저장하는 데 활용
	// Person에 매핑되어 있는 값을 JdbcBatchItemWriter db에 저장
	@Bean
	public JdbcBatchItemWriter<TodayRegisteredUser> writer(DataSource dataSource) {
		System.out.println("writer 실행");
		return new JdbcBatchItemWriterBuilder<TodayRegisteredUser>()
				.sql("INSERT INTO TodayRegisteredUsers (first_name, last_name, gender, married, age, address) " +
						"VALUES (?, ?, ?, ?, ?, ?)")
				.itemPreparedStatementSetter(new TodayRegisteredUserPreparedStatementSetter())
				.dataSource(dataSource)
				.build();
	}

	//NOTE. StepBuilder - SpringBatch에서 Step을 생성하는 데 사용되는 빌더 클래스 인스턴스를 생성하고 chunk()
	// 메서드를 통해 청크(Chunk) 처리 사이즈와 트랜잭션 관리자(TransactionManager)를 설정하고, reader(), processor(), writer()
	// 설정한 다음 build() 메서드를 호출하여 Step 객체를 생성
	// chunk() - 청크는 일괄 처리(batch processing)의 기본 단위이며, 설정된 크기만큼의 아이템을 읽어 처리하고, 성공적으로 처리된 경우에만 커밋되는 트랜잭션
	// JobRepository - 배치 작업(Job)의 실행 상태와 관련된 메타데이터를 저장하고 관리하는 인터페이스
	// 작업 실행 상태, 작업 실행 이력, 단계 실행 상태, 중복 방지 정보 등의 내용이 있다.
	// 스텝에서 불러온 파일을 가공하고 insert할꺼라는 걸 설정
	@Bean
	public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
					  JdbcCursorItemReader<PersonDTO> reader, ItemProcessor<PersonDTO, TodayRegisteredUser> processor, JdbcTemplate jdbcTemplate) {
		System.out.println("Step1 준비");
		return new StepBuilder("step1", jobRepository)
				.<PersonDTO, TodayRegisteredUser>chunk(1) // TodayRegisteredUser로 변환
				.reader(reader)
				.processor(processor) // Processor에서 Person을 TodayRegisteredUser로 변환
				.writer(new JdbcBatchItemWriterBuilder<TodayRegisteredUser>()
						.sql("INSERT INTO TodayRegisteredUsers (first_name, last_name, gender, married, age, address) " +
								"VALUES (?, ?, ?, ?, ?, ?)")
						.itemPreparedStatementSetter(new TodayRegisteredUserPreparedStatementSetter()) // 여기를 수정
						.dataSource(jdbcTemplate.getDataSource())
						.build())
				.transactionManager(transactionManager)
				.build();
	}



	// NOTE. JobBuilder -  Spring Batch에서 Job을 생성하는 데 사용되는 빌더 클래스
	//  이 클래스의 인스턴스를 생성하고 여러 설정 메서드를 사용하여 Job의 속성을 지정한 다음 build() 메서드를 호출하여 Job 객체를 생성
	//  JobCompletionNotificationListener: JobCompletionNotificationListener는 배치 작업이 완료될 때 실행되는 리스너

	//여기서 User를 불러오는 job을 실행
	@Bean
	public Job importUserJob(JobRepository jobRepository, Step step1, JobCompletionNotificationListener listener, Step addressStep) {
		System.out.println("importUserJob 실행");
		Job job = new JobBuilder("importUserJob", jobRepository)
				.listener(listener) // 배치 작업이 완료되면 여기서 afterJob을 호출해서 원하는 다음 작업 진행
				.start(step1) // 배치작업 시작
				.next(addressStep)
				.build();
		// Job 이름을 JobCompletionNotificationListener에 전달
		((JobCompletionNotificationListener) listener).setJobName(job.getName());
		return job;
	}
}
