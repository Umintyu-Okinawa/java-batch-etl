package com.example.batch.config;

import com.example.batch.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    // ===== Reader: 10列CSVを name/country にマッピング =====
    @Bean
    public FlatFileItemReader<Customer> customerReader() {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .resource(new ClassPathResource("input/CustomerDate.csv"))
                .linesToSkip(1)               // ヘッダーありなら1
                // .encoding("MS932")         // CSVがSJISなら有効化
                .lineMapper(customerLineMapper())
                .build();
    }

    @Bean
    public LineMapper<Customer> customerLineMapper() {
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setStrict(false); // 列欠けがあっても致命的エラーにしない
        tokenizer.setNames(
                "code",        // D1000163
                "lastName",    // マケテ
                "firstName",   // テモ
                "age",         // 25
                "prefecture",  // 滋賀県
                "birthDate",   // 1932-12-10
                "score",       // 88
                "flag",        // 1
                "amount",      // 45700
                "regDate"      // 2021-08-20
        );

        lineMapper.setLineTokenizer(tokenizer);

        lineMapper.setFieldSetMapper((FieldSet fs) -> {
            Customer c = new Customer();
            String ln = fs.readString("lastName");
            String fn = fs.readString("firstName");
            // 日本名なのでスペースなしで結合（好みで " " を挟んでもOK）
            c.setName((ln == null ? "" : ln) + (fn == null ? "" : fn));
            c.setCountry(fs.readString("prefecture")); // 都道府県を country に格納
            return c;
        });

        return lineMapper;
    }

    // ===== Processor: 体裁整え（任意） =====
    @Bean
    public ItemProcessor<Customer, Customer> customerProcessor() {
        return item -> {
            if (item.getName() != null) item.setName(item.getName().trim());
            if (item.getCountry() != null) item.setCountry(item.getCountry().trim());
            return item;
        };
    }

    // ===== Writer: DBへINSERT =====
    @Bean
    public JdbcBatchItemWriter<Customer> customerWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Customer>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO customers (name, country) VALUES (:name, :country)")
                .dataSource(dataSource)
                .build();
    }

    // ===== Step =====
    @Bean
    public Step csvToDbStep(JobRepository jobRepository,
                            PlatformTransactionManager tx,
                            FlatFileItemReader<Customer> reader,
                            ItemProcessor<Customer, Customer> processor,
                            JdbcBatchItemWriter<Customer> writer) {
        return new StepBuilder("csvToDbStep", jobRepository)
                .<Customer, Customer>chunk(100, tx)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    // ===== Job（RunIdIncrementerで毎回実行可能に）=====
    @Bean
    public Job csvToDbJob(JobRepository jobRepository, Step csvToDbStep) {
        return new JobBuilder("csvToDbJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(csvToDbStep)
                .build();
    }
}
