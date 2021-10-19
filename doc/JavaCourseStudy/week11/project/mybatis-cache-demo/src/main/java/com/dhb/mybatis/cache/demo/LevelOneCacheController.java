package com.dhb.mybatis.cache.demo;

import com.alibaba.fastjson.JSONObject;
import com.dhb.mybatis.cache.orm.dao.intf.BankAccountDao;
import com.dhb.mybatis.cache.orm.entity.BankAccountEntity;
import com.dhb.mybatis.cache.orm.mapper.BankAccountMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	 * 另外一个sqlMapper更新操作，会造成全局刷新
	 * @param id
	 * @return
	 */
	@RequestMapping("/testLevel1Cache2Mapper")
	@Transactional
	public String testLevel1Cache2Mapper(int id) {
		BankAccountEntity accountEntity = bankAccountMapper.findById(id);
		log.info("1:"+JSONObject.toJSONString(accountEntity));
		accountEntity = bankAccountMapper.findById(id);
		log.info("1:"+JSONObject.toJSONString(accountEntity));
		BankAccountEntity updateEntity = new BankAccountEntity();
		updateEntity.setId(1);
		updateEntity.setUpdateTime(new Date());
		log.info(" 再次查询相同的数据。。。");
		bankAccountMapper2.updateById(updateEntity);
		accountEntity = bankAccountMapper2.findById(id);
		log.info("1:"+JSONObject.toJSONString(accountEntity));
		accountEntity = bankAccountMapper.findById(id);
		log.info("1:"+JSONObject.toJSONString(accountEntity));
		return JSONObject.toJSONString(accountEntity);
	}




}
