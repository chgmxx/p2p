package com.power.platform.credit.service.ztmgLoanBasicInfo;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.ztmgLoanBasicInfo.ZtmgLoanBasicInfoDao;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.ZtmgLoanBasicInfo;

/**
 * 
 * 类: ZtmgLoanBasicInfoService <br>
 * 描述: 借款人基本信息Service. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2018年4月28日 上午10:37:49
 */
@Service("ztmgLoanBasicInfoService")
public class ZtmgLoanBasicInfoService extends CrudService<ZtmgLoanBasicInfo> {

	@Resource
	private ZtmgLoanBasicInfoDao ztmgLoanBasicInfoDao;

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	protected CrudDao<ZtmgLoanBasicInfo> getEntityDao() {

		return ztmgLoanBasicInfoDao;
	}

}