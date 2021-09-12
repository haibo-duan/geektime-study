package com.dhb.gts.javacourse.week6.mysqltest;


import cn.org.atool.generator.javafile.summary.ATMFile;
import com.alibaba.fastjson.JSON;
import com.dhb.gts.javacourse.fluent.dao.intf.CustomerInfoDao;
import com.dhb.gts.javacourse.fluent.entity.CustomerInfoEntity;
import com.dhb.gts.javacourse.fluent.mapper.CustomerInfoMapper;
import com.dhb.gts.javacourse.fluent.wrapper.CustomerInfoQuery;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class CustomerInfoContraller {

	@Autowired
	CustomerInfoDao customerInfoDao;
	
	@Autowired
	CustomerInfoMapper customerInfoMapper;

	@RequestMapping("/customerInfo/add")
	public String AddCustomer(String userid,String name, String mobile,String email,String identityNo,String gender) {
		if(!Strings.isNullOrEmpty(userid)){
			CustomerInfoEntity customerInfo = new CustomerInfoEntity()
					.setUserId(Integer.parseInt(userid))
					.setName(name)
					.setMobile(mobile)
					.setEmail(email)
					.setIdentityCardType(1)
					.setIdentityCardNo(identityNo)
					.setGender(gender)
					.setIsValidate(1);
			customerInfoMapper.insert(customerInfo);
		}
		
		return "success";
	}

	@RequestMapping("/customerInfo/delete")
	public String deleteCustomer(String id) {
		if(!Strings.isNullOrEmpty(id)){
			try {
				Integer key = new Integer(Integer.parseInt(id));
				customerInfoMapper.deleteById(key);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return "id 不能转换为int";
			}
		}else {
			return "id 为空！";
		}
		return "success";
	}

	@RequestMapping("/customerInfo/modify")
	public String modifyCustomer(String id,String name, String mobile,String email,String identityNo,String gender) {
		if(!Strings.isNullOrEmpty(id)) {
			int key = Integer.parseInt(id);
			CustomerInfoEntity customerInfo = new CustomerInfoEntity()
					.setId(key);
			if(!Strings.isNullOrEmpty(name)){
				customerInfo.setName(name);
			}
			if(!Strings.isNullOrEmpty(mobile)){
				customerInfo.setMobile(mobile);
			}
			if(!Strings.isNullOrEmpty(email)){
				customerInfo.setEmail(email);
			}
			if(!Strings.isNullOrEmpty(identityNo)){
				customerInfo.setIdentityCardNo(identityNo);
			}
			if(!Strings.isNullOrEmpty(gender)){
				customerInfo.setGender(gender);
			}
			customerInfoMapper.updateById(customerInfo);
			return "success";
		}else {
			return "id不能为空！";
		}
	}

	@RequestMapping("/customerInfo/query")
	public String modifyCustomer(String id,String userid) {
		if(!Strings.isNullOrEmpty(id)) {
			Integer key = Integer.parseInt(id);
			CustomerInfoEntity customerInfo = customerInfoMapper.findById(key);
			return JSON.toJSONString(customerInfo);
		}else if(!Strings.isNullOrEmpty(userid)){
			Integer userId = Integer.parseInt(userid);
			CustomerInfoQuery query = new CustomerInfoQuery().selectAll()
					.where.userId().eq(userId).end();
			List<CustomerInfoEntity> list = customerInfoMapper.listEntity(query);
			return JSON.toJSONString(list);
		}else {
			return "id不能为空！";
		}
	}


}
