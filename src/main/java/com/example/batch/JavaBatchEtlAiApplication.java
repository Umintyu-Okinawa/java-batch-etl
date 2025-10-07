package com.example.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.boot.ApplicationRunner;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.JobExecution;

@SpringBootApplication
public class JavaBatchEtlAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaBatchEtlAiApplication.class, args);
    }

    // 起動時に sampleJob を必ず実行する
    @Bean
    ApplicationRunner runJobOnStartup(JobLauncher jobLauncher, Job sampleJob) {
        return args -> {
            JobParameters params = new JobParametersBuilder()
                    .addLong("ts", System.currentTimeMillis()) // 毎回ユニーク
                    .toJobParameters();

            JobExecution exec = jobLauncher.run(sampleJob, params);
            System.out.println(">>> Job ExitStatus = " + exec.getExitStatus());
        };
    }
}
