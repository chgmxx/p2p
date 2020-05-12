package com.power.platform.more.suggestion.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.more.suggestion.entity.Suggestion;

/**
 * 客户投资建议Dao
 * @author Mr.Jia
 * @version 2016-05-23
 */
@MyBatisDao
public interface SuggestionDao extends CrudDao<Suggestion> {
	
}