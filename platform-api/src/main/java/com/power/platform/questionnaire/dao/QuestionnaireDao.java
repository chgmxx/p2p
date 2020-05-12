package com.power.platform.questionnaire.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.questionnaire.entity.Questionnaire;

/**
 * 
 * 类: QuestionnaireDao <br>
 * 描述: 问卷DAO接口. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年4月18日 下午3:00:49
 */
@MyBatisDao
public interface QuestionnaireDao extends CrudDao<Questionnaire> {

}