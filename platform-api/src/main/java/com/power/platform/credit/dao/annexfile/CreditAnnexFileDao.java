package com.power.platform.credit.dao.annexfile;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;

/**
 * 
 * 类: CreditAnnexFileDao <br>
 * 描述: 信贷附件DAO接口. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年4月1日 上午9:42:00
 */
@MyBatisDao
public interface CreditAnnexFileDao extends CrudDao<CreditAnnexFile> {

	List<CreditAnnexFile> findCreditAnnexFileList(@Param("otherId") String otherId);

	int deleteById(String id);

	List<CreditAnnexFile> findCreditAnnexFileListByType(CreditAnnexFile creditAnnexFile);

	List<CreditAnnexFile> findList1(CreditAnnexFile creditAnnexFile);

	List<CreditAnnexFile> findCreditAnnexFileToInvestment(CreditAnnexFile annexFile);
	//供应商销户
	void deleteCreditAnnexFileByUserId(String userId);
}