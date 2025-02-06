package com.vaibhav.SpringBatch.configuration;

import com.vaibhav.SpringBatch.entity.Person;
import com.vaibhav.SpringBatch.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
public class BatchConfiguration {

    @Autowired
    private PersonRepository personRepository;

    @Bean
    public Job job(JobRepository jobRepository, Step step){
        return new JobBuilder("importPerson", jobRepository)
                .start(step)
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return  new StepBuilder("csv-steps", jobRepository)
                .<Person, Person>chunk(100, transactionManager)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .faultTolerant()
                .skip(ObjectOptimisticLockingFailureException.class)
                .skipLimit(10)
                .build();
    }

    @Bean
    FlatFileItemReader<Person> itemReader() {
        log.info("Item Reader Initialized, looking for user_data.csv");
        return new FlatFileItemReaderBuilder<Person>()
                .name("itemReader")
                .resource(new ClassPathResource("user_data.csv"))
                .linesToSkip(1)
                .lineMapper(lineMapper())
                .targetType(Person.class)
                .build();
    }

    private LineMapper<Person> lineMapper() {
        log.info("Mapping Line from CSV Data");
        DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames("id","first_name","last_name","email","phone_number","city","country");
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);


        BeanWrapperFieldSetMapper<Person> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Person.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Bean
    PersonProcessor itemProcessor() {
        log.info("Item Processor Initialized");
        return new PersonProcessor();
    }

    @Bean
    RepositoryItemWriter<Person> itemWriter() {
        log.info("Item Writer Initialized");
        RepositoryItemWriter<Person> itemWriter = new RepositoryItemWriter<>();
        itemWriter.setRepository(personRepository);
        itemWriter.setMethodName("save");
        return itemWriter;
    }


}
