package com.dhb.gts.javacourse.week7.dynamic;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Aspect
@Order(-1)// 保证该AOP在@Transactional之前执行
@Component
@Slf4j
public class DynamicDataSourceAspect {

	private static volatile AtomicInteger index = new AtomicInteger(0);
	
	@Before("@annotation(ds)")
	public void changeDataSource(JoinPoint point, TargetDataSource ds) throws Throwable {
		String dsId = ds.name();
		if("slave".equals(dsId)){
			log.info("如果传入为slave标识，则将在所有的slave数据库中进行负载均衡");
			//首先通过slaveLoadBalance 方法获得一个数据源name
			String dsName = slaveLoadBalance();
			//由于master与slave数据源都在这个数组里面，因此可能存在拿到master数据源的情况，此处如果没有拿到slave数据源将会重试
			for(int i=0;i<DynamicDataSourceContextHolder.dataSourceIds.size();i++) {
				if(dsName.startsWith("slave")){
					log.info("Use DataSource : {} > {}", dsName, point.getSignature());
					DynamicDataSourceContextHolder.setDataSourceType(dsName);
					break;
				}
				dsName = slaveLoadBalance();
			}
			log.error("尝试多次仍然没有获取到slave相关的数据源，将使用默认数据源 > {}",  point.getSignature());
			//如果尝试完成仍然没有拿到合适的数据源，则将通过默认数据源
		}else if (!DynamicDataSourceContextHolder.containsDataSource(dsId)) {
			log.error("数据源[{}]不存在，使用默认数据源 > {}", ds.name(), point.getSignature());
		}else {
			log.info("Use DataSource : {} > {}", dsId, point.getSignature());
			DynamicDataSourceContextHolder.setDataSourceType(dsId);
		}
	}
	
	@After("@annotation(ds)")
	public void restoreDataSource(JoinPoint point, TargetDataSource ds) {
		log.debug("Revert DataSource : {} > {}", ds.name(), point.getSignature());
		DynamicDataSourceContextHolder.clearDataSourceType();
	}
	
	private String slaveLoadBalance() {
		if(index.get() >= DynamicDataSourceContextHolder.dataSourceIds.size()){
			index.set(0);
		}
		return DynamicDataSourceContextHolder.dataSourceIds.get(index.getAndIncrement());
	}
}
