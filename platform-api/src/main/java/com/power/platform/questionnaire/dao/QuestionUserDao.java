package com.power.platform.questionnaire.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.questionnaire.entity.QuestionUser;


/**
 * 客户问卷DAO接口
 * @author nice
 * @version 2017-04-19
 */
@MyBatisDao
public interface QuestionUserDao extends CrudDao<QuestionUser> {
	
}