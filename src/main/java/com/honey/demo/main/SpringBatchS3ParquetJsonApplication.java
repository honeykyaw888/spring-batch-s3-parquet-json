package com.honey.demo.main;

import com.honey.demo.reader.S3ParquetItemReader;
import com.honey.demo.processor.ParquetToJsonItemProcessor;
import com.honey.demo.util.MyIncrementerFactory;
import com.honey.demo.writer.S3JsonItemWriter;
import org.apache.parquet.example.data.Group;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.DefaultExecutionContextSerializer;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.support.DatabaseType;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@SpringBootApplication
@Configuration
@EnableBatchProcessing
public class SpringBatchS3ParquetJsonApplication implements CommandLineRunner {

	@Value("${destination.s3.bucket}")
	private String destinationBucket;

	@Value("${destination.s3.prefix}")
	private String destinationPrefix;



	public static void main(String[] args) {
		SpringApplication.run(SpringBatchS3ParquetJsonApplication.class, args);
	}

	@Bean
	public JobLauncher jobLauncher() throws Exception {
		TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
		jobLauncher.setJobRepository(jobRepository());
		jobLauncher.afterPropertiesSet();
		return jobLauncher;
	}

	@Override
	public void run(String... args) throws Exception {
		// Constructing JobParameters from command line arguments
		JobParametersBuilder builder = new JobParametersBuilder();
		for (String arg : args) {
			String[] keyValue = arg.split("=", 2);
			if (keyValue.length == 2) {
				builder.addString(keyValue[0], keyValue[1]);
			}
		}
		JobParameters jobParameters = builder.toJobParameters();
		System.err.println(jobParameters);


		// Launching the job
		jobLauncher().run(sampleJob(jobRepository(), sampleStep(jobRepository(), transactionManager(dataSource()))), jobParameters);
	}
	@Bean
	public DataSource dataSource() {
		EmbeddedDatabaseBuilder embeddedDatabaseBuilder = new EmbeddedDatabaseBuilder();
		return embeddedDatabaseBuilder.addScript("classpath:org/springframework/batch/core/schema-drop-h2.sql")
				.addScript("classpath:org/springframework/batch/core/schema-h2.sql")
				.setType(EmbeddedDatabaseType.H2)
				.build();
	}
	@Bean
	public JdbcTemplate batchJdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	public ResourcelessTransactionManager transactionManager(DataSource dataSource) {
		return new ResourcelessTransactionManager();
	}

	@Bean
	public JobRepository jobRepository() throws Exception {
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		factory.setDatabaseType(DatabaseType.H2.getProductName());
		factory.setDataSource(dataSource());
		factory.setIncrementerFactory(new MyIncrementerFactory(dataSource()));
		factory.setJdbcOperations(batchJdbcTemplate(dataSource()));
		factory.setConversionService(new DefaultFormattingConversionService());
		factory.setSerializer(new DefaultExecutionContextSerializer());
		return factory.getObject();
	}

	@Bean
	public Job sampleJob(JobRepository jobRepository, Step sampleStep) {
		return new JobBuilder("sampleJob", jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(sampleStep)
				.build();
	}

	@Bean
	public Step sampleStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("sampleStep", jobRepository)
				.<Group, String>chunk(10,  transactionManager)
				.reader(itemReader())
				.processor(itemProcessor())
				.writer(itemWriter())
				.build();
	}

	@Bean
	@StepScope
	public S3ParquetItemReader itemReader(){
		return new S3ParquetItemReader();
	}

	@Bean
	public ParquetToJsonItemProcessor itemProcessor() {
		return new ParquetToJsonItemProcessor();
	}

	@Bean
	@StepScope
	public S3JsonItemWriter itemWriter() {
		return new S3JsonItemWriter(destinationBucket, destinationPrefix);
	}
}
