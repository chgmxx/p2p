/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.bouns.dao;

import java.util.List;

import com.power.platform.bouns.entity.AwardInfo;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;


/**
 * 奖品信息DAO接口
 * @author yb
 * @version 2016-12-13
 */
@MyBatisDao
public interface AwardInfoDao extends CrudDao<AwardInfo> {

	//针对奖品列表是否显示谢谢惠顾  isTrue=2时 不显示
	List<AwardInfo> findList1(AwardInfo awardInfo);
	
	
	//针对奖品列表是否显示谢谢惠顾  isTrue=2时 不显示  PC
	List<AwardInfo> findList2(AwardInfo awardInfo);
	
}