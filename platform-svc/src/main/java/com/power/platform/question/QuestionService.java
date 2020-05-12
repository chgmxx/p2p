package com.power.platform.question;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.platform.cache.Cache;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.questionnaire.dao.AnswerDao;
import com.power.platform.questionnaire.dao.QuestionUserDao;
import com.power.platform.questionnaire.dao.QuestionnaireDao;
import com.power.platform.questionnaire.dao.TopicDao;
import com.power.platform.questionnaire.entity.Answer;
import com.power.platform.questionnaire.entity.QuestionUser;
import com.power.platform.questionnaire.entity.Questionnaire;
import com.power.platform.questionnaire.entity.Topic;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;





@Path("/question")
@Service("questionService")
@Produces(MediaType.APPLICATION_JSON)
public class QuestionService {
	
	private static final Logger LOG = LoggerFactory.getLogger(QuestionService.class);
	
	@Autowired
	private UserInfoDao userInfoDao;
	@Autowired
	private QuestionnaireDao questionnaireDao;
	@Autowired
	private TopicDao topicDao;
	@Autowired
	private AnswerDao answerDao;
	@Autowired
	private QuestionUserDao questionUserDao;
	@Autowired
	private UserInfoService userInfoService;
	
	/**
	 * 风险评测
	 * 
	 * @param from
	 * @return
	 */
	@POST
	@Path("/getQuestionList")
	public Map<String, Object> getQuestionList(@FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(token)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		/**
		 * 获取token.
		 */
		try {
			
			String jedisUserId = JedisUtils.get(token);
			
			
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoDao.get(jedisUserId);
				if(userInfo==null){
					userInfo = userInfoDao.getCgb(jedisUserId);
				}
				if (userInfo != null) {
					//查询问卷
					Questionnaire entity = new Questionnaire();
					entity.setState("1");
					List<Questionnaire> question = questionnaireDao.findList(entity);
					if(question!=null && question.size()>0){
						LOG.info("获取试卷成功");
						entity = question.get(0); 
						map.put("name", entity.getName());//问卷名称
						List<Topic> topicList = topicDao.findAll(entity.getId());
						if(topicList!=null && topicList.size()>0){
							LOG.info("获取题目成功");
							for (int i = 0; i < topicList.size(); i++) {
								Topic topic = topicList.get(i);
								int num = i+1;
								topic.setName(num+"."+topic.getName());
								List<Answer> answerList = answerDao.findAll(topic.getId());
								if(answerList!=null && answerList.size()>0){
									LOG.info("获取题目所有答案成功");
									topic.setAnswerList(answerList);
								}
							}
							map.put("topicList", topicList);
						}
						result.put("state", "0");
						result.put("message", "获取试卷成功");
						result.put("data", map);
					}else{
						result.put("state", "1");
						result.put("message", "获取试卷失败");
						result.put("data", map);
					}
				} else {
					result.put("state", "3");
					result.put("message", "系统异常");
				}
			}else{
				result.put("state", "4");
				result.put("message", "系统超时");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	
	/**
	 * 保存用户答案
	 * @param token
	 * @param answer
	 * @return
	 */
	@POST
	@Path("/saveUserAnswer")
	public Map<String, Object> saveUserAnswer(@FormParam("token") String token,@FormParam("answer") String answer) {

		Map<String, Object> result = new HashMap<String, Object>();
		// Map<String, Object> map = new HashMap<String, Object>();
		int score = 0;// 分值
		// 判断必要参数是否为空
		if (StringUtils.isBlank(token) || StringUtils.isBlank(answer)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		/**
		 * 获取token.
		 */
		try {
             String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoDao.get(jedisUserId);
				if(userInfo==null){
					userInfo = userInfoDao.getCgb(jedisUserId);
				}
				if (userInfo != null) {
					//N1.根据用户查询用户答案
					QuestionUser entity = new QuestionUser();
					entity.setUser(userInfo);
					List<QuestionUser> list = questionUserDao.findList(entity);
					//有数据先清空
					if(list!=null && list.size()>0){
						int i = questionUserDao.delete(entity);
						if(i>0){
							LOG.info("用户["+userInfo.getId()+"]答案已清空");
						}else{
							LOG.info("用户["+userInfo.getId()+"]答案清空失败");
						}
					}
					//N3.保存用户答案
					String[] answers = answer.split(",");
					for (int i = 0; i < answers.length; i++) {
						    String[] topic = answers[i].split("--");
							LOG.info("题目["+topic[0]+"] 答案["+topic[1]+"]");
							QuestionUser userAnswer = new QuestionUser();
							userAnswer.setId(IdGen.uuid());
							userAnswer.setUser(userInfo);
							userAnswer.setTopicId(topic[0]);
							userAnswer.setAnswerId(topic[1]);
							userAnswer.setCreateDate(new Date());
							userAnswer.setUpdateDate(new Date());
							int q = questionUserDao.insert(userAnswer);
							if(q>0){
								LOG.info("答案添加成功");
							}else{
								LOG.info("答案添加失败");
							}
							//查询答案分值
							Answer answerInfo = answerDao.get(topic[1]);
							if(answerInfo!=null){
								score = score + answerInfo.getScore();
							}
					}
					
					//判断用户风险类型
					String type = "";
					String riskType = "";
					String describe = "";
//					if(score>=10 && score < 20){
//						type = "保守型";
//					}else if(score>=20 && score < 28){
//						type = "成长型";
//					}else if(score>=28 && score < 36){
//						type = "稳重型";
//					}else if(score>=36 && score < 43){
//						type = "激进型";
//					}else if(score>=43){
//						type = "激进型";
//					}
					if (score >= 0 && score <= 15) {
						type = "保守型";
						describe = "承受风险能力较弱，关注项目的安全性远远超过项目利息。您可关注平台五星出借项目。";
						riskType = UserInfo.RISKTYPE5;
					} else if (score >= 16 && score <= 30) {
						type = "谨慎型";
						describe = "能承受较低的风险，对出借利息比较敏感，期望通过短期、持续、渐进的出借获得高于定期存款的回报。您可关注平台四星及以上出借项目。";
						riskType = UserInfo.RISKTYPE4;
					} else if (score >= 31 && score <= 60) {
						type = "稳健型";
						describe = "能够承受一定的风险，包括本金损失，对出借利息比较敏感，期望通过长期且持续的出借获得高于平均水平的回报。您可以关注平台三星及以上出借项目。";
						riskType = UserInfo.RISKTYPE3;
					} else if (score >= 61 && score <= 80) {
						type = "进取型";
						describe = "能够承受适度的中高风险，可以接受一定的利息波动及本金损失。您可以关注平台二星及以上出借项目。";
						riskType = UserInfo.RISKTYPE2;
					} else if (score >= 81) {
						type = "激进型";
						describe = "有较高风险承受能力，在出借利息波动较大及有可能发生较大本金损失的情况下，仍然保持积极进取的理念。您愿意从事风险与报酬都比较高的出借，满足出借经验丰富和可支配资金充裕条件，但仍请务必做好风险管理与资金调配工作。您可以关注平台各类出借项目。";
						riskType = UserInfo.RISKTYPE1;
					}
					userInfo.setRiskType(riskType);
					userInfoService.save(userInfo);
					result.put("state", "0");
					result.put("message", "提交问卷成功");
					result.put("score", type);
					result.put("describe", describe);
				} else {
					result.put("state", "1");
					result.put("message", "系统异常");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
