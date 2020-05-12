package com.power.platform.questionnaire.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.questionnaire.dao.TopicDao;
import com.power.platform.questionnaire.entity.Topic;

/**
 * 
 * 类: TopicService <br>
 * 描述: 题目Service. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年4月18日 下午3:58:40
 */
@Service("topicService")
public class TopicService extends CrudService<Topic> {

	@Resource
	private TopicDao topicDao;

	@Override
	protected CrudDao<Topic> getEntityDao() {

		return topicDao;
	}

}