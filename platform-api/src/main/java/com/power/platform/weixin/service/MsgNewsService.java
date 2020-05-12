package com.power.platform.weixin.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.weixin.api.process.MsgType;
import com.power.platform.weixin.dao.MsgNewsDao;
import com.power.platform.weixin.entity.MsgBase;
import com.power.platform.weixin.entity.MsgNews;

@Transactional(readOnly = false)
@Service("msgNewsService")
public class MsgNewsService extends CrudService<MsgNews> {
	
	@Autowired
	private MsgNewsDao msgNewsDao;
	
	@Autowired
	private MsgBaseService msgBaseService;
	
	protected CrudDao<MsgNews> getEntityDao() {
		return msgNewsDao;
	}

	public boolean saveMsgNewsAndBase(MsgNews msgNews) {
		
		if(msgNews.getId()==null || msgNews.getId().equals("")){
			MsgBase base = new MsgBase();
			base.setInputCode(msgNews.getMsgBase().getInputCode());
			base.setCreateDate(new Date());
			base.setMsgType(MsgType.Text.toString());
			msgBaseService.save(base);
			msgNews.preInsert();
			msgNews.setMsgBase(base);
			msgNewsDao.insert(msgNews);
		} else {
			MsgBase base = msgBaseService.get(msgNews.getMsgBase().getId());
			base.setInputCode(msgNews.getMsgBase().getInputCode());
			msgBaseService.save(base);
			msgNews.setMsgBase(base);
			msgNewsDao.update(msgNews);
		}
		
		return true;
	}


}

