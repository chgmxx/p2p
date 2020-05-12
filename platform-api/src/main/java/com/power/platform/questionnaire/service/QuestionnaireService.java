package com.power.platform.questionnaire.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.questionnaire.dao.QuestionnaireDao;
import com.power.platform.questionnaire.entity.Questionnaire;

/**
 * 
 * 类: QuestionnaireService <br>
 * 描述: 问卷Service. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年4月18日 下午3:00:10
 */
@Service("questionnaireService")
public class QuestionnaireService extends CrudService<Questionnaire> {

	@Resource
	private QuestionnaireDao questionnaireDao;

	@Override
	protected CrudDao<Questionnaire> getEntityDao() {

		return questionnaireDao;
	}

}