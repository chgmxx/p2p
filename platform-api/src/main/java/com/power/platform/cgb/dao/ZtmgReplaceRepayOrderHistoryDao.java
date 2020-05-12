/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.cgb.dao;

import java.util.List;

import com.power.platform.cgb.entity.ZtmgReplaceRepayOrderHistory;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;

/**
 * 
 * class: ZtmgReplaceRepayOrderHistoryDao <br>
 * description: 代偿还款订单历史DAO接口. <br>
 * author: Roy <br>
 * date: 2019年8月1日 下午6:20:27
 */
@MyBatisDao
public interface ZtmgReplaceRepayOrderHistoryDao extends CrudDao<ZtmgReplaceRepayOrderHistory> {

	List<ZtmgReplaceRepayOrderHistory> findReplaceRepayOrderListByProSnAndRepayDate(ZtmgReplaceRepayOrderHistory zrroh);
}