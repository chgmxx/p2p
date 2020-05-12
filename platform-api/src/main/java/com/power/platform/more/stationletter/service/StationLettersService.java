package com.power.platform.more.stationletter.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.more.stationletter.dao.StationLetterDao;
import com.power.platform.more.stationletter.entity.StationLetter;

@Service("stationLettersService")
public class StationLettersService extends CrudService<StationLetter> {

	public static final String LETTER_TYPE_REGIST = "0"; // 站内信类型 - 注册
	public static final String LETTER_TYPE_WLOAN = "1"; // 站内信类型 - 投资
	public static final String LETTER_TYPE_REPAY = "2"; // 站内信类型 - 还款
	public static final String LETTER_TYPE_RECHAGE = "3"; // 站内信类型 - 充值
	public static final String LETTER_TYPE_CASH = "4"; // 站内信类型 - 提现
	

	public static final String LETTER_STATE_UNREAD = "1"; // 站内信是否已读 - 未读
	public static final String LETTER_STATE_READ = "2"; // 站内信是否已读 - 已读

	@Resource
	private StationLetterDao stationLetterDao;

	protected CrudDao<StationLetter> getEntityDao() {

		return stationLetterDao;
	}

	/**
	 * 
	 * 方法: insertStationLetter <br>
	 * 描述: 新增站内信. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年2月6日 下午12:23:20
	 * 
	 * @param entity
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertStationLetter(StationLetter entity) {

		return stationLetterDao.insert(entity);
	}

	/**
	 * 修改用户站内信为已读
	 * 
	 * @param list
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateByUserId(List<StationLetter> list) {

		int result = 0;
		for (int i = 0; i < list.size(); i++) {
			StationLetter letter = new StationLetter();
			letter = list.get(i);
			letter.setState(LETTER_STATE_READ);
			stationLetterDao.update(letter);
			result++;
		}
		return result;
	}

}
