package com.power.platform.lanmao.trade.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.cgb.dao.ZtmgUserAuthorizationDao;
import com.power.platform.cgb.entity.ZtmgUserAuthorization;
import com.power.platform.common.utils.IdGen;
import com.power.platform.lanmao.dao.LmTransactionDao;
import com.power.platform.lanmao.entity.LmTransaction;
import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.type.AuthEnum;
import com.power.platform.lanmao.type.BusinessStatusEnum;
import com.power.platform.lanmao.type.ServiceNameEnum;

/**
 * 
 * class: UserAuthorizationNotifyService <br>
 * description: 用户授权异步通知 <br>
 * author: Roy <br>
 * date: 2019年10月8日 上午9:25:47
 */
@Service("userAuthorizationNotifyService")
public class UserAuthorizationNotifyService {

	private final static Logger logger = LoggerFactory.getLogger(UserAuthorizationNotifyService.class);
	@Autowired
	private ZtmgUserAuthorizationDao ztmgUserAuthorizationDao;
	@Autowired
	private LmTransactionDao lmTransactionDao;

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public boolean userAuthorizationNotify(NotifyVo input) {

		boolean flag = false;
		try {
			// 业务数据报文，JSON格式，具体见各接口定义
			JSONObject jsonObject = JSONObject.parseObject(input.getRespData());
			String platformUserNo = jsonObject.getString("platformUserNo"); // 平台用户编号
			String requestNo = jsonObject.getString("requestNo"); // 请求流水号
			String code = jsonObject.getString("code");
			String status = jsonObject.getString("status"); // 业务处理状态（处理失败INIT；处理成功SUCCESS），平台可根据非SUCCESS状态做相应处理，处理失败时可参考错误码及描述
			String errorCode = jsonObject.getString("errorCode");
			String errorMessage = jsonObject.getString("errorMessage");
			String authList = jsonObject.getString("authList");
			String amount = jsonObject.getString("amount");
			String failTime = jsonObject.getString("failTime");
			// --
			LmTransaction lmTransaction = null;
			if (BusinessStatusEnum.SUCCESS.getValue().equals(status)) {
				logger.info("用户平台编号:{}，授权列表:{}", platformUserNo, authList);

				ZtmgUserAuthorization zua = new ZtmgUserAuthorization();
				zua.setUserId(platformUserNo);
				zua.setMerchantId(input.getPlatformNo());
				zua.setSignature(input.getSign());
				zua.setGrantAmountList(amount);
				zua.setGrantTimeList(failTime);
				boolean ztmgUserAuthorization = ztmgUserAuthorization(zua);
				if (ztmgUserAuthorization) {
					logger.info("用户授权同步回调成功 ......");
				}
				// 懒猫交易留存
				lmTransaction = new LmTransaction();
				lmTransaction.setId(IdGen.uuid());
				lmTransaction.setRequestNo(requestNo);
				lmTransaction.setServiceName(ServiceNameEnum.USER_AUTHORIZATION.getValue());
				lmTransaction.setPlatformUserNo(platformUserNo);
				lmTransaction.setCreateDate(new Date());
				lmTransaction.setUpdateDate(new Date());
				lmTransaction.setCode(code);
				lmTransaction.setStatus(status);
				int lmTransactionFlag = lmTransactionDao.insert(lmTransaction);
				logger.debug("懒猫交易留存，插入:{}", lmTransactionFlag == 1 ? "成功" : "失败");
			} else {
				logger.info("用户授权同步回调失败 ......");
				// 懒猫交易留存
				lmTransaction = new LmTransaction();
				lmTransaction.setId(IdGen.uuid());
				lmTransaction.setRequestNo(requestNo);
				lmTransaction.setServiceName(ServiceNameEnum.USER_AUTHORIZATION.getValue());
				lmTransaction.setPlatformUserNo(platformUserNo);
				lmTransaction.setCreateDate(new Date());
				lmTransaction.setUpdateDate(new Date());
				lmTransaction.setCode(code);
				lmTransaction.setStatus(status);
				lmTransaction.setErrorCode(errorCode);
				lmTransaction.setErrorMessage(errorMessage);
				int lmTransactionFlag = lmTransactionDao.insert(lmTransaction);
				logger.debug("懒猫交易留存，插入:{}", lmTransactionFlag == 1 ? "成功" : "失败");
			}
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public boolean ztmgUserAuthorization(ZtmgUserAuthorization zua) {

		boolean flag = false;
		try {
			// --
			String platformUserNo = zua.getUserId();
			String platformNo = zua.getMerchantId();
			String sign = zua.getSignature();
			String amount = zua.getGrantAmountList();
			String failTime = zua.getGrantTimeList();
			// --
			ZtmgUserAuthorization entity = new ZtmgUserAuthorization();
			entity.setUserId(platformUserNo);
			List<ZtmgUserAuthorization> list = ztmgUserAuthorizationDao.findList(entity);
			if (null != list && list.size() > 0) {
				ZtmgUserAuthorization ztmgUserAuthorization = list.get(0);
				ztmgUserAuthorization.setGrantList(AuthEnum.REPAYMENT.getValue());
				ztmgUserAuthorization.setStatus("S");
				ztmgUserAuthorization.setSignature(sign);
				ztmgUserAuthorization.setGrantAmountList(amount); // 授权金额
				ztmgUserAuthorization.setGrantTimeList(failTime); // 授权截至期限
				ztmgUserAuthorization.setUpdateDate(new Date());
				ztmgUserAuthorization.setRemarks("变更授权信息");
				int updateCreUserAuthorization = ztmgUserAuthorizationDao.update(ztmgUserAuthorization);
				logger.info("变更授权信息:{}", updateCreUserAuthorization == 1 ? "成功" : "失败");
				flag = true;
			} else {
				ZtmgUserAuthorization ztmgUserAuthorization = new ZtmgUserAuthorization();
				ztmgUserAuthorization.setId(IdGen.uuid());
				ztmgUserAuthorization.setUserId(platformUserNo);
				ztmgUserAuthorization.setMerchantId(platformNo);
				ztmgUserAuthorization.setStatus("S");
				ztmgUserAuthorization.setSignature(sign);
				ztmgUserAuthorization.setGrantList(AuthEnum.REPAYMENT.getValue());
				ztmgUserAuthorization.setGrantAmountList(amount);
				ztmgUserAuthorization.setGrantTimeList(failTime);
				ztmgUserAuthorization.setCreateDate(new Date());
				ztmgUserAuthorization.setUpdateDate(new Date());
				ztmgUserAuthorization.setRemarks("新增授权信息");
				int insertCreUserAuthorization = ztmgUserAuthorizationDao.insert(ztmgUserAuthorization);
				logger.info("新增授权信息:{}", insertCreUserAuthorization == 1 ? "成功" : "失败");
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;

	}

}
