package io.zeebe;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories
public class DataSource {
	
	@Bean
	public javax.sql.DataSource dataSource() {
		return DataSourceBuilder
		        .create()
		        .username("postgres")
		        .password("zeebe")
		        .url("jdbc:postgresql://localhost:5432/postgres")
		        .driverClassName("org.postgresql.Driver")
		        .build();

	}
}
