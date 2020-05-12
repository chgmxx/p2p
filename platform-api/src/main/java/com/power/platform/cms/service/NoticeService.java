package com.power.platform.cms.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.cms.dao.NoticeDao;
import com.power.platform.cms.entity.Notice;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;

/**
 * 公告Service
 * @author lc
 * @version 2015-12-16
 */
@Service("noticeService")
@Transactional(readOnly = false)
public class NoticeService extends CrudService<Notice>  {

	@Resource
	private NoticeDao noticeDao;
	
	protected CrudDao<Notice> getEntityDao() {
		return noticeDao;
	}
	
	public void updateNoticeStatus(Notice notice, Boolean isRe) {
		noticeDao.updateNoticeStatus(notice);
	}

	public List<Notice> findNoticeByTypeAndTop(Integer type, Integer top) {
		return noticeDao.findNoticeByTypeAndTop(type, top);
	}

	public Notice getArticle(String id) {
		// TODO Auto-generated method stub
		return noticeDao.getArticle(id);
	}
}
