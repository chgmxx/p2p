package com.power.platform.credit.service.basicinfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.basicinfo.CreditBasicInfoDao;
import com.power.platform.credit.entity.basicinfo.CreditBasicInfo;

/**
 * 
 * 类: CreditBasicInfoService <br>
 * 描述: 个人信贷基本信息Service <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年3月28日 上午9:09:25
 */
@Service("creditBasicInfoService")
public class CreditBasicInfoService extends CrudService<CreditBasicInfo> {

	/**
	 * 婚姻，1：未婚.
	 */
	public static final String MARITAL_STATUS_1 = "1";
	/**
	 * 婚姻，2：已婚.
	 */
	public static final String MARITAL_STATUS_2 = "2";

	/**
	 * 学历，1：小学.
	 */
	public static final String EDUCATION_STATUS_1 = "1";
	/**
	 * 学历，2：初中.
	 */
	public static final String EDUCATION_STATUS_2 = "2";
	/**
	 * 学历，3：高中.
	 */
	public static final String EDUCATION_STATUS_3 = "3";
	/**
	 * 学历，4：大学专科.
	 */
	public static final String EDUCATION_STATUS_4 = "4";
	/**
	 * 学历，5：大学本科.
	 */
	public static final String EDUCATION_STATUS_5 = "5";
	/**
	 * 学历，6：研究生.
	 */
	public static final String EDUCATION_STATUS_6 = "6";
	/**
	 * 学历，7：硕士.
	 */
	public static final String EDUCATION_STATUS_7 = "7";
	/**
	 * 学历，8：博士.
	 */
	public static final String EDUCATION_STATUS_8 = "8";
	/**
	 * 学历，9：博士后.
	 */
	public static final String EDUCATION_STATUS_9 = "9";
	/**
	 * 学历，10：智者.
	 */
	public static final String EDUCATION_STATUS_10 = "10";

	/**
	 * 性别，M：男.
	 */
	public static final String BORROWER_GENDER_M = "M";
	/**
	 * 性别，F：女.
	 */
	public static final String BORROWER_GENDER_F = "F";
	/**
	 * 性别，N：未知.
	 */
	public static final String BORROWER_GENDER_N = "N";

	/**
	 * 基本信息传递文本表单字段3个(String类型的参数).
	 */
	public static final int IS_TEXT_FORM_FIELD_3 = 3;
	/**
	 * 基本信息传递文本表单字段4个(String类型的参数).
	 */
	public static final int IS_TEXT_FORM_FIELD_4 = 4;
	/**
	 * 基本信息传递文本表单字段5个(String类型的参数).
	 */
	public static final int IS_TEXT_FORM_FIELD_5 = 5;
	/**
	 * 基本信息传递文本表单字段6个(String类型的参数).
	 */
	public static final int IS_TEXT_FORM_FIELD_6 = 6;
	/**
	 * 基本信息传递文本表单字段6个(String类型的参数).
	 */
	public static final int IS_TEXT_FORM_FIELD_7 = 7;

	@Resource
	private CreditBasicInfoDao creditBasicInfoDao;

	@Override
	protected CrudDao<CreditBasicInfo> getEntityDao() {

		return creditBasicInfoDao;
	}

	public List<CreditBasicInfo> getCreditBasicInfo(String creditUserId) {

		return creditBasicInfoDao.getCreditBasicInfo(creditUserId);
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertCreditBasicInfo(CreditBasicInfo creditBasicInfo, Map<String, String> map) {

		int flag = 0;
		// 姓名.
		creditBasicInfo.setName(map.get("name"));
		// 年龄.
		creditBasicInfo.setAge(map.get("age"));
		// 婚姻状况.
		creditBasicInfo.setMaritalStatus(map.get("maritalStatus"));
		// 学历.
		creditBasicInfo.setEducationStatus(map.get("educationStatus"));
		// 身份证号码.
		creditBasicInfo.setIdCard(map.get("idCard"));
		// 创建时间.
		creditBasicInfo.setCreateDate(new Date());
		// 更新时间.
		creditBasicInfo.setUpdateDate(new Date());
		// 备注.
		creditBasicInfo.setRemark("客户基本信息");

		try {
			flag = creditBasicInfoDao.insert(creditBasicInfo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("fn:insertCreditBasicInfo,{" + e.getMessage() + "}");
		}
		return flag;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateCreditBasicInfo(CreditBasicInfo creditBasicInfo, Map<String, String> map) {

		int flag = 0;
		// 姓名.
		creditBasicInfo.setName(map.get("name"));
		// 年龄.
		creditBasicInfo.setAge(map.get("age"));
		// 婚姻状况.
		creditBasicInfo.setMaritalStatus(map.get("maritalStatus"));
		// 学历.
		creditBasicInfo.setEducationStatus(map.get("educationStatus"));
		// 身份证号码.
		creditBasicInfo.setIdCard(map.get("idCard"));
		// 更新时间.
		creditBasicInfo.setUpdateDate(new Date());

		try {
			flag = creditBasicInfoDao.update(creditBasicInfo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("fn:updateCreditBasicInfo,{" + e.getMessage() + "}");
		}
		return flag;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int deleteBasicInfoById(String id) {

		int flag = 0;

		try {
			flag = creditBasicInfoDao.deleteBasicInfoById(id);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("fn:deleteBasicInfoById,{" + e.getMessage() + "}");
		}
		return flag;
	}

}