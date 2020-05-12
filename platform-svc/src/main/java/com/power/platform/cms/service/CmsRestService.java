package com.power.platform.cms.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.power.platform.cms.dao.LeaveMessageDao;
import com.power.platform.cms.dao.NoticeDao;
import com.power.platform.cms.entity.LeaveMessage;
import com.power.platform.cms.entity.Notice;
import com.power.platform.cms.pojo.CmsPojo;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.IdGen;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;

/**
 * 
 * 类: CmsRestService <br>
 * 描述: 内容管理rest service. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月24日 下午4:01:31
 */
@Component
@Path("/cms")
@Service("cmsRestService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CmsRestService {

	private static final Logger LOG = LoggerFactory.getLogger(CmsRestService.class);

	@Autowired
	private NoticeService noticeService;
	@Resource
	private NoticeDao noticeDao;
	@Resource
	private CreditUserInfoService creditUserInfoService;
	
	@Resource
	private  LeaveMessageDao leaveMessageDao;

	@POST
	@Path("/getCmsListByType")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getCmsListByType(@FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize, @FormParam("from") String from, @FormParam("type") String type) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize) || StringUtils.isBlank(from) || StringUtils.isBlank(type)) {
			LOG.info("fn:getCmsListByType,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		try {

			/**
			 * 数据域.
			 */
			Map<String, Object> data = new HashMap<String, Object>();

			Page<Notice> page = new Page<Notice>();
			Notice notice = new Notice();
			notice.setType(Integer.valueOf(type));
			// 判断是否是请求banner，如果是之请求状态为上线状态的banner
			if (type.equals("1") || type.equals("0")) {
				notice.setState(1);
			}
			// 分页信息.
			page.setPageNo(Integer.valueOf(pageNo));
			page.setPageSize(Integer.valueOf(pageSize));
			Page<Notice> pageResult = noticeService.findPage(page, notice);
			List<CmsPojo> cmsList = new ArrayList<CmsPojo>();
			if (null != pageResult) {
				List<Notice> list = pageResult.getList();
				CmsPojo cmsPojo = null;
				for (Notice model : list) {
					cmsPojo = new CmsPojo();
					cmsPojo.setId(model.getId());
					cmsPojo.setSources(model.getSources());
					cmsPojo.setSourcesDate(model.getSourcesDate());
					cmsPojo.setCreateDate(model.getCreateDate());
					cmsPojo.setTitle(model.getTitle());
					cmsPojo.setHead(model.getHead());
					cmsPojo.setText(StringEscapeUtils.unescapeHtml4(model.getText()));
					cmsPojo.setState(model.getState());
					cmsPojo.setType(model.getType());
					if (null == model.getLogopath()) {
						cmsPojo.setImgPath("");
					} else {
						cmsPojo.setImgPath(Global.getConfig("img_new_path") + model.getLogopath());
					}
					cmsList.add(cmsPojo);
				}
				data.put("pageNo", pageNo);
				data.put("pageSize", pageSize);
				data.put("totalCount", pageResult.getCount());
				data.put("pageCount", pageResult.getLast());
				data.put("cmsList", cmsList);
			}

			LOG.info("fn:getCmsListByType,中投摩根内容列表响应成功了！");
			result.put("state", "0");
			result.put("message", "中投摩根内容列表响应成功了！");
			result.put("data", data);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:getCmsListByType,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

		return result;
	}

	/**
	 * 根据ID获取系统公告详细信息
	 * 
	 * @param from
	 * @param noticeId
	 * @return
	 */
	@POST
	@Path("/getCmsNoticeById")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getCmsNoticeById(@FormParam("from") String from, @FormParam("noticeId") String noticeId) {

		Map<String, Object> result = new HashMap<String, Object>();

		if (StringUtils.isBlank(noticeId) || StringUtils.isBlank(from)) {
			LOG.info("fn:getCmsNoticeById,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			return result;
		}

		try {
			Notice notice = noticeService.get(noticeId);
			notice.setText(StringEscapeUtils.unescapeHtml4(notice.getText()));
			if (notice != null) {
				result.put("state", "0");
				result.put("message", "中投摩根内容列表响应成功了！");
				result.put("data", notice);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:getCmsListByType,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

		return result;
	}
	
	
	/**
	 * 获取上一篇
	 * @param from
	 * @param Id
	 * @return
	 */
	@POST
	@Path("/getEducationLast")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getEducationLast(@FormParam("from") String from, @FormParam("orderSum") String orderSum) {

		Map<String, Object> result = new HashMap<String, Object>();

		if("null".equals(orderSum)||orderSum==null) {
			orderSum = "3";
		}
		if (StringUtils.isBlank(orderSum) || StringUtils.isBlank(from)) {
			LOG.info("fn:getCmsNoticeById,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			return result;
		}

		try {
			Integer order = Integer.valueOf(orderSum) - 1; 
			Notice notice = noticeService.getArticle(order.toString());
			if (notice != null) {
				notice.setText(StringEscapeUtils.unescapeHtml4(notice.getText()));
				result.put("state", "0");
				result.put("message", "中投摩根内容列表响应成功了！");
				result.put("data", notice);
			}else{
				result.put("state", "3");
				result.put("message", "此为首篇");
				result.put("data", notice);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:getCmsListByType,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

		return result;
	}
	
	/**
	 * 获取下一篇
	 * @param from
	 * @param Id
	 * @return
	 */
	@POST
	@Path("/getEducationNext")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getEducationNext(@FormParam("from") String from, @FormParam("orderSum") String orderSum) {

		Map<String, Object> result = new HashMap<String, Object>();
		if("null".equals(orderSum)||orderSum==null) {
			orderSum = "3";
		}
		if (StringUtils.isBlank(orderSum) || StringUtils.isBlank(from)) {
			LOG.info("fn:getCmsNoticeById,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			return result;
		}
		try {
			Integer order = Integer.valueOf(orderSum) + 1; 
			Notice notice = noticeService.getArticle(order.toString());
			if (notice != null) {
				notice.setText(StringEscapeUtils.unescapeHtml4(notice.getText()));
				result.put("state", "0");
				result.put("message", "中投摩根内容列表响应成功了！");
				result.put("data", notice);
			}else{
				result.put("state", "3");
				result.put("message", "此为末篇");
				result.put("data", notice);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:getCmsListByType,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

		return result;
	}
	
	
	/**
	 * 获取所有活动数据
	 * @param pageNo
	 * @param pageSize
	 * @param from
	 * @param type
	 * @return
	 */
	@POST
	@Path("/getCmsList")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getCmsList(@FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize, @FormParam("from") String from, @FormParam("type") String type) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize) || StringUtils.isBlank(from) || StringUtils.isBlank(type)) {
			LOG.info("fn:getCmsListByType,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		try {

			/**
			 * 数据域.
			 */
			Map<String, Object> data = new HashMap<String, Object>();

			Page<Notice> page = new Page<Notice>();
			Notice notice = new Notice();
//			notice.setType(Integer.valueOf(type));
			// 判断是否是请求banner，如果是之请求状态为上线状态的banner
			//if (type.equals("1") || type.equals("0")) {
			//	notice.setState(1);
			//}
			notice.setBannerType("0");//0-活动
			// 分页信息.
			page.setPageNo(Integer.valueOf(pageNo));
			page.setPageSize(Integer.valueOf(pageSize));
			Page<Notice> pageResult = noticeService.findPage(page, notice);
			List<CmsPojo> cmsList = new ArrayList<CmsPojo>();
			if (null != pageResult) {
				List<Notice> list = pageResult.getList();
				CmsPojo cmsPojo = null;
				for (Notice model : list) {
					cmsPojo = new CmsPojo();
					cmsPojo.setId(model.getId());
					cmsPojo.setSources(model.getSources());
					cmsPojo.setSourcesDate(model.getCreateDate());
					cmsPojo.setTitle(model.getTitle());
					cmsPojo.setHead(model.getHead());
					cmsPojo.setText(StringEscapeUtils.unescapeHtml4(model.getText()));
					cmsPojo.setState(model.getState());//0-下线  1-上线
					cmsPojo.setType(model.getType());
					if (null == model.getLogopath()) {
						cmsPojo.setImgPath("");
					} else {
						cmsPojo.setImgPath(Global.getConfig("img_new_path") + model.getLogopath());
					}
					cmsList.add(cmsPojo);
				}
				data.put("pageNo", pageNo);
				data.put("pageSize", pageSize);
				data.put("totalCount", pageResult.getCount());
				data.put("pageCount", pageResult.getLast());
				data.put("cmsList", cmsList);
			}

			LOG.info("fn:getCmsListByType,中投摩根内容列表响应成功了！");
			result.put("state", "0");
			result.put("message", "中投摩根内容列表响应成功了！");
			result.put("data", data);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:getCmsListByType,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

		return result;
	}
	
	
	
	/**
	 * 用户留言
	 * 
	 */
	@POST
	@Path("/leaveMessage")
	public Map<String, Object> leaveMessage(@FormParam("from") String from, @FormParam("name") String name, @FormParam("mobile") String mobile, @FormParam("bussinessName") String bussinessName, @FormParam("message") String message, @Context HttpServletRequest servletrequest){
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			// 判断必要参数是否为空
			if (StringUtils.isBlank(from) || StringUtils.isBlank(name) || StringUtils.isBlank(mobile)|| StringUtils.isBlank(bussinessName)|| StringUtils.isBlank(message)) {
				result.put("state", "2");
				result.put("message", "缺少必要参数");
				return result;
			}
			LeaveMessage leaveMessage = new LeaveMessage();
			leaveMessage.setId(String.valueOf(IdGen.randomLong()));
			leaveMessage.setName(name);
			leaveMessage.setMobile(mobile);
			leaveMessage.setBussinessName(bussinessName);
			leaveMessage.setMessage(message);
			Date date  = new Date();
			leaveMessage.setDate(date);
			int a = leaveMessageDao.insert(leaveMessage);
			System.out.println(a);
			result.put("state", "1");
			result.put("message", "留言成功");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "3");
			result.put("message", "系统异常");
			return result;
		}
		
	}
	
	
	/**
	 * 出借人教育
	 * @param pageNo
	 * @param pageSize
	 * @param from
	 * @param label
	 * @return
	 */
	@POST
	@Path("/getEducationListByType")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getEducationListByType(@FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize, @FormParam("from") String from, @FormParam("label") String label) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize) || StringUtils.isBlank(from) ) {
			LOG.info("fn:getEducationListByType,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		try {

			/**
			 * 数据域.
			 */
			Map<String, Object> data = new HashMap<String, Object>();

			Page<Notice> page = new Page<Notice>();
			Notice notice = new Notice();
			notice.setType(7);
			if(!StringUtils.isBlank(label)){
				notice.setBannerType(label);//标签
			}
			// 判断是否是请求banner，如果是之请求状态为上线状态的banner
			
				notice.setState(1);
			// 分页信息.
			page.setPageNo(Integer.valueOf(pageNo));
			page.setPageSize(Integer.valueOf(pageSize));
			Page<Notice> pageResult = noticeService.findPage(page, notice);
			List<CmsPojo> cmsList = new ArrayList<CmsPojo>();
			if (null != pageResult) {
				List<Notice> list = pageResult.getList();
				CmsPojo cmsPojo = null;
				for (Notice model : list) {
					cmsPojo = new CmsPojo();
					cmsPojo.setId(model.getId());
					cmsPojo.setSources(model.getSources());
					cmsPojo.setSourcesDate(model.getSourcesDate());
					cmsPojo.setTitle(model.getTitle());
					cmsPojo.setHead(model.getHead());
					cmsPojo.setText(StringEscapeUtils.unescapeHtml4(model.getText()));
					cmsPojo.setState(model.getState());
					cmsPojo.setType(model.getType());
					cmsPojo.setLabel(model.getBannerType());
					if (null == model.getLogopath()) {
						cmsPojo.setImgPath("");
					} else {
						cmsPojo.setImgPath(Global.getConfig("img_new_path") + model.getLogopath());
					}
					cmsPojo.setOrderSum(model.getOrderSum());
					cmsList.add(cmsPojo);
				}
				data.put("pageNo", pageNo);
				data.put("pageSize", pageSize);
				data.put("totalCount", pageResult.getCount());
				data.put("pageCount", pageResult.getLast());
				data.put("cmsList", cmsList);
			}

			LOG.info("fn:getEducationListByType,中投摩根出借人教育列表响应成功了！");
			result.put("state", "0");
			result.put("message", "中投摩根出借人教育列表响应成功了！");
			result.put("data", data);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:getEducationListByType,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}
		return result;
	}
	
	/**
	 * 出借端 ---核心企业列表
	 * @param pageNo
	 * @param pageSize
	 * @param from
	 * @return
	 */
	@POST
	@Path("/getMiddlemenList")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getMiddlemenList(@FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize, @FormParam("from") String from) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize) || StringUtils.isBlank(from)) {
			LOG.info("fn:getCmsListByType,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		try {

			/**
			 * 数据域.
			 */
			Map<String, Object> data = new HashMap<String, Object>();

			CreditUserInfo creditUser = new CreditUserInfo();
			Page<CreditUserInfo> page = new Page<CreditUserInfo>();
			page.setPageNo(Integer.valueOf(pageNo));
			page.setPageSize(Integer.valueOf(pageSize));
            Page<CreditUserInfo> pageResult = creditUserInfoService.findPageByAnnexFile(page,creditUser);
            List<CreditUserInfo> creditUserList = new ArrayList<CreditUserInfo>();
			if (null != pageResult) {
				List<CreditUserInfo> list = pageResult.getList();
				for (CreditUserInfo creditUserInfo : list) {
				     CreditUserInfo user = new CreditUserInfo();
				     CreditAnnexFile creditFile = new CreditAnnexFile();
				     String annexFileUrl[] = creditUserInfo.getAnnexFile().getUrl().split("\\|");
				     if(annexFileUrl.length<2){
				    	 creditFile.setUrl(Global.getConfig("img_new_path") + creditUserInfo.getAnnexFile().getUrl());
				     }else{
				    	 creditFile.setUrl(Global.getConfig("img_new_path") + creditUserInfo.getAnnexFile().getUrl().split("\\|")[1]);
				     }
				     
				     creditFile.setRemark(creditUserInfo.getAnnexFile().getRemark()+"?middlemenId="+creditUserInfo.getId());
				     user.setAnnexFile(creditFile);
				     user.setEnterpriseFullName(creditUserInfo.getEnterpriseFullName());
					 creditUserList.add(user);
				}
				data.put("pageNo", pageNo);
				data.put("pageSize", pageSize);
				data.put("totalCount", pageResult.getCount());
				data.put("pageCount", pageResult.getLast());
				data.put("middlemenList", creditUserList);
			}

			LOG.info("fn:getCmsListByType,中投摩根内容列表响应成功了！");
			result.put("state", "0");
			result.put("message", "中投摩根内容列表响应成功了！");
			result.put("data", data);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:getCmsListByType,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

		return result;
	}
	
}
