package com.power.platform.more.suggestion.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.more.suggestion.dao.SuggestionDao;
import com.power.platform.more.suggestion.entity.Suggestion;

@Service("suggestionService")
public class SuggestionService extends CrudService<Suggestion>  {
	
	@Resource
	private SuggestionDao suggestionDao;
	
	protected CrudDao<Suggestion> getEntityDao() {
		return suggestionDao;
	}

	/**
	 * 
	 * 方法: insertSuggestion <br>
	 * 描述: 新增. <br>
	 * 作者: Mr.Jia <br>
	 * 时间: 2016-05-23
	 * 
	 * @param Suggestion
	 * @return
	 */
	@Transactional(readOnly = false)
	public int insertSuggestion(Suggestion suggestion) {
		int flag = 0;
		try {
			flag = suggestionDao.insert(suggestion);
			logger.info("fn:insertSuggestion,{新增保存成功}");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:insertSuggestion,{新增保存异常：" + e.getMessage() + "}");
		}
		return flag;
	}
}
