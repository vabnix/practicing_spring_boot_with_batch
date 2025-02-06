package com.vaibhav.SpringBatch.controller;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @PostMapping("/import")
    public String getData(){
        final JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis()).toJobParameters();

        try {
            final JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            return jobExecution.getStatus().toString();

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobParametersInvalidException |
                 JobInstanceAlreadyCompleteException e){
            e.printStackTrace();
             return "Job has failed due to "+ e.getMessage();
        }

    }
}
