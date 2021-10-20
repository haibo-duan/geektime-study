package com.dhb.mybatis.cache.demo;

import com.alibaba.fastjson.JSONObject;
import com.dhb.mybatis.cache.orm.entity.BankAccountEntity;
import com.dhb.mybatis.cache.orm.mapper.BankAccountMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LevelTwoCacheController {

	@Autowired
	SqlSessionFactory factory;

	/**
	 * mybatis中二级缓存的条件：
	 * @param id
	 * @return
	 */
	@RequestMapping("/testLevel2CacheNoCommit")
	@Transactional
	public String testLevel2CacheNoCommit(int id) {
		SqlSession sqlSession1 = factory.openSession(true);
		SqlSession sqlSession2 = factory.openSession(true);
		BankAccountMapper accountMapper1 = sqlSession1.getMapper(BankAccountMapper.class);
		BankAccountMapper accountMapper2 = sqlSession2.getMapper(BankAccountMapper.class);
		
		BankAccountEntity accountEntity = accountMapper1.findById(id);
		log.info(" accountMapper1 查询数据："+JSONObject.toJSONString(accountEntity));
		accountEntity = accountMapper2.findById(id);
		log.info(" accountMapper2 查询数据："+JSONObject.toJSONString(accountEntity));
		return JSONObject.toJSONString(accountEntity);
	}

	/**
	 * mybatis中二级缓存的条件：
	 * @param id
	 * @return
	 */
	@RequestMapping("/testLevel2CacheCommit")
	public String testLevel2CacheCommit(int id) {
		SqlSession sqlSession1 = factory.openSession(true);
		SqlSession sqlSession2 = factory.openSession(true);
		BankAccountMapper accountMapper1 = sqlSession1.getMapper(BankAccountMapper.class);
		BankAccountMapper accountMapper2 = sqlSession2.getMapper(BankAccountMapper.class);

		BankAccountEntity accountEntity = accountMapper1.findById(id);
		log.info(" accountMapper1 查询数据："+accountEntity.toString());
		sqlSession1.commit();
		accountEntity = accountMapper2.findById(id);
		accountEntity = accountMapper2.findById(id);
		log.info(" accountMapper2 查询数据："+accountEntity.toString());
		return accountEntity.toString();
	}
	
	

}
