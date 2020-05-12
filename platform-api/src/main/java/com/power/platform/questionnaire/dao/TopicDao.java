package com.power.platform.questionnaire.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.questionnaire.entity.QuestionnaireToTopic;
import com.power.platform.questionnaire.entity.Topic;

/**
 * 
 * 类: TopicDao <br>
 * 描述: 题目DAO接口. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年4月18日 下午3:59:51
 */
@MyBatisDao
public interface TopicDao extends CrudDao<Topic> {

	List<Topic> findAll(@Param("questionnaireId") String questionnaireId);

	/**
	 * 
	 * 方法: deleteQuestionnaireTopic <br>
	 * 描述: 移除问卷题目关系. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月19日 下午2:02:57
	 * 
	 * @param topic
	 * @return
	 */
	int deleteQuestionnaireTopic(Topic topic);

	/**
	 * 
	 * 方法: insertQuestionnaireTopic <br>
	 * 描述: 新增问卷题目. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月19日 下午1:32:35
	 * 
	 * @param model
	 * @return
	 */
	int insertQuestionnaireTopic(QuestionnaireToTopic model);

	/**
	 * 
	 * 方法: findTopicAssignList <br>
	 * 描述: 已选题目. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月19日 上午10:39:52
	 * 
	 * @param questionnaireId
	 * @return
	 */
	List<Topic> findTopicAssignList(String questionnaireId);

	/**
	 * 
	 * 方法: findTopicTreeList <br>
	 * 描述: 待选题目. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月20日 上午9:19:48
	 * 
	 * @param questionnaireId
	 * @return
	 */
	List<Topic> findTopicTreeList(String questionnaireId);

}