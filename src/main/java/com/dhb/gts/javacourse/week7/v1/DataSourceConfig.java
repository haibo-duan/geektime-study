package com.dhb.gts.javacourse.week7.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;

//@Configuration
@Slf4j
public class DataSourceConfig {
	
//	@Bean
//	@ConfigurationProperties("spring.datasource.dynamic.slave1")
//	public DataSourceProperties slave1rDataSourceProperties(){
//		return new DataSourceProperties();
//	}
//	
//	@Bean
//	public DataSource slave1DataSource() {
//		DataSourceProperties dataSourceProperties = slave1rDataSourceProperties();
//		log.info("master datasource : {}", dataSourceProperties.getUrl());
//		return dataSourceProperties.initializeDataSourceBuilder().build();
//	}
//
//	@Bean
//	@Resource
//	public PlatformTransactionManager slave1TransactionManager(DataSource slave1DataSource) {
//		return new DataSourceTransactionManager(slave1DataSource);
//	}
//
//
//	@Bean
//	@ConfigurationProperties("spring.datasource.dynamic.master")
//	public DataSourceProperties masterDataSourceProperties(){
//		return new DataSourceProperties();
//	}
//
//	@Bean
//	@Primary
//	public DataSource masterDataSource() {
//		DataSourceProperties dataSourceProperties = masterDataSourceProperties();
//		log.info("slave1 datasource : {}", dataSourceProperties.getUrl());
//		return dataSourceProperties.initializeDataSourceBuilder().build();
//	}
//
//	@Bean
//	@Resource
//	public PlatformTransactionManager masterTransactionManager(DataSource masterDataSource) {
//		return new DataSourceTransactionManager(masterDataSource);
//	}


}
