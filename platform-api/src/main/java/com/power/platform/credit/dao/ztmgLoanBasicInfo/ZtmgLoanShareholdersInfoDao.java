package com.power.platform.credit.dao.ztmgLoanBasicInfo;

import java.util.List;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.ZtmgLoanShareholdersInfo;

/**
 * 
 * 类: ZtmgLoanShareholdersInfoDao <br>
 * 描述: 借款人股东信息DAO接口. <br>
 * 作者: Mr.Roy <br>
 * 时间: 2018年5月2日 上午9:19:50
 */
@MyBatisDao
public interface ZtmgLoanShareholdersInfoDao extends CrudDao<ZtmgLoanShareholdersInfo> {

	List<ZtmgLoanShareholdersInfo> findListByLoanBasicInfoId(ZtmgLoanShareholdersInfo entity);

}