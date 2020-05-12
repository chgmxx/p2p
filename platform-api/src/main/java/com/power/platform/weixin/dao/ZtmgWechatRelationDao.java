package com.power.platform.weixin.dao;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.weixin.entity.ZtmgWechatRelation;

/**
 * 
 * 类: ZtmgWechatRelationDao <br>
 * 描述: 中投摩根，客户资料与微信资料关系建立DAO接口. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年6月7日 上午10:51:40
 */
@MyBatisDao
public interface ZtmgWechatRelationDao extends CrudDao<ZtmgWechatRelation> {

	/**
	 * 
	 * 方法: findByOpenId <br>
	 * 描述: 根据openId查询微信关系是否建立. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年6月7日 下午2:46:19
	 * 
	 * @param openId
	 * @return
	 */
	public abstract ZtmgWechatRelation findByOpenId(@Param("openId") String openId);

}