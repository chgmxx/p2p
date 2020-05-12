package com.power.platform.cms.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.cms.entity.Notice;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;

/**
 * 公告接口
 * 
 * @author lc
 *
 */
@MyBatisDao
public interface NoticeDao extends CrudDao<Notice> {

	public void updateNoticeStatus(Notice notice);

	public List<Notice> findNoticeByTypeAndTop(@Param("type") Integer type, @Param("top") Integer top);

	public List<Notice> findNoticeByOrdersum(@Param("ordersum") Integer ordersum);

	public Notice getArticle(@Param("ordersum") String id);

}
