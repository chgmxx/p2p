package com.power.platform.regular.service;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.entity.WloanSubject;

/**
 * 
 * 类: WloanSubjectService <br>
 * 描述: 融资主体Service. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2015年12月29日 上午11:33:59
 */
@Service("wloanSubjectService")
@Transactional(readOnly = true)
public class WloanSubjectService extends CrudService<WloanSubject> {

	/**
	 * 0：受托支付标识，否.
	 */
	public static final String IS_ENTRUSTED_PAY_0 = "0";
	/**
	 * 1：受托支付标识，是.
	 */
	public static final String IS_ENTRUSTED_PAY_1 = "1";
	/**
	 * 账户对公对私标识，1：对公.
	 */
	public static final String CASHIER_BANK_NO_FLAG_1 = "1";
	/**
	 * 账户对公对私标识，2：对私.
	 */
	public static final String CASHIER_BANK_NO_FLAG_2 = "2";

	/**
	 * 融资主体类别，1：个人.
	 */
	public static final String WLOAN_SUBJECT_TYPE_1 = "1";
	/**
	 * 融资主体类别，2：企业.
	 */
	public static final String WLOAN_SUBJECT_TYPE_2 = "2";
	/**
	 * 爱亲融资主体ID.
	 */
	public static final String AIQIN_SUBJECT_ID = "57866d55093343e690faa91f32ed4d90";

	private static final Logger logger = Logger.getLogger(WloanSubjectService.class);

	@Resource
	private WloanSubjectDao wloanSubjectDao;

	@Override
	protected CrudDao<WloanSubject> getEntityDao() {

		logger.info("fn:getEntityDao,{获取当前(WloanSubjectDao)}");
		return wloanSubjectDao;
	}

	/**
	 * 
	 * 方法: isExistWloanSubjectAndWloanTermProject <br>
	 * 描述: 当前融资主体是否被融资项目使用，用于删除判断. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月11日 上午10:46:15
	 * 
	 * @param entity
	 * @return
	 */
	public List<WloanSubject> isExistWloanSubjectAndWloanTermProject(WloanSubject entity) {

		return wloanSubjectDao.isExistWloanSubjectAndWloanTermProject(entity);
	}

	/**
	 * 
	 * 方法: updateWloanSubject <br>
	 * 描述: 修改保存. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月29日 下午2:09:34
	 * 
	 * @param wloanSubject
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateWloanSubject(WloanSubject wloanSubject) {

		int flag = 0;
		try {
			flag = wloanSubjectDao.update(wloanSubject);
			logger.info("fn:updateWloanSubject,{修改保存成功}");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:updateWloanSubject,{修改保存异常：" + e.getMessage() + "}");
		}
		return flag;
	}

	/**
	 * 
	 * 方法: insertWloanSubject <br>
	 * 描述: 新增保存. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月29日 下午2:08:44
	 * 
	 * @param wloanSubject
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertWloanSubject(WloanSubject wloanSubject) {

		int flag = 0;
		try {
			flag = wloanSubjectDao.insert(wloanSubject);
			logger.info("fn:insertWloanSubject,{新增保存成功}");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:insertWloanSubject,{新增保存异常：" + e.getMessage() + "}");
		}
		return flag;
	}

	// 供应商销户
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void deleteWloanSubjectByUserId(String userId) {

		wloanSubjectDao.deleteWloanSubjectByUserId(userId);

	}
	// 中登网登记查询出质人信息
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public WloanSubject getSubject(String subjectId) {
		
		return wloanSubjectDao.getSubject(subjectId);
	}


}