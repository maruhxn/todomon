package com.maruhxn.todomon.batch.job.todo_achievement;

import com.maruhxn.todomon.batch.listener.StopWatchJobListener;
import com.maruhxn.todomon.batch.rowmapper.MemberAchievementRowMapper;
import com.maruhxn.todomon.batch.validator.DateParameterValidator;
import com.maruhxn.todomon.batch.vo.MemberAchievementDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DailyTodoAchievementJobConfig {

    private static final int CHUNK_SIZE = 100;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager tx;
    private final DataSource dataSource;

    @Bean
    public Job dailyTodoAchievementJob(
            Step updateMemberStep,
            Step createTodoAchievementHistoryStep
    ) throws Exception {
        return new JobBuilder("dailyTodoAchievementJob", jobRepository)
                .validator(new DateParameterValidator())
                .incrementer(new RunIdIncrementer())
                .start(updateMemberStep)
                .next(createTodoAchievementHistoryStep)
                .listener(new StopWatchJobListener())
                .build();
    }

    @Bean
    @JobScope
    public Step dailyTodoAchievementStep(
            ItemReader<MemberAchievementDTO> memberItemReader,
            CompositeItemWriter<MemberAchievementDTO> compositeItemWriter
    ) {
        return new StepBuilder("dailyTodoAchievementStep", jobRepository)
                .<MemberAchievementDTO, MemberAchievementDTO>chunk(CHUNK_SIZE, tx) // 한 번에 처리할 청크 크기
                .reader(memberItemReader)
                .writer(compositeItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<MemberAchievementDTO> memberItemReader(
            @Value("#{jobParameters['date']}") String date,
            PagingQueryProvider queryProvider
    ) throws Exception {
        JdbcPagingItemReader<MemberAchievementDTO> reader = new JdbcPagingItemReader<>() {
            @Override
            public int getPage() {
                return 0;
            }
        };

        reader.setName("memberItemReader");
        reader.setPageSize(CHUNK_SIZE);
        reader.setDataSource(dataSource);
        reader.setRowMapper(new MemberAchievementRowMapper(date));
        reader.setQueryProvider(queryProvider);

        return reader;
    }

    @Bean
    public PagingQueryProvider queryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("id, daily_achievement_cnt");
        queryProvider.setFromClause("from member");
        queryProvider.setWhereClause("where daily_achievement_cnt > 0");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        return queryProvider.getObject();
    }

    @Bean
    @StepScope
    public CompositeItemWriter<MemberAchievementDTO> compositeItemWriter(
            JdbcBatchItemWriter<MemberAchievementDTO> updateMemberStateItemWriter,
            JdbcBatchItemWriter<MemberAchievementDTO> todoAchievementHistoryItemWriter
    ) {
        CompositeItemWriter<MemberAchievementDTO> compositeItemWriter = new CompositeItemWriter<>();
        compositeItemWriter.setDelegates(Arrays.asList(updateMemberStateItemWriter, todoAchievementHistoryItemWriter));
        return compositeItemWriter;
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<MemberAchievementDTO> updateMemberStateItemWriter() {
        return new JdbcBatchItemWriterBuilder<MemberAchievementDTO>()
                .dataSource(dataSource)
                .sql("UPDATE member " +
                        "SET star_point = star_point + scheduled_reward, " +
                        "daily_achievement_cnt = 0, " +
                        "scheduled_reward = 0 " +
                        "WHERE id = ?")
                .itemPreparedStatementSetter((item, ps) -> {
                    ps.setLong(1, item.getMemberId());
                })
                .build();
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<MemberAchievementDTO> todoAchievementHistoryItemWriter() {
        return new JdbcBatchItemWriterBuilder<MemberAchievementDTO>()
                .dataSource(dataSource)
                .sql("INSERT INTO todo_achievement_history (member_id, cnt, date, created_at, updated_at) VALUES (:memberId, :achievementCount, :date, now(), now())")
                .beanMapped()
                .build();
    }
}
