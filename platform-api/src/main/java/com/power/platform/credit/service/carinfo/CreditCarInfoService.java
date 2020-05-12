package com.power.platform.credit.service.carinfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.carinfo.CreditCarInfoDao;
import com.power.platform.credit.entity.carinfo.CreditCarInfo;

/**
 * 
 * 类: CreditCarInfoService <br>
 * 描述: 信贷车产Service. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年3月30日 下午4:32:56
 */
@Service("creditCarInfoService")
public class CreditCarInfoService extends CrudService<CreditCarInfo> {

	/**
	 * 家庭信息传递文本表单字段2个(String类型的参数).
	 */
	public static final int IS_TEXT_FORM_FIELD_2 = 2;
	/**
	 * 家庭信息传递文本表单字段2个(String类型的参数).
	 */
	public static final int IS_TEXT_FORM_FIELD_3 = 3;
	/**
	 * 上限：3条.
	 */
	public static final int CEILING_3 = 3;

	@Resource
	private CreditCarInfoDao creditCarInfoDao;

	@Override
	protected CrudDao<CreditCarInfo> getEntityDao() {

		return creditCarInfoDao;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int deleteCarInfoById(String id) {

		int flag = 0;

		try {
			flag = creditCarInfoDao.deleteCarInfoById(id);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("fn:deleteCarInfoById,{" + e.getMessage() + "}");
		}

		return flag;
	}

	/**
	 * 
	 * 方法: updateCreditCarInfo <br>
	 * 描述: 修改车产信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月6日 上午10:03:41
	 * 
	 * @param creditCarInfo
	 * @param map
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateCreditCarInfo(CreditCarInfo creditCarInfo, Map<String, String> map) {

		int flag = 0;

		// 车牌号码.
		creditCarInfo.setPlateNumber(map.get("plateNumber"));
		// 更新时间.
		creditCarInfo.setUpdateDate(new Date());

		try {
			flag = creditCarInfoDao.update(creditCarInfo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("fn:updateCreditCarInfo,{" + e.getMessage() + "}");
		}

		return flag;
	}

	/**
	 * 
	 * 方法: insertCreditCarInfo <br>
	 * 描述: 新增车产信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月30日 下午4:45:57
	 * 
	 * @param creditCarInfo
	 * @param map
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertCreditCarInfo(CreditCarInfo creditCarInfo, Map<String, String> map) {

		int flag = 0;

		// 车牌号码.
		creditCarInfo.setPlateNumber(map.get("plateNumber"));
		// 创建时间.
		creditCarInfo.setCreateDate(new Date());
		// 更新时间.
		creditCarInfo.setUpdateDate(new Date());
		// 备注.
		creditCarInfo.setRemark("车产信息");

		try {
			flag = creditCarInfoDao.insert(creditCarInfo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("fn:insertCreditCarInfo,{" + e.getMessage() + "}");
		}

		return flag;
	}

	/**
	 * 
	 * 方法: getCreditCarInfoList <br>
	 * 描述: 根据用户id获取车产信息列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月31日 上午9:12:56
	 * 
	 * @param creditUserId
	 * @return
	 */
	public List<CreditCarInfo> getCreditCarInfoList(String creditUserId) {

		return creditCarInfoDao.getCreditCarInfoList(creditUserId);
	}
	
	/**
	 * 用于后台查询
	 */
	public Page<CreditCarInfo> findPage(Page<CreditCarInfo> page, CreditCarInfo entity) {
		entity.setPage(page);
		page.setList(creditCarInfoDao.findList1(entity));
		return page;
	}

}