package io.github.aliontory.uni_meeting.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration //설정에 관련된 클래스임을 나타냄
@RequiredArgsConstructor
@Slf4j
@MapperScan("io.github.aliontory.uni_meeting.mapper")
public class MyBatisConfig {
    private final ApplicationContext applicationContext;

    // @Bean
    // @Configuration 또는 @Component가 작성된 클래스의 메서드에 사용
    // Method의 리턴 객체를 Spring Container에 등록
    // 객체명은 메서드의 이름으로 자동 설정, 직접 설정 시 (@Bean(name="객체명"))

    // 1. Property 가져오기
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    // 2. DataSource 설정
    @Bean
    public DataSource dataSource() {
        HikariDataSource hds = new HikariDataSource(hikariConfig());
        return hds;
    }

    // 3. SQLSessionFactory 만들기
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws IOException {
        SqlSessionFactoryBean sfb = new SqlSessionFactoryBean();
        sfb.setDataSource(dataSource());
        // SQL을 작성한 xml의 경로 설정(getResources의 s 주의)
        sfb.setMapperLocations(applicationContext.getResources("classpath*:/mappers/*.xml"));
        sfb.setConfigLocation(applicationContext.getResource("classpath:/config/config.xml"));

        SqlSessionFactory factory = null;
        try {
            factory = sfb.getObject();

            // today_amt => todayAmt 자동변환. (DB는 대소문자 구분이 없으므로)
            factory.getConfiguration().setMapUnderscoreToCamelCase(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return factory;
    }

}
