package com.power.platform.questionnaire.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.questionnaire.dao.QuestionUserDao;
import com.power.platform.questionnaire.entity.QuestionUser;


/**
 * 客户问卷Service
 * @author nice
 * @version 2017-04-19
 */
@Service("questionUserService")
@Transactional(readOnly = false)
public class QuestionUserService extends CrudService<QuestionUser> {

	@Resource
	private QuestionUserDao questionUserDao;
	
	public QuestionUser get(String id) {
		return super.get(id);
	}
	
	public List<QuestionUser> findList(QuestionUser questionUser) {
		return super.findList(questionUser);
	}
	
	public Page<QuestionUser> findPage(Page<QuestionUser> page, QuestionUser questionUser) {
		return super.findPage(page, questionUser);
	}
	
	@Transactional(readOnly = false)
	public void save(QuestionUser questionUser) {
		super.save(questionUser);
	}
	
	@Transactional(readOnly = false)
	public void delete(QuestionUser questionUser) {
		super.delete(questionUser);
	}

	@Override
	protected CrudDao<QuestionUser> getEntityDao() {
		// TODO Auto-generated method stub
		return questionUserDao;
	}
	
}