package com.dhb.gts.javacourse.week6.mysqltest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class C3p0DatasourceConfig {

//	@Bean(name = "dataSource")
//	@Qualifier(value = "dataSource")
//	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		       return DataSourceBuilder.create().type(com.mchange.v2.c3p0.ComboPooledDataSource.class).build();
	}
	
}
