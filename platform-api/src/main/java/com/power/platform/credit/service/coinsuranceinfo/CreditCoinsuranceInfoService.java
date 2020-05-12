package com.power.platform.credit.service.coinsuranceinfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.coinsuranceinfo.CreditCoinsuranceInfoDao;
import com.power.platform.credit.entity.coinsuranceinfo.CreditCoinsuranceInfo;

/**
 * 
 * 类: CreditCoinsuranceInfoService <br>
 * 描述: 信贷联保Service. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年3月30日 下午5:37:49
 */
@Service("creditCoinsuranceInfoService")
public class CreditCoinsuranceInfoService extends CrudService<CreditCoinsuranceInfo> {

	/**
	 * 联保信息传递文本表单字段5个(String类型的参数).
	 */
	public static final int IS_TEXT_FORM_FIELD_5 = 5;
	/**
	 * 联保信息传递文本表单字段5个(String类型的参数).
	 */
	public static final int IS_TEXT_FORM_FIELD_6 = 6;
	/**
	 * 联保类型：'1'，个人.
	 */
	public static final int COINSURANCE_TYPE_1 = '1';
	/**
	 * 联保类型：'2'，公司.
	 */
	public static final int COINSURANCE_TYPE_2 = '2';
	/**
	 * 上限：最多6条.
	 */
	public static final int CEILING_6 = 6;

	@Resource
	private CreditCoinsuranceInfoDao creditCoinsuranceInfoDao;

	@Override
	protected CrudDao<CreditCoinsuranceInfo> getEntityDao() {

		return creditCoinsuranceInfoDao;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int deleteCoinsuranceInfoById(String id) {

		int flag = 0;

		try {
			flag = creditCoinsuranceInfoDao.deleteCoinsuranceInfoById(id);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("fn:deleteCoinsuranceInfoById,{" + e.getMessage() + "}");
		}

		return flag;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateCreditCoinsuranceInfo(CreditCoinsuranceInfo creditCoinsuranceInfo, Map<String, String> map) {

		int flag = 0;

		// 联保类型：'1'，个人，'2'，公司.
		creditCoinsuranceInfo.setCoinsuranceType(map.get("coinsuranceType"));
		// 公司名称.
		creditCoinsuranceInfo.setCompanyName(map.get("companyName"));
		// 姓名/法人姓名.
		creditCoinsuranceInfo.setName(map.get("name"));
		// 手机号/法人手机号.
		creditCoinsuranceInfo.setPhone(map.get("phone"));
		// 身份证号码.
		creditCoinsuranceInfo.setIdCard(map.get("idCard"));
		// 更新时间.
		creditCoinsuranceInfo.setUpdateDate(new Date());

		try {
			flag = creditCoinsuranceInfoDao.update(creditCoinsuranceInfo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("fn:updateCreditCoinsuranceInfo,{" + e.getMessage() + "}");
		}

		return flag;
	}

	/**
	 * 
	 * 方法: insertCreditCoinsuranceInfo <br>
	 * 描述: 新增联保信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月30日 下午5:51:06
	 * 
	 * @param creditCoinsuranceInfo
	 * @param map
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertCreditCoinsuranceInfo(CreditCoinsuranceInfo creditCoinsuranceInfo, Map<String, String> map) {

		int flag = 0;

		// 联保类型：'1'，个人，'2'，公司.
		creditCoinsuranceInfo.setCoinsuranceType(map.get("coinsuranceType"));
		// 公司名称.
		creditCoinsuranceInfo.setCompanyName(map.get("companyName"));
		// 姓名/法人姓名.
		creditCoinsuranceInfo.setName(map.get("name"));
		// 手机号/法人手机号.
		creditCoinsuranceInfo.setPhone(map.get("phone"));
		// 身份证号码.
		creditCoinsuranceInfo.setIdCard(map.get("idCard"));
		// 创建时间.
		creditCoinsuranceInfo.setCreateDate(new Date());
		// 更新时间.
		creditCoinsuranceInfo.setUpdateDate(new Date());
		// 备注.
		creditCoinsuranceInfo.setRemark("联保信息");

		try {
			flag = creditCoinsuranceInfoDao.insert(creditCoinsuranceInfo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("fn:insertCreditCoinsuranceInfo,{" + e.getMessage() + "}");
		}

		return flag;
	}

	/**
	 * 
	 * 方法: getCreditCoinsuranceInfoList <br>
	 * 描述: 根据用户id获取联保信息列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月31日 上午8:55:37
	 * 
	 * @param creditUserId
	 * @return
	 */
	public List<CreditCoinsuranceInfo> getCreditCoinsuranceInfoList(String creditUserId) {

		return creditCoinsuranceInfoDao.getCreditCoinsuranceInfoList(creditUserId);
	}
	
	/**
	 * 用于后台查询
	 */
	public Page<CreditCoinsuranceInfo> findPage1(Page<CreditCoinsuranceInfo> page, CreditCoinsuranceInfo entity) {
		entity.setPage(page);
		page.setList(creditCoinsuranceInfoDao.findList1(entity));
		return page;
	}

}