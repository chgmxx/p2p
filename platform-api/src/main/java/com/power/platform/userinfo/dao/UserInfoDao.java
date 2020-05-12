/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.userinfo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 用户信息管理DAO接口
 * 
 * @author jiajunfeng
 * @version 2015-12-16
 */
@MyBatisDao
public interface UserInfoDao extends CrudDao<UserInfo> {

	/**
	 * 
	 * methods: findUserListByRegisterDate <br>
	 * description: 2019年，发送新年祝福短消息，用户集合. <br>
	 * author: Mr.Roy <br>
	 * date: 2019年1月21日 下午4:45:11
	 * 
	 * @param userInfo
	 * @return
	 */
	List<UserInfo> findUserListByRegisterDate(UserInfo userInfo);

	/**
	 * 
	 * 方法: getUserInfoById <br>
	 * 描述: 获取单条数据. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年11月7日 下午1:53:17
	 * 
	 * @param id
	 * @return
	 */
	public UserInfo getUserInfoById(String id);

	/**
	 * 
	 * 方法: findStatisticalAllList <br>
	 * 描述: 平台统计注册用户. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年7月13日 上午11:12:20
	 * 
	 * @param wloanTermInvest
	 * @return
	 */
	public List<UserInfo> findStatisticalAllList(WloanTermInvest wloanTermInvest);

	/**
	 * 客户邀请人信息
	 * 
	 * @return
	 */
	public String PartnerForUserInfo(String userId);

	/**
	 * 为渠道用户查询注册人数
	 * 
	 * @param recommendUserId
	 * @return
	 */
	// public List<UserInfo> findListForRegist(String recommendUserId);
	public List<UserInfo> findListForRegist(UserInfo userInfo);

	/**
	 * 
	 * 方法: loginByGesturePwd <br>
	 * 描述: 手势密码是否登陆成功. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年5月23日 下午2:17:19
	 * 
	 * @param userInfo
	 * @return
	 */
	UserInfo loginByGesturePwd(UserInfo userInfo);

	/**
	 * 修改密码
	 * 
	 * @param userInfo
	 */
	void updateUser(UserInfo userInfo);

	/**
	 * 根据name修改userInfo
	 * 
	 * @param userInfo
	 */
	void updateByName(UserInfo userInfo);

	/**
	 * 
	 * 方法: getUserInfoByPhone <br>
	 * 描述: 检查手机注册，根据手机号码查询客户信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年4月27日 上午9:40:31
	 * 
	 * @param phone
	 * @return
	 */
	UserInfo getUserInfoByPhone(@Param("phone") String phone);

	public void updateUserPhone(UserInfo userInfo);

	public void updateUserPwd(UserInfo userInfo);

	/**
	 * 紧急联系人
	 * 
	 * @param userInfo
	 */
	public void updateEmergency(UserInfo userInfo);

	public void updateAddress(UserInfo userInfo);

	public void updateEmailInfo(UserInfo userInfo);

	public UserInfo getCgb(String id);

	/**
	 * 存管宝
	 * 
	 * @param userInfo
	 * @return
	 */
	public List<UserInfo> findList1(UserInfo userInfo);

	public List<UserInfo> findRecommendUser();

	public List<UserInfo> findLLUserList();

}