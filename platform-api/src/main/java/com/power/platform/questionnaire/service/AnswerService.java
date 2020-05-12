package com.power.platform.questionnaire.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.questionnaire.dao.AnswerDao;
import com.power.platform.questionnaire.entity.Answer;

/**
 * 
 * 类: AnswerService <br>
 * 描述: 答案Service. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年4月18日 下午4:05:53
 */
@Service("answerService")
public class AnswerService extends CrudService<Answer> {

	@Resource
	private AnswerDao answerDao;

	@Override
	protected CrudDao<Answer> getEntityDao() {

		return answerDao;
	}

}