package com.power.platform.credit.service.companyInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.companyInfo.CreditCompanyInfoDao;
import com.power.platform.credit.entity.companyInfo.CreditCompanyInfo;

/**
 * 
 * 类: CreditCompanyInfoService <br>
 * 描述: 个人信贷公司信息Service. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年3月30日 上午11:45:09
 */
@Service("creditCompanyInfoService")
public class CreditCompanyInfoService extends CrudService<CreditCompanyInfo> {

	/**
	 * 公司信息传递文本表单字段4个(String类型的参数).
	 */
	public static final int IS_TEXT_FORM_FIELD_4 = 4;
	/**
	 * 公司信息传递文本表单字段4个(String类型的参数).
	 */
	public static final int IS_TEXT_FORM_FIELD_5 = 5;
	/**
	 * 上限：最多1条.
	 */
	public static final int CEILING_1 = 1;

	@Resource
	private CreditCompanyInfoDao creditCompanyInfoDao;

	@Override
	protected CrudDao<CreditCompanyInfo> getEntityDao() {

		return creditCompanyInfoDao;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int deleteCompanyInfoById(String id) {

		int flag = 0;

		try {
			flag = creditCompanyInfoDao.deleteCompanyInfoById(id);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("fn:deleteCompanyInfoById,{" + e.getMessage() + "}");
		}

		return flag;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateCreditCompanyInfo(CreditCompanyInfo creditCompanyInfo, Map<String, String> map) {

		int flag = 0;

		// 公司名称.
		creditCompanyInfo.setCompanyName(map.get("companyName"));
		// 对公银行账户.
		creditCompanyInfo.setBankAccountNo(map.get("bankAccountNo"));
		// 开户行.
		creditCompanyInfo.setBankName(map.get("bankName"));
		// 更新时间.
		creditCompanyInfo.setUpdateDate(new Date());

		try {
			flag = creditCompanyInfoDao.update(creditCompanyInfo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("fn:updateCreditCompanyInfo,{" + e.getMessage() + "}");
		}

		return flag;
	}

	/**
	 * 
	 * 方法: insertCreditCompanyInfo <br>
	 * 描述: 新增公司信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月30日 下午5:16:29
	 * 
	 * @param creditCompanyInfo
	 * @param map
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertCreditCompanyInfo(CreditCompanyInfo creditCompanyInfo, Map<String, String> map) {

		int flag = 0;

		// 公司名称.
		creditCompanyInfo.setCompanyName(map.get("companyName"));
		// 对公银行账户.
		creditCompanyInfo.setBankAccountNo(map.get("bankAccountNo"));
		// 开户行.
		creditCompanyInfo.setBankName(map.get("bankName"));
		// 创建时间.
		creditCompanyInfo.setCreateDate(new Date());
		// 更新时间.
		creditCompanyInfo.setUpdateDate(new Date());
		// 备注.
		creditCompanyInfo.setRemark("公司信息");

		try {
			flag = creditCompanyInfoDao.insert(creditCompanyInfo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("fn:insertCreditCompanyInfo,{" + e.getMessage() + "}");
		}

		return flag;
	}

	/**
	 * 
	 * 方法: getCreditCompanyInfoList <br>
	 * 描述: 根据用户id获取公司信息列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月31日 上午9:05:19
	 * 
	 * @param creditUserId
	 * @return
	 */
	public List<CreditCompanyInfo> getCreditCompanyInfoList(String creditUserId) {

		return creditCompanyInfoDao.getCreditCompanyInfoList(creditUserId);
	}

}