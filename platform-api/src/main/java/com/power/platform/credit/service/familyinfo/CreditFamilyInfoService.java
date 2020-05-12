package com.power.platform.credit.service.familyinfo;

import java.util.*;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.familyinfo.CreditFamilyInfoDao;
import com.power.platform.credit.entity.familyinfo.CreditFamilyInfo;

/**
 * 
 * 类: CreditFamilyInfoService <br>
 * 描述: 个人信贷家庭信息Service. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年3月28日 上午9:46:49
 */
@Service("creditFamilyInfoService")
public class CreditFamilyInfoService extends CrudService<CreditFamilyInfo> {

	/**
	 * 家庭信息传递文本表单字段5个(String类型的参数).
	 */
	public static final int IS_TEXT_FORM_FIELD_5 = 5;
	/**
	 * 家庭信息传递文本表单字段5个(String类型的参数).
	 */
	public static final int IS_TEXT_FORM_FIELD_6 = 6;
	/**
	 * 家庭信息增加上限6条.
	 */
	public static final int CEILING_6 = 6;

	@Resource
	private CreditFamilyInfoDao creditFamilyInfoDao;

	@Override
	protected CrudDao<CreditFamilyInfo> getEntityDao() {

		return creditFamilyInfoDao;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int deleteFamilyInfoById(String id) {

		int flag = 0;

		try {
			flag = creditFamilyInfoDao.deleteFamilyInfoById(id);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("fn:deleteFamilyInfoById,{" + e.getMessage() + "}");
		}
		return flag;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateCreditFamilyInfo(CreditFamilyInfo creditFamilyInfo, Map<String, String> map) {

		int flag = 0;
		// 关系类型.
		creditFamilyInfo.setRelationType(map.get("relationType"));
		// 姓名.
		creditFamilyInfo.setName(map.get("name"));
		// 手机号.
		creditFamilyInfo.setPhone(map.get("phone"));
		// 身份证号码.
		creditFamilyInfo.setIdCard(map.get("idCard"));
		// 更新时间.
		creditFamilyInfo.setUpdateDate(new Date());

		try {
			flag = creditFamilyInfoDao.update(creditFamilyInfo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("fn:updateCreditFamilyInfo,{" + e.getMessage() + "}");
		}
		return flag;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertCreditFamilyInfo(CreditFamilyInfo creditFamilyInfo, Map<String, String> map) {

		int flag = 0;
		// 关系类型.
		creditFamilyInfo.setRelationType(map.get("relationType"));
		// 姓名.
		creditFamilyInfo.setName(map.get("name"));
		// 手机号.
		creditFamilyInfo.setPhone(map.get("phone"));
		// 身份证号码.
		creditFamilyInfo.setIdCard(map.get("idCard"));
		// 创建时间.
		creditFamilyInfo.setCreateDate(new Date());
		// 更新时间.
		creditFamilyInfo.setUpdateDate(new Date());
		// 备注.
		creditFamilyInfo.setRemark("客户家庭信息");

		try {
			flag = creditFamilyInfoDao.insert(creditFamilyInfo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("fn:insertCreditFamilyInfo,{" + e.getMessage() + "}");
		}
		return flag;
	}

	/**
	 * 
	 * 方法: getCreditFamilyInfo <br>
	 * 描述: 根据用户id查询家庭信息列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月30日 上午8:59:16
	 * 
	 * @param creditUserInfoId
	 * @return
	 */
	public List<CreditFamilyInfo> getCreditFamilyInfoList(String creditUserId) {

		return creditFamilyInfoDao.getCreditFamilyInfoList(creditUserId);
	}
	
	
	
	/**
	 * 查询分页数据----后台专用
	 * @param page 分页对象
	 * @param entity
	 * @return
	 */
	public Page<CreditFamilyInfo> findPage1(Page<CreditFamilyInfo> page, CreditFamilyInfo entity) {
		entity.setPage(page);
		page.setList(creditFamilyInfoDao.findList1(entity));
		return page;
	}

}