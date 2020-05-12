package com.power.platform.credit.service.ztmgLoanBasicInfo;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.ztmgLoanBasicInfo.ZtmgLoanShareholdersInfoDao;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.ZtmgLoanShareholdersInfo;

/**
 * 
 * 类: ZtmgLoanShareholdersInfoService <br>
 * 描述: 借款人股东信息Service. <br>
 * 作者: Mr.Roy <br>
 * 时间: 2018年5月2日 上午9:22:01
 */
@Service("ztmgLoanShareholdersInfoService")
public class ZtmgLoanShareholdersInfoService extends CrudService<ZtmgLoanShareholdersInfo> {

	@Resource
	private ZtmgLoanShareholdersInfoDao ztmgLoanShareholdersInfoDao;

	@Override
	protected CrudDao<ZtmgLoanShareholdersInfo> getEntityDao() {

		return ztmgLoanShareholdersInfoDao;
	}

}