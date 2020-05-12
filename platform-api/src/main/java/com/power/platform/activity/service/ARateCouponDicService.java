package com.power.platform.activity.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.activity.dao.ARateCouponDicDao;
import com.power.platform.activity.entity.ARateCouponDic;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;

@Service("aRateCouponDicService")
public class ARateCouponDicService extends CrudService<ARateCouponDic> {

	/**
	 * 状态，1：未使用，可以变更及删除.
	 */
	public static final String A_RATE_COUPON_DIC_STATE_1 = "1";

	/**
	 * 状态，2：使用中，不可变更及删除.
	 */
	public static final String A_RATE_COUPON_DIC_STATE_2 = "2";

	@Resource
	private ARateCouponDicDao aRateCouponDicDao;

	public List<ARateCouponDic> findAll() {

		return null;
	}

	@Override
	protected CrudDao<ARateCouponDic> getEntityDao() {

		logger.info("fn:getEntityDao,{获取当前DAO}");
		return aRateCouponDicDao;
	}

	/**
	 * 
	 * 方法: findAllList <br>
	 * 描述: 获取所有字典数据. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年4月18日 下午5:14:32
	 * 
	 * @param aRateCouponDic
	 * @return
	 */
	public List<ARateCouponDic> findAllList() {

		return aRateCouponDicDao.findAllARateCouponDics();
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateARateCouponDic(ARateCouponDic aRateCouponDic) {

		int flag = 0;
		try {
			flag = aRateCouponDicDao.update(aRateCouponDic);
			logger.info("fn:updateARateCouponDic,{修改保存成功}");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:updateARateCouponDic,{修改保存异常：" + e.getMessage() + "}");
		}
		return flag;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertARateCouponDic(ARateCouponDic aRateCouponDic) {

		int flag = 0;
		try {
			flag = aRateCouponDicDao.insert(aRateCouponDic);
			logger.info("fn:insertARateCouponDic,{新增保存成功}");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:insertARateCouponDic,{新增保存异常：" + e.getMessage() + "}");
		}
		return flag;
	}

}
