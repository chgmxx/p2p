package com.power.platform.questionnaire.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.questionnaire.entity.Answer;
import com.power.platform.questionnaire.entity.TopicToAnswer;

/**
 * 
 * 类: AnswerDao <br>
 * 描述: 答案DAO接口. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年4月18日 下午4:06:29
 */
@MyBatisDao
public interface AnswerDao extends CrudDao<Answer> {

	List<Answer> findAll(@Param("topicId") String topicId);

	/**
	 * 
	 * 方法: deleteTopicAnswer <br>
	 * 描述: 移除题目与答案的关联关系. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月19日 下午2:41:02
	 * 
	 * @param answer
	 * @return
	 */
	int deleteTopicAnswer(Answer answer);

	/**
	 * 
	 * 方法: insertTopicToAnswer <br>
	 * 描述: 新增题目答案. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月19日 下午2:44:52
	 * 
	 * @param model
	 * @return
	 */
	int insertTopicAnswer(TopicToAnswer model);

	/**
	 * 
	 * 方法: findAnswerTreeList <br>
	 * 描述: 待选答案列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月19日 下午3:04:23
	 * 
	 * @return
	 */
	List<Answer> findAnswerTreeList(String topicId);

	/**
	 * 
	 * 方法: findAnswerAssignList <br>
	 * 描述: 已选答案列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月19日 下午2:50:35
	 * 
	 * @param topicId
	 * @return
	 */
	List<Answer> findAnswerAssignList(String topicId);

}