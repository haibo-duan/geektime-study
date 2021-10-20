package com.dhb.mybatis.cache.demo;

import com.alibaba.fastjson.JSONObject;
import com.dhb.mybatis.cache.orm.dao.intf.BankAccountDao;
import com.dhb.mybatis.cache.orm.entity.BankAccountEntity;
import com.dhb.mybatis.cache.orm.mapper.BankAccountMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.Date;

@RestController
@Slf4j
public class LevelOneCacheController {
	
	@Autowired
	BankAccountDao bankAccountDao;
	
	@Autowired
	BankAccountMapper bankAccountMapper;

	@Autowired
	BankAccountMapper bankAccountMapper2;
	
	@Autowired
	SqlSessionFactory factory;
	
	/**
	 * mybatis中一级缓存的条件：
	 *   1.配置mybatis.configuration.local-cache-scope: session / statement
	 *   2.在同一个Transactional
	 *   这样相同的查询就不会再次请求mysql数据库，直接从缓存中获得
	 * @param id
	 * @return
	 */
	@RequestMapping("/testLevel1Cache")
	@Transactional
	public String testLevel1Cache(int id) {
		BankAccountEntity accountEntity = bankAccountMapper.findById(id);
		log.info(" 再次查询相同的数据。。。");
		accountEntity = bankAccountMapper.findById(id);
		accountEntity = bankAccountMapper.findById(id);
		return JSONObject.toJSONString(accountEntity);
	}

	/**
	 * 测试一级缓存失效的场景：
	 *   当一个session中存在update操作，则后续的第一次查询会不走cache
	 *  
	 * @param id
	 * @return
	 */
	@RequestMapping("/testLevel1CacheUpdate")
	@Transactional
	public String testLevel1CacheUpdate(int id) {
		BankAccountEntity accountEntity = bankAccountMapper.findById(id);
		accountEntity = bankAccountMapper.findById(id);
		BankAccountEntity updateEntity = new BankAccountEntity();
		updateEntity.setId(1);
		updateEntity.setUpdateTime(new Date());
		log.info(" 再次查询相同的数据。。。");
		bankAccountMapper.updateById(updateEntity);
		accountEntity = bankAccountMapper.findById(id);
		accountEntity = bankAccountMapper.findById(id);
		return JSONObject.toJSONString(accountEntity);
	}

	/**
	 * 当两个sqlMapper对同一个表操作的时候：
	 * 另外一个session中更新了，对当前session中的缓存并不产生任何影响，这样可看出缓存对数据结果造成了污染。
	 * 这也证明了，mybatis的一级缓存只在一个sqlSession中有效。 可以将一级缓存配置为statement,这样等价于变相关闭一级缓存。
	 * @param id
	 * @return
	 */
	@RequestMapping("/testLevel1Cache2Mapper")
	@Transactional
	public String testLevel1Cache2Mapper(int id) {
		
		SqlSession sqlSession = factory.openSession(true);
		BankAccountMapper bankAccountMapper2 = sqlSession.getMapper(BankAccountMapper.class);
		
		BankAccountEntity accountEntity = bankAccountMapper2.findById(id);
		log.info("1:"+JSONObject.toJSONString(accountEntity));
		accountEntity = bankAccountMapper2.findById(id);
		log.info("2:"+JSONObject.toJSONString(accountEntity));
		BankAccountEntity updateEntity = new BankAccountEntity();
		updateEntity.setId(1);
		updateEntity.setUpdateTime(new Date());
		bankAccountMapper.updateById(updateEntity);
		log.info(" 通过bankAccountMapper 修改数据，再次查询相同的数据。。。");
		accountEntity = bankAccountMapper.findById(id);
		log.info("3:"+JSONObject.toJSONString(accountEntity));
		accountEntity = bankAccountMapper2.findById(id);
		log.info("4:"+JSONObject.toJSONString(accountEntity));
		return JSONObject.toJSONString(accountEntity);
	}




}
