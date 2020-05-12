package com.power.platform.regular.service;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.regular.dao.WloanTermDocDao;
import com.power.platform.regular.entity.WloanTermDoc;

/**
 * 
 * 类: WloanTermDocService <br>
 * 描述: 定期融资档案Service interface. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2015年12月28日 下午5:38:37
 */
@Service("wloanTermDocService")
@Transactional(readOnly = true)
public class WloanTermDocService extends CrudService<WloanTermDoc> {

	/**
	 * 定期融资档案，资料类别.
	 */
	public static final String WLOAN_TERM_DOC_DIC_TYPE = "wloan_term_doc_type";

	private static final Logger logger = Logger.getLogger(WloanTermDocService.class);

	@Resource
	private WloanTermDocDao wloanTermDocDao;

	protected CrudDao<WloanTermDoc> getEntityDao() {

		logger.info("fn:getEntityDao,{获取当前(WloanTermDocDao)}");
		return wloanTermDocDao;
	}

	/**
	 * 
	 * 方法: isExistWloanTermDocAndWloanTermProject <br>
	 * 描述: 定期项目是否使用融资档案，用于删除的时候判断. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月11日 上午11:28:42
	 * 
	 * @param entity
	 * @return
	 */
	public List<WloanTermDoc> isExistWloanTermDocAndWloanTermProject(WloanTermDoc entity) {

		return wloanTermDocDao.isExistWloanTermDocAndWloanTermProject(entity);
	}

	/**
	 * 
	 * 方法: updateWloanTermDoc <br>
	 * 描述: 修改定期融资档案. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月29日 上午10:46:41
	 * 
	 * @param wloanTermDoc
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateWloanTermDoc(WloanTermDoc wloanTermDoc) {

		int flag = 0;
		try {
			flag = wloanTermDocDao.update(wloanTermDoc);
			logger.info("fn:updateWloanTermDoc,{修改定期融资档案成功}");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:updateWloanTermDoc,{修改定期融资档案异常：" + e.getMessage() + "}");
		}
		return flag;
	}

	/**
	 * 
	 * 方法: insertWloanTermDoc <br>
	 * 描述: 新增定期融资档案. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月28日 下午8:04:51
	 * 
	 * @param wloanTermDoc
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertWloanTermDoc(WloanTermDoc wloanTermDoc) {

		int flag = 0;
		try {
			flag = wloanTermDocDao.insert(wloanTermDoc);
			logger.info("fn:insertWloanTermDoc,{新增定期融资档案成功}");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:insertWloanTermDoc,{新增定期融资档案异常：" + e.getMessage() + "}");
		}
		return flag;
	}

}