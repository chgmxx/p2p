/**
 * 银行托管-银行卡-DAO接口.
 */
package com.power.platform.cgb.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;

/**
 * 银行托管-银行卡-DAO接口.
 * 
 * @author lance
 * @version 2017-10-26
 */
@MyBatisDao
public interface CgbUserBankCardDao extends CrudDao<CgbUserBankCard> {

	/**
	 * 
	 * 方法: getUserBankCardByCreditUserIdAndState <br>
	 * 描述: 获取借款人开户银行卡信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年6月22日 下午1:33:13
	 * 
	 * @param cgbUserBankCard
	 * @return
	 */
	public CgbUserBankCard getUserBankCardByCreditUserIdAndState(CgbUserBankCard cgbUserBankCard);

	/**
	 * 
	 * 方法: physicallyDeleted <br>
	 * 描述: 物理删除客户银行卡信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月24日 下午5:21:30
	 * 
	 * @param entity
	 * @return
	 */
	public int physicallyDeleted(CgbUserBankCard entity);

	/**
	 * 用户银行卡信息查询
	 * 
	 * @param orderId
	 * @return
	 */
	public CgbUserBankCard getInfoById(@Param("id") String orderId);

	/**
	 * 
	 * 方法: getUserBankCardByUserId <br>
	 * 描述: 获取客户银行卡信息，根据客户ID. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月21日 下午2:48:30
	 * 
	 * @param userId
	 * @return
	 */
	public CgbUserBankCard getUserBankCardByUserId(@Param("userId") String userId);

	public List<CgbUserBankCard> findCreditList(CgbUserBankCard entity);

	public CgbUserBankCard getUserBankCardByUserId1(@Param("userId")String userId);

	public CgbUserBankCard getUserBankCardByUserId2(@Param("userId")String userId);
	
	public List<CgbUserBankCard> findState0(@Param("createDate")String createDate);
	
	public Integer updateState2(@Param("id")String id);
	
	//供应商销户
	public void deleteBankByUserId(@Param("delUserId")String delUserId,@Param("userId")String userId);
}