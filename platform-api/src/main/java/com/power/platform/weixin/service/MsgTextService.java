package com.power.platform.weixin.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.weixin.api.process.MsgType;
import com.power.platform.weixin.dao.MsgTextDao;
import com.power.platform.weixin.entity.MsgBase;
import com.power.platform.weixin.entity.MsgText;

@Transactional(readOnly = false)
@Service("msgTextService")
public class MsgTextService extends CrudService<MsgText> {
	
	@Autowired
	private MsgTextDao msgTextDao;
	
	@Autowired
	private MsgBaseService msgBaseService;
	
	protected CrudDao<MsgText> getEntityDao() {
		return msgTextDao;
	}
	 
	public boolean saveMsgTextAndBase(MsgText msgText) {
		
		if(msgText.getId()==null || msgText.getId().equals("")){
			MsgBase base = new MsgBase();
			base.setInputCode(msgText.getMsgBase().getInputCode());
			base.setCreateDate(new Date());
			base.setMsgType(MsgType.Text.toString());
			msgBaseService.save(base);
			
			msgText.preInsert();
			msgText.setMsgBase(base);
			msgTextDao.insert(msgText);
		} else {
			MsgBase base = msgBaseService.get(msgText.getMsgBase().getId());
			base.setInputCode(msgText.getMsgBase().getInputCode());
			msgBaseService.save(base);
			msgText.setMsgBase(base);
			msgTextDao.update(msgText);
		}
		
		return true;
	}

}

