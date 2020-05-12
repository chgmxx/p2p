/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.ifcert.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.ifcert.entity.BatchNum;

/**
 * 批次数据状态信息表DAO接口
 * 
 * @author Roy
 * @version 2019-05-07
 */
@MyBatisDao
public interface BatchNumDao extends CrudDao<BatchNum> {

	List<BatchNum> fingBatchNumList(BatchNum bn);

	List<BatchNum> findBatchNum(@Param("infType") String infType,@Param("startTime") String startTime,@Param("endTime") String endTime);

}