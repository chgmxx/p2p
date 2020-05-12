/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.riskmanagement.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.riskmanagement.dao.RiskManagementDao;
import com.power.platform.riskmanagement.entity.RiskManagement;


/**
 * 风控企业信息Service
 * @author yb
 * @version 2016-10-11
 */
@Service("riskManagementService")
@Transactional(readOnly = true)
public class RiskManagementService extends CrudService<RiskManagement> {

	public static final String RISK_MANAGEMENT = "risk_management";
	
	public static final String RISK_MANAGEMENT_TYPE0 = "100";
	
	public static final String RISK_MANAGEMENT_TYPE1 = "101";
	
	public static final String STATE_0 = "0"; //拒绝
	
	public static final String STATE_1 = "1"; //初始
	
	public static final String STATE_2 = "2"; //风控专员审批通过
	
	public static final String STATE_3 = "3"; //风控经理审批通过
	
	public static final String STATE_4 = "4"; //总经理审批通过
	
	public static final String STATE_5 = "5"; //签约
	
	@Resource
	private RiskManagementDao riskManagementDao;
	public RiskManagement get(String id) {
		return super.get(id);
	}
	
	public List<RiskManagement> findList(RiskManagement riskManagement) {
		return super.findList(riskManagement);
	}
	
	@Transactional(readOnly = false)
	public void save(RiskManagement riskManagement) {
		super.save(riskManagement);
	}
	
	@Transactional(readOnly = false)
	public void delete(RiskManagement riskManagement) {
		super.delete(riskManagement);
	}

	@Override
	protected CrudDao<RiskManagement> getEntityDao() {
		// TODO Auto-generated method stub
		return riskManagementDao;
	}
	
}