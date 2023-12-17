package io.zeebe;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DataSource {
	
	public static DriverManagerDataSource getDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource(); // org.apache.tomcat.jdbc.pool.DataSource;
	    dataSource.setDriverClassName("org.postgresql.Driver");
	    dataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
	    dataSource.setUsername("postgres");
	    dataSource.setPassword("zeebe");
	    return dataSource;
	}
}
