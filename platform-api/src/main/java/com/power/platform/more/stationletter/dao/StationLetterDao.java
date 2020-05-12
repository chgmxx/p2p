package com.power.platform.more.stationletter.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.more.stationletter.entity.StationLetter;

/**
 * 站内信Dao
 * @author Mr.Jia
 * @version 2016-06-06
 */
@MyBatisDao
public interface StationLetterDao extends CrudDao<StationLetter> {
	
}