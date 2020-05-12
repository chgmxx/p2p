package com.power.platform.ifcert.userInfo.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.cert.open.CertToolV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.cgb.dao.CgbUserBankCardDao;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.dao.ztmgLoanBasicInfo.ZtmgLoanBasicInfoDao;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.ZtmgLoanBasicInfo;
import com.power.platform.ifcert.dao.BatchNumDao;
import com.power.platform.ifcert.dao.IfCertUserInfoDao;
import com.power.platform.ifcert.entity.BatchNum;
import com.power.platform.ifcert.entity.IfCertUserInfo;
import com.power.platform.ifcert.type.CardTypeEnum;
import com.power.platform.ifcert.type.CountriesEnum;
import com.power.platform.ifcert.type.DataTypeEnum;
import com.power.platform.ifcert.type.InfTypeEnum;
import com.power.platform.ifcert.type.RegisterDateEnum;
import com.power.platform.ifcert.type.ResponseEnum;
import com.power.platform.ifcert.type.UserAddressEnum;
import com.power.platform.ifcert.type.UserAttr;
import com.power.platform.ifcert.type.UserFundEnum;
import com.power.platform.ifcert.type.UserLawPersonEnum;
import com.power.platform.ifcert.type.UserProvinceEnum;
import com.power.platform.ifcert.type.UserSexEnum;
import com.power.platform.ifcert.type.UserTypeEnum;
import com.power.platform.ifcert.utils.HashAndSaltUtil;
import com.power.platform.ifcert.utils.UserIdCardUtil;
import com.power.platform.ifcert.utils.http.HttpsUtilSendPost;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 
 * class: UserInfoDataAccessService <br>
 * description: 用户信息数据访问，是指出借人、借款人、第三方担保公司、平台自身、
 * 受托支付方的相关属性信息。需要注意的是，借款人用户姓名需要推送明文. <br>
 * author: Roy <br>
 * date: 2019年4月22日 上午10:52:25
 */
@Service("ifcertUserInfoDataAccessService")
public class IfcertUserInfoDataAccessService {

	private static final Logger log = LoggerFactory.getLogger(IfcertUserInfoDataAccessService.class);

	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private CreditUserInfoDao creditUserInfoDao;
	@Resource
	private IfCertUserInfoDao userinfoDao;
	@Resource
	private CgbUserBankCardDao cgbUserBankCardDao;
	@Resource
	private BatchNumDao batchNumDao;
	@Resource
	private WloanSubjectDao wloanSubjectDao;
	@Resource
	private ZtmgLoanBasicInfoDao ztmgLoanBasicInfoDao;
	/**
	 * 国家应急中心工具包.
	 */
	public static CertToolV1 tool = new CertToolV1();

	/**
	 * 
	 * methods: pushCreUserInfoC <br>
	 * description: 补推用户信息. <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月22日 上午11:52:28
	 * 
	 * @param userIdList
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> fillPushUserInfo(List<IfCertUserInfo> uiList) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 接口数据列表.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 系统当前时间毫秒值.
		long currentTimeMillis = System.currentTimeMillis();
		// 当前推送数据批次号.
		String batchNum = ServerURLConfig.SOURCE_CODE + "_" + DateUtils.getDate("yyyyMMdd") + "_" + currentTimeMillis;
		// 发送时间.
		String sentTime = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
		try {
			/**
			 * 该批次出借人集合数据接口封装.
			 */
			for (IfCertUserInfo ui : uiList) { // 融资主体.
				// 国家应急中心用户信息.
				IfCertUserInfo userinfo = new IfCertUserInfo();
				/**
				 * 出借人信息封装.
				 */
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				/**
				 * 接口版本号.
				 */
				param.put("version", ServerURLConfig.VERSION);
				userinfo.setVersion(ServerURLConfig.VERSION);
				/**
				 * 网贷机构平台在应急中心系统的唯一编号，
				 * 网贷机构在应急中心系统注册实名之后自动生成.
				 */
				param.put("sourceCode", ServerURLConfig.SOURCE_CODE);
				userinfo.setSourceCode(ServerURLConfig.SOURCE_CODE);
				/**
				 * 用户类型1-自然人/2-企业.
				 */
				param.put("userType", UserTypeEnum.USER_TYPE_2.getValue());
				userinfo.setUserType(UserTypeEnum.USER_TYPE_2.getValue());
				/**
				 * 用户属性1-出借方/2-借款方/3-出借方+借款方/4-自代偿平台方/5-第三方代偿/6-受托支付方.
				 */
				param.put("userAttr", UserAttr.USER_ATTR_2);
				userinfo.setUserAttr(UserAttr.USER_ATTR_2);
				param.put("userCreateTime", DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
				userinfo.setUserCreateTime(DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
						/**
						 * // 用户姓名/名称.
						 * // 出借人姓名（或企业名称）／借款人姓名（或企业名称）/自代偿平台名称/第三方担保公司名称
						 * //说明：出借人允许脱敏处理，姓氏不允许脱敏，名字可脱敏。借款人姓名、企业名称（出借/借款）、自代偿平台名称、第三方担保公司名称不可脱敏处理.
						 */
						param.put("userName", ui.getUserName());
						userinfo.setUserName(ui.getUserName());
						/**
						 * 1-中国大陆；2-中国港澳台；3-国外；
						 * 说明：此处需区分自然人和企业; 如果网贷机构没有此字段数据填写-1.
						 */
						param.put("countries", CountriesEnum.COUNTRIES_NEGATIVE_1.getValue());
						userinfo.setCountries(CountriesEnum.COUNTRIES_NEGATIVE_1.getValue());
						/**
						 * 1-身份证；2-护照；3-军官证；4-台湾居民来往大陆通行证；5-港澳居民来往内地通行证；
						 * 6-外国人永久居留身份证；7-三证合一证/五证合一证/工商注册号等机构证件类型；说明：如无以上信息请联系应急中心
						 */
						param.put("cardType", CardTypeEnum.CARD_TYPE_7.getValue());
						userinfo.setCardType(CardTypeEnum.CARD_TYPE_7.getValue());
						/**
						 * 个人证件号（身份证；护照；台湾居民来往大陆通行证；港澳居民来往内地通行证；
						 * 外国人永久居留身份证）企业证件号（五证合一号；三证合一号）。
						 * 说明：个人证件号必须脱敏后4 位；所有企业证件号不可以脱敏。如果网贷机构暂时没有记录企业三证合一号或者五证合一号，
						 * 可以去国家企业信用信息公示系统等网站查询获得，完善信息之后再上报。
						 */
						param.put("userIdcard", ui.getUserIdcard());
						userinfo.setUserIdcard(ui.getUserIdcard());
						/**
						 * 用户明文的证件号进行加密后的hash 值（hash 值为64 位随机数字和字母）。加密方法需使用应急中心提供的工具包( 下载地址:”https://open.ifcert.org.cn”)，
						 * 按照idCardHash 方法生成的hash 值。此字段是散标接口、交易流水接口、还款计划、初始债权、转让项目、承接项目的关联字段.
						 */
						param.put("userIdcardHash", ui.getUserIdcardHash());
						userinfo.setUserIdcardHash(ui.getUserIdcardHash());
						/**
						 * 手机号为自然人手机号或企业法人手机号。若用户类型是1-自然人,则本项需填写自然人手机号；
						 * 若用户类型是2-企业,则本项传法人手机号码。说明：必须脱敏后4 位.
						 */
						param.put("userPhone",ui.getUserPhone() );
						userinfo.setUserPhone(ui.getUserPhone());
						/**
						 * 用户明文的手机号进行加密后的hash 值（hash 值为64 位随机数字和字母）。加密方法需使用应急中心提供的工具包( 下载地址:”https://open.ifcert.org.cn”)，
						 * 按照phoneHash 方法生成的hash 值。
						 */
						param.put("userPhoneHash", ui.getUserPhoneHash());
						userinfo.setUserPhoneHash(ui.getUserPhoneHash());
						/**
						 * 使用工具包中phoneHash 方法生成的salt 值.
						 */
						param.put("userUuid", ui.getUserUuid());
						userinfo.setUserUuid(ui.getUserUuid());
						/**
						 * 用户类型是企业必填，用户类型是自然人填写-1.
						 * 说明：如果网贷机构暂时没有记录此项信息，可以去国家企业信用信息公示系统等网站查询获得，完善信息之后再上报。
						 */
						param.put("userLawperson",ui.getUserLawperson());
						userinfo.setUserLawperson(ui.getUserLawperson());
						/**
						 * 注册资本，单位：万元.
						 * 用户类型是企业必填，用户类型是自然人填写-1。
						 * 说明：币种不是人民币的按照汇率转化成人民币对应的金额.
						 * 说明：如果网贷机构暂时没有记录此项信息，可以去国家企业信用信息公示系统等网站查询获得，完善信息之后再上报。
						 */
						param.put("userFund",ui.getUserFund());
						userinfo.setUserFund(ui.getUserFund());
						/**
						 * 注册省份：用户归属地的行政区号。用户类型是企业必填，用户类型是自然人填写-1，使用工具包中getCompanyAscription 方法取得（）.
						 */
						param.put("userProvince",ui.getUserProvince());
						userinfo.setUserProvince(ui.getUserProvince());
						/**
						 * 注册地址：用户类型是企业必填，用户类型是自然人填写-1。
						 * 说明：如果网贷机构暂时没有记录此项信息，可以去国家企业信用信息公示系统等网站查询获得，完善信息之后再上报。
						 */
						param.put("userAddress",ui.getUserAddress());
						userinfo.setUserAddress(ui.getUserAddress());
						/**
						 * 企业注册时间：用户类型是企业必填，用户类型是自然人填写-1，格式"yyyy-MM-dd".
						 * 说明：如果网贷机构暂时没有记录此项信息，可以去国家企业信用信息公示系统等网站查询获得，完善信息之后再上报。
						 */
						param.put("registerDate",ui.getRegisterDate());
						userinfo.setRegisterDate(ui.getRegisterDate());
						/**
						 * 用户性别：用户类型是自然人必填，用户类型是企业填写-1。
						 * 1：男；0：女；
						 */
						param.put("userSex", UserSexEnum.USER_SEX_NAGATIVE_1.getValue());
						userinfo.setUserSex(UserSexEnum.USER_SEX_NAGATIVE_1.getValue());
						/**
						 * 用户交易时使用的银行卡号。如果是存管银行，则按存管银行返回的脱敏数据推送。
						 * 说明：采用JSON数组方式组织数据， 如："userList":[{"userBankAccount":"6228480240389521611"},{"userBankAccount":"6228480240389521612"}].
						 */
						List<Map<String, String>> userBankAccountlist = new ArrayList<Map<String, String>>();
//						CgbUserBankCard cgbUserBankCard = new CgbUserBankCard();
//						cgbUserBankCard.setState(CgbUserBankCard.CERTIFY_YES); // 银行卡已认证.
//						cgbUserBankCard.setUserId(creUser.getId()); // 借款人ID.
//						List<CgbUserBankCard> cgbUserBankCardList = cgbUserBankCardDao.findCreditList(cgbUserBankCard);
//						// 用户银行卡号列表，使用逗号拼接.
//						StringBuilder sb = new StringBuilder();
//						for (int i = 0; i < cgbUserBankCardList.size(); i++) {
//							Map<String, String> userBankAccountMap = new LinkedHashMap<String, String>();
//							userBankAccountMap.put("userBankAccount", StringUtils.replaceBlanK(cgbUserBankCardList.get(i).getBankAccountNo()));
//							userBankAccountlist.add(userBankAccountMap);
//							if (sb.length() > 0) {// 该步即不会第一位有逗号，也防止最后一位拼接逗号！
//								sb.append(",");
//							}
//							sb.append(StringUtils.replaceBlanK(cgbUserBankCardList.get(i).getBankAccountNo()));
//						}
						String userBankAccount = ui.getUserBankAccount();
						if(userBankAccount!=null&&!"".equals(userBankAccount)) {
							String str[] = userBankAccount.split(",");
							for (int i = 0; i < str.length; i++) {
								Map<String, String> userBankAccountMap = new LinkedHashMap<String, String>();
								userBankAccountMap.put("userBankAccount",str[i]);
								userBankAccountlist.add(userBankAccountMap);
							}
						}
						param.put("userList", userBankAccountlist);
						userinfo.setUserBankAccount(ui.getUserBankAccount());
						userinfo.setBatchNum(batchNum); // 批次号.
						userinfo.setSendTime(sentTime); // 发送时间.
						int insert = userinfoDao.insert(userinfo);
						if (insert == 1) {
							log.info("借款人用户信息插入成功！");
						} else {
							log.info("借款人用户信息插入失败！");
						}
				list.add(param);
			}
			JSONObject json = new JSONObject();
			String nonce = Integer.toHexString(new Random().nextInt());
			json.accumulate("version", ServerURLConfig.VERSION);
			json.accumulate("batchNum", batchNum);
			json.accumulate("checkCode", tool.checkCode(list.toString()));
			json.accumulate("totalNum", list.size() + "");
			json.accumulate("sentTime", sentTime);
			json.accumulate("sourceCode", ServerURLConfig.SOURCE_CODE);
			json.accumulate("infType", InfTypeEnum.INF_TYPE_1.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);

			// System.out.println("出借人用户信息接口推送数据：" + json);
			log.info("借款人用户信息接口推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_USERINFO, "utf-8");
			// System.out.println("出借人用户信息接口推送响应：" + responseStr);
			log.info("借款人用户信息接口推送响应：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_1.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_01.getValue());
					bn.setCode(code);
					int insert = batchNumDao.insert(bn);
					if (insert == 1) {
						log.info("该批次数据状态信息插入成功！");
					} else {
						log.info("该批次数据状态信息插入失败！");
					}
					result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_0000.getValue());
					result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_0000.getText());
				} else { // 该批次数据推送失败.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_1.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
					bn.setCode(code);
					int insert = batchNumDao.insert(bn);
					if (insert == 1) {
						log.info("该批次数据状态信息插入成功！");
					} else {
						log.info("该批次数据状态信息插入失败！");
					}
					result.put("respCode", code);
					result.put("respMsg", "参考数据接入手册，错误码对照表");
				}
			} else { // 该批次数据推送失败.
				BatchNum bn = new BatchNum();
				bn.setId(IdGen.uuid());
				bn.setBatchNum(batchNum);
				bn.setSendTime(sentTime);
				bn.setInfType(InfTypeEnum.INF_TYPE_1.getValue());
				bn.setTotalNum(list.size() + "");
				bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
				bn.setCode("resp null");
				int insert = batchNumDao.insert(bn);
				if (insert == 1) {
					log.info("该批次数据状态信息插入成功！");
				} else {
					log.info("该批次数据状态信息插入失败！");
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_02.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
			BatchNum bn = new BatchNum();
			bn.setId(IdGen.uuid());
			bn.setBatchNum(batchNum);
			bn.setSendTime(sentTime);
			bn.setInfType(InfTypeEnum.INF_TYPE_1.getValue());
			bn.setTotalNum(list.size() + "");
			bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
			bn.setCode("exception");
			int insert = batchNumDao.insert(bn);
			if (insert == 1) {
				log.info("该批次数据状态信息插入成功！");
			} else {
				log.info("该批次数据状态信息插入失败！");
			}
			result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
			result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_04.getText());
		}

		return result;
	}
	
	
	
	/**
	 * 
	 * methods: pushCreUserInfoC <br>
	 * description: 推送存量借款人用户信息. <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月22日 上午11:52:28
	 * 
	 * @param userIdList
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> pushCreUserInfoC(List<String> subIdList) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 接口数据列表.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 系统当前时间毫秒值.
		long currentTimeMillis = System.currentTimeMillis();
		// 当前推送数据批次号.
		String batchNum = ServerURLConfig.SOURCE_CODE + "_" + DateUtils.getDate("yyyyMMdd") + "_" + currentTimeMillis;
		// 发送时间.
		String sentTime = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
		try {
			/**
			 * 该批次出借人集合数据接口封装.
			 */
			for (String subId : subIdList) { // 融资主体.
				// 国家应急中心用户信息.
				IfCertUserInfo userinfo = new IfCertUserInfo();
				/**
				 * 出借人信息封装.
				 */
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				/**
				 * 接口版本号.
				 */
				param.put("version", ServerURLConfig.VERSION);
				userinfo.setVersion(ServerURLConfig.VERSION);
				/**
				 * 网贷机构平台在应急中心系统的唯一编号，
				 * 网贷机构在应急中心系统注册实名之后自动生成.
				 */
				param.put("sourceCode", ServerURLConfig.SOURCE_CODE);
				userinfo.setSourceCode(ServerURLConfig.SOURCE_CODE);
				/**
				 * 用户类型1-自然人/2-企业.
				 */
				param.put("userType", UserTypeEnum.USER_TYPE_2.getValue());
				userinfo.setUserType(UserTypeEnum.USER_TYPE_2.getValue());
				/**
				 * 用户属性1-出借方/2-借款方/3-出借方+借款方/4-自代偿平台方/5-第三方代偿/6-受托支付方.
				 */
				param.put("userAttr", UserAttr.USER_ATTR_2);
				userinfo.setUserAttr(UserAttr.USER_ATTR_2);
				WloanSubject subject = wloanSubjectDao.get(subId);
				if (subject != null) { // 借款方融资主体信息.
					CreditUserInfo creUser = creditUserInfoDao.get(subject.getLoanApplyId());
					if (creUser != null) {
						/**
						 * 用户注册时间.
						 */
						if (creUser.getRegisterDate() != null) {
							param.put("userCreateTime", DateUtils.formatDate(creUser.getRegisterDate(), "yyyy-MM-dd HH:mm:ss"));
							userinfo.setUserCreateTime(DateUtils.formatDate(creUser.getRegisterDate(), "yyyy-MM-dd HH:mm:ss"));
						} else {
							param.put("userCreateTime", DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
							userinfo.setUserCreateTime(DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
						}
						/**
						 * // 用户姓名/名称.
						 * // 出借人姓名（或企业名称）／借款人姓名（或企业名称）/自代偿平台名称/第三方担保公司名称
						 * //说明：出借人允许脱敏处理，姓氏不允许脱敏，名字可脱敏。借款人姓名、企业名称（出借/借款）、自代偿平台名称、第三方担保公司名称不可脱敏处理.
						 */
						param.put("userName", creUser.getEnterpriseFullName());
						userinfo.setUserName(creUser.getEnterpriseFullName());
						/**
						 * 1-中国大陆；2-中国港澳台；3-国外；
						 * 说明：此处需区分自然人和企业; 如果网贷机构没有此字段数据填写-1.
						 */
						param.put("countries", CountriesEnum.COUNTRIES_NEGATIVE_1.getValue());
						userinfo.setCountries(CountriesEnum.COUNTRIES_NEGATIVE_1.getValue());
						/**
						 * 1-身份证；2-护照；3-军官证；4-台湾居民来往大陆通行证；5-港澳居民来往内地通行证；
						 * 6-外国人永久居留身份证；7-三证合一证/五证合一证/工商注册号等机构证件类型；说明：如无以上信息请联系应急中心
						 */
						param.put("cardType", CardTypeEnum.CARD_TYPE_7.getValue());
						userinfo.setCardType(CardTypeEnum.CARD_TYPE_7.getValue());
						/**
						 * 个人证件号（身份证；护照；台湾居民来往大陆通行证；港澳居民来往内地通行证；
						 * 外国人永久居留身份证）企业证件号（五证合一号；三证合一号）。
						 * 说明：个人证件号必须脱敏后4 位；所有企业证件号不可以脱敏。如果网贷机构暂时没有记录企业三证合一号或者五证合一号，
						 * 可以去国家企业信用信息公示系统等网站查询获得，完善信息之后再上报。
						 */
						param.put("userIdcard", subject.getBusinessNo());
						userinfo.setUserIdcard(subject.getBusinessNo());
						/**
						 * 用户明文的证件号进行加密后的hash 值（hash 值为64 位随机数字和字母）。加密方法需使用应急中心提供的工具包( 下载地址:”https://open.ifcert.org.cn”)，
						 * 按照idCardHash 方法生成的hash 值。此字段是散标接口、交易流水接口、还款计划、初始债权、转让项目、承接项目的关联字段.
						 */
						param.put("userIdcardHash", tool.idCardHash(subject.getBusinessNo()));
						userinfo.setUserIdcardHash(tool.idCardHash(subject.getBusinessNo()));
						/**
						 * 手机号为自然人手机号或企业法人手机号。若用户类型是1-自然人,则本项需填写自然人手机号；
						 * 若用户类型是2-企业,则本项传法人手机号码。说明：必须脱敏后4 位.
						 */
						param.put("userPhone", CommonStringUtils.mobileEncryptAfterFour(subject.getLoanPhone()));
						userinfo.setUserPhone(CommonStringUtils.mobileEncryptAfterFour(subject.getLoanPhone()));
						// 获取用户手机号码的Hash和Salt值.
						Map<String, String> hashAndSaltMap = HashAndSaltUtil.getPhoneHashAndSalt(subject.getLoanPhone());
						/**
						 * 用户明文的手机号进行加密后的hash 值（hash 值为64 位随机数字和字母）。加密方法需使用应急中心提供的工具包( 下载地址:”https://open.ifcert.org.cn”)，
						 * 按照phoneHash 方法生成的hash 值。
						 */
						param.put("userPhoneHash", hashAndSaltMap.get("userPhoneHash"));
						userinfo.setUserPhoneHash(hashAndSaltMap.get("userPhoneHash"));
						/**
						 * 使用工具包中phoneHash 方法生成的salt 值.
						 */
						param.put("userUuid", hashAndSaltMap.get("userUuid"));
						userinfo.setUserUuid(hashAndSaltMap.get("userUuid"));
						/**
						 * 用户类型是企业必填，用户类型是自然人填写-1.
						 * 说明：如果网贷机构暂时没有记录此项信息，可以去国家企业信用信息公示系统等网站查询获得，完善信息之后再上报。
						 */
						param.put("userLawperson", subject.getLoanUser());
						userinfo.setUserLawperson(subject.getLoanUser());
						// 借款人基本信息.
						ZtmgLoanBasicInfo zlbi = new ZtmgLoanBasicInfo();
						zlbi.setCreditUserId(creUser.getId());
						ZtmgLoanBasicInfo zlBasicI = ztmgLoanBasicInfoDao.findByCreditUserId(zlbi);
						if (zlBasicI != null) {
							/**
							 * 注册资本，单位：万元.
							 * 用户类型是企业必填，用户类型是自然人填写-1。
							 * 说明：币种不是人民币的按照汇率转化成人民币对应的金额.
							 * 说明：如果网贷机构暂时没有记录此项信息，可以去国家企业信用信息公示系统等网站查询获得，完善信息之后再上报。
							 */
							Double registeredCapitalD = Double.valueOf(zlBasicI.getRegisteredCapital());
							param.put("userFund", NumberUtils.scaleSixStr(NumberUtils.divide(registeredCapitalD, 10000D)));
							userinfo.setUserFund(NumberUtils.scaleSixStr(NumberUtils.divide(registeredCapitalD, 10000D)));
							/**
							 * 注册省份：用户归属地的行政区号。用户类型是企业必填，用户类型是自然人填写-1，使用工具包中getCompanyAscription 方法取得（）.
							 */
							param.put("userProvince", tool.getCompanyAscription(subject.getBusinessNo()));
							userinfo.setUserProvince(tool.getCompanyAscription(subject.getBusinessNo()));
							/**
							 * 注册地址：用户类型是企业必填，用户类型是自然人填写-1。
							 * 说明：如果网贷机构暂时没有记录此项信息，可以去国家企业信用信息公示系统等网站查询获得，完善信息之后再上报。
							 */
							param.put("userAddress", zlBasicI.getRegisteredAddress());
							userinfo.setUserAddress(zlBasicI.getRegisteredAddress());
							/**
							 * 企业注册时间：用户类型是企业必填，用户类型是自然人填写-1，格式"yyyy-MM-dd".
							 * 说明：如果网贷机构暂时没有记录此项信息，可以去国家企业信用信息公示系统等网站查询获得，完善信息之后再上报。
							 */
							param.put("registerDate", DateUtils.formatDate(zlBasicI.getSetUpTime(), "yyyy-MM-dd HH:mm:ss"));
							userinfo.setRegisterDate(DateUtils.formatDate(zlBasicI.getSetUpTime(), "yyyy-MM-dd HH:mm:ss"));
						}
						/**
						 * 用户性别：用户类型是自然人必填，用户类型是企业填写-1。
						 * 1：男；0：女；
						 */
						param.put("userSex", UserSexEnum.USER_SEX_NAGATIVE_1.getValue());
						userinfo.setUserSex(UserSexEnum.USER_SEX_NAGATIVE_1.getValue());
						/**
						 * 用户交易时使用的银行卡号。如果是存管银行，则按存管银行返回的脱敏数据推送。
						 * 说明：采用JSON数组方式组织数据， 如："userList":[{"userBankAccount":"6228480240389521611"},{"userBankAccount":"6228480240389521612"}].
						 */
						List<Map<String, String>> userBankAccountlist = new ArrayList<Map<String, String>>();
						CgbUserBankCard cgbUserBankCard = new CgbUserBankCard();
						cgbUserBankCard.setState(CgbUserBankCard.CERTIFY_YES); // 银行卡已认证.
						cgbUserBankCard.setUserId(creUser.getId()); // 借款人ID.
						List<CgbUserBankCard> cgbUserBankCardList = cgbUserBankCardDao.findCreditList(cgbUserBankCard);
						// 用户银行卡号列表，使用逗号拼接.
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < cgbUserBankCardList.size(); i++) {
							Map<String, String> userBankAccountMap = new LinkedHashMap<String, String>();
							userBankAccountMap.put("userBankAccount", StringUtils.replaceBlanK(cgbUserBankCardList.get(i).getBankAccountNo()));
							userBankAccountlist.add(userBankAccountMap);
							if (sb.length() > 0) {// 该步即不会第一位有逗号，也防止最后一位拼接逗号！
								sb.append(",");
							}
							sb.append(StringUtils.replaceBlanK(cgbUserBankCardList.get(i).getBankAccountNo()));
						}
						param.put("userList", userBankAccountlist);
						userinfo.setUserBankAccount(sb.toString());
						userinfo.setBatchNum(batchNum); // 批次号.
						userinfo.setSendTime(sentTime); // 发送时间.
						int insert = userinfoDao.insert(userinfo);
						if (insert == 1) {
							log.info("借款人用户信息插入成功！");
						} else {
							log.info("借款人用户信息插入失败！");
						}
					}
				}
				list.add(param);
			}
			JSONObject json = new JSONObject();
			String nonce = Integer.toHexString(new Random().nextInt());
			json.accumulate("version", ServerURLConfig.VERSION);
			json.accumulate("batchNum", batchNum);
			json.accumulate("checkCode", tool.checkCode(list.toString()));
			json.accumulate("totalNum", list.size() + "");
			json.accumulate("sentTime", sentTime);
			json.accumulate("sourceCode", ServerURLConfig.SOURCE_CODE);
			json.accumulate("infType", InfTypeEnum.INF_TYPE_1.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);

			// System.out.println("出借人用户信息接口推送数据：" + json);
			log.info("借款人用户信息接口推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_USERINFO, "utf-8");
			// System.out.println("出借人用户信息接口推送响应：" + responseStr);
			log.info("借款人用户信息接口推送响应：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_1.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_01.getValue());
					bn.setCode(code);
					int insert = batchNumDao.insert(bn);
					if (insert == 1) {
						log.info("该批次数据状态信息插入成功！");
					} else {
						log.info("该批次数据状态信息插入失败！");
					}
					result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_0000.getValue());
					result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_0000.getText());
				} else { // 该批次数据推送失败.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_1.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
					bn.setCode(code);
					int insert = batchNumDao.insert(bn);
					if (insert == 1) {
						log.info("该批次数据状态信息插入成功！");
					} else {
						log.info("该批次数据状态信息插入失败！");
					}
					result.put("respCode", code);
					result.put("respMsg", "参考数据接入手册，错误码对照表");
				}
			} else { // 该批次数据推送失败.
				BatchNum bn = new BatchNum();
				bn.setId(IdGen.uuid());
				bn.setBatchNum(batchNum);
				bn.setSendTime(sentTime);
				bn.setInfType(InfTypeEnum.INF_TYPE_1.getValue());
				bn.setTotalNum(list.size() + "");
				bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
				bn.setCode("resp null");
				int insert = batchNumDao.insert(bn);
				if (insert == 1) {
					log.info("该批次数据状态信息插入成功！");
				} else {
					log.info("该批次数据状态信息插入失败！");
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_02.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
			BatchNum bn = new BatchNum();
			bn.setId(IdGen.uuid());
			bn.setBatchNum(batchNum);
			bn.setSendTime(sentTime);
			bn.setInfType(InfTypeEnum.INF_TYPE_1.getValue());
			bn.setTotalNum(list.size() + "");
			bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
			bn.setCode("exception");
			int insert = batchNumDao.insert(bn);
			if (insert == 1) {
				log.info("该批次数据状态信息插入成功！");
			} else {
				log.info("该批次数据状态信息插入失败！");
			}
			result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
			result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_04.getText());
		}

		return result;
	}

	/**
	 * 
	 * methods: pushInvestUserInfo <br>
	 * description: 推送全量出借人用户信息. <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月7日 下午4:00:24
	 * 
	 * @param userIdList
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> pushInvestUserInfo(List<String> userIdList) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 接口数据列表.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 系统当前时间毫秒值.
		long currentTimeMillis = System.currentTimeMillis();
		// 当前推送数据批次号.
		String batchNum = ServerURLConfig.SOURCE_CODE + "_" + DateUtils.getDate("yyyyMMdd") + "_" + currentTimeMillis;
		// 发送时间.
		String sentTime = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
		try {
			/**
			 * 该批次出借人集合数据接口封装.
			 */
			for (String userId : userIdList) {
				// 国家应急中心用户信息.
				IfCertUserInfo userinfo = new IfCertUserInfo();
				/**
				 * 出借人信息封装.
				 */
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				// 用户信息.
				UserInfo user = userInfoDao.getCgb(userId);
				/**
				 * 接口版本号.
				 */
				param.put("version", ServerURLConfig.VERSION);
				userinfo.setVersion(ServerURLConfig.VERSION);
				/**
				 * 网贷机构平台在应急中心系统的唯一编号，
				 * 网贷机构在应急中心系统注册实名之后自动生成.
				 */
				param.put("sourceCode", ServerURLConfig.SOURCE_CODE);
				userinfo.setSourceCode(ServerURLConfig.SOURCE_CODE);
				/**
				 * 用户类型1-自然人/2-企业.
				 */
				param.put("userType", UserTypeEnum.USER_TYPE_1.getValue());
				userinfo.setUserType(UserTypeEnum.USER_TYPE_1.getValue());
				/**
				 * 用户属性1-出借方/2-借款方/3-出借方+借款方/4-自代偿平台方/5-第三方代偿/6-受托支付方.
				 */
				param.put("userAttr", UserAttr.USER_ATTR_1);
				userinfo.setUserAttr(UserAttr.USER_ATTR_1);
				if (user != null) { // 用户信息.
					/**
					 * 用户注册时间.
					 */
					if (user.getRegisterDate() != null) {
						param.put("userCreateTime", DateUtils.formatDate(user.getRegisterDate(), "yyyy-MM-dd HH:mm:ss"));
						userinfo.setUserCreateTime(DateUtils.formatDate(user.getRegisterDate(), "yyyy-MM-dd HH:mm:ss"));
					} else {
						param.put("userCreateTime", DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
						userinfo.setUserCreateTime(DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
					}
					/**
					 * // 用户姓名/名称.
					 * // 出借人姓名（或企业名称）／借款人姓名（或企业名称）/自代偿平台名称/第三方担保公司名称
					 * //说明：出借人允许脱敏处理，姓氏不允许脱敏，名字可脱敏。借款人姓名、企业名称（出借/借款）、自代偿平台名称、第三方担保公司名称不可脱敏处理.
					 */
					param.put("userName", CommonStringUtils.replaceNameX(user.getRealName()));
					userinfo.setUserName(CommonStringUtils.replaceNameX(user.getRealName()));
					/**
					 * 1-中国大陆；2-中国港澳台；3-国外；
					 * 说明：此处需区分自然人和企业; 如果网贷机构没有此字段数据填写-1.
					 */
					param.put("countries", CountriesEnum.COUNTRIES_NEGATIVE_1.getValue());
					userinfo.setCountries(CountriesEnum.COUNTRIES_NEGATIVE_1.getValue());
					/**
					 * 1-身份证；2-护照；3-军官证；4-台湾居民来往大陆通行证；5-港澳居民来往内地通行证；
					 * 6-外国人永久居留身份证；7-三证合一证/五证合一证/工商注册号等机构证件类型；说明：如无以上信息请联系应急中心
					 */
					param.put("cardType", CardTypeEnum.CARD_TYPE_1.getValue());
					userinfo.setCardType(CardTypeEnum.CARD_TYPE_1.getValue());
					/**
					 * 个人证件号（身份证；护照；台湾居民来往大陆通行证；港澳居民来往内地通行证；
					 * 外国人永久居留身份证）企业证件号（五证合一号；三证合一号）。
					 * 说明：个人证件号必须脱敏后4 位；所有企业证件号不可以脱敏。如果网贷机构暂时没有记录企业三证合一号或者五证合一号，
					 * 可以去国家企业信用信息公示系统等网站查询获得，完善信息之后再上报。
					 */
					param.put("userIdcard", CommonStringUtils.idEncryptAfterFour(user.getCertificateNo()));
					userinfo.setUserIdcard(CommonStringUtils.idEncryptAfterFour(user.getCertificateNo()));
					/**
					 * 用户明文的证件号进行加密后的hash 值（hash 值为64 位随机数字和字母）。加密方法需使用应急中心提供的工具包( 下载地址:”https://open.ifcert.org.cn”)，
					 * 按照idCardHash 方法生成的hash 值。此字段是散标接口、交易流水接口、还款计划、初始债权、转让项目、承接项目的关联字段.
					 */
					param.put("userIdcardHash", HashAndSaltUtil.tool.idCardHash(user.getCertificateNo()));
					userinfo.setUserIdcardHash(HashAndSaltUtil.tool.idCardHash(user.getCertificateNo()));
					/**
					 * 手机号为自然人手机号或企业法人手机号。若用户类型是1-自然人,则本项需填写自然人手机号；
					 * 若用户类型是2-企业,则本项传法人手机号码。说明：必须脱敏后4 位.
					 */
					param.put("userPhone", CommonStringUtils.mobileEncryptAfterFour(user.getName()));
					userinfo.setUserPhone(CommonStringUtils.mobileEncryptAfterFour(user.getName()));
					// 获取用户手机号码的Hash和Salt值.
					Map<String, String> hashAndSaltMap = HashAndSaltUtil.getPhoneHashAndSalt(user.getName());
					/**
					 * 用户明文的手机号进行加密后的hash 值（hash 值为64 位随机数字和字母）。加密方法需使用应急中心提供的工具包( 下载地址:”https://open.ifcert.org.cn”)，
					 * 按照phoneHash 方法生成的hash 值。
					 */
					param.put("userPhoneHash", hashAndSaltMap.get("userPhoneHash"));
					userinfo.setUserPhoneHash(hashAndSaltMap.get("userPhoneHash"));
					/**
					 * 使用工具包中phoneHash 方法生成的salt 值.
					 */
					param.put("userUuid", hashAndSaltMap.get("userUuid"));
					userinfo.setUserUuid(hashAndSaltMap.get("userUuid"));
					/**
					 * 用户类型是企业必填，用户类型是自然人填写-1.
					 * 说明：如果网贷机构暂时没有记录此项信息，可以去国家企业信用信息公示系统等网站查询获得，完善信息之后再上报。
					 */
					param.put("userLawperson", UserLawPersonEnum.USER_LAW_PERSON_NEGATIVE_1.getValue());
					userinfo.setUserLawperson(UserLawPersonEnum.USER_LAW_PERSON_NEGATIVE_1.getValue());
					/**
					 * 注册资本，单位：万元.
					 * 用户类型是企业必填，用户类型是自然人填写-1。
					 * 说明：币种不是人民币的按照汇率转化成人民币对应的金额.
					 * 说明：如果网贷机构暂时没有记录此项信息，可以去国家企业信用信息公示系统等网站查询获得，完善信息之后再上报。
					 */
					param.put("userFund", UserFundEnum.USER_FUND_NEGATIVE_1.getValue());
					userinfo.setUserFund(UserFundEnum.USER_FUND_NEGATIVE_1.getValue());
					/**
					 * 注册省份：用户归属地的行政区号。用户类型是企业必填，用户类型是自然人填写-1，使用工具包中getCompanyAscription 方法取得（）.
					 */
					param.put("userProvince", UserProvinceEnum.USER_PROVINCE_NEGATIVE_1.getValue());
					userinfo.setUserProvince(UserProvinceEnum.USER_PROVINCE_NEGATIVE_1.getValue());
					/**
					 * 注册地址：用户类型是企业必填，用户类型是自然人填写-1。
					 * 说明：如果网贷机构暂时没有记录此项信息，可以去国家企业信用信息公示系统等网站查询获得，完善信息之后再上报。
					 */
					param.put("userAddress", UserAddressEnum.USER_ADDRESS_NAGATIVE_1.getValue());
					userinfo.setUserAddress(UserAddressEnum.USER_ADDRESS_NAGATIVE_1.getValue());
					/**
					 * 企业注册时间：用户类型是企业必填，用户类型是自然人填写-1，格式"yyyy-MM-dd".
					 * 说明：如果网贷机构暂时没有记录此项信息，可以去国家企业信用信息公示系统等网站查询获得，完善信息之后再上报。
					 */
					param.put("registerDate", RegisterDateEnum.REGISTER_DATE_NAGATIVE_1.getValue());
					userinfo.setRegisterDate(RegisterDateEnum.REGISTER_DATE_NAGATIVE_1.getValue());
					/**
					 * 用户性别：用户类型是自然人必填，用户类型是企业填写-1。
					 * 1：男；0：女；
					 */
					param.put("userSex", UserIdCardUtil.getGenderByIdCard(user.getCertificateNo()));
					userinfo.setUserSex(UserIdCardUtil.getGenderByIdCard(user.getCertificateNo()));
					/**
					 * 用户交易时使用的银行卡号。如果是存管银行，则按存管银行返回的脱敏数据推送。
					 * 说明：采用JSON数组方式组织数据， 如："userList":[{"userBankAccount":"6228480240389521611"},{"userBankAccount":"6228480240389521612"}].
					 */
					List<Map<String, String>> userBankAccountlist = new ArrayList<Map<String, String>>();
					CgbUserBankCard cgbUserBankCard = new CgbUserBankCard();
					cgbUserBankCard.setState(CgbUserBankCard.CERTIFY_YES); // 银行卡已认证.
					cgbUserBankCard.setUserId(user.getId()); // 出借人ID.
					List<CgbUserBankCard> cgbUserBankCardList = cgbUserBankCardDao.findList(cgbUserBankCard);
					// 用户银行卡号列表，使用逗号拼接.
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < cgbUserBankCardList.size(); i++) {
						Map<String, String> userBankAccountMap = new LinkedHashMap<String, String>();
						userBankAccountMap.put("userBankAccount", StringUtils.replaceBlanK(cgbUserBankCardList.get(i).getBankAccountNo()));
						userBankAccountlist.add(userBankAccountMap);
						if (sb.length() > 0) {// 该步即不会第一位有逗号，也防止最后一位拼接逗号！
							sb.append(",");
						}
						sb.append(StringUtils.replaceBlanK(cgbUserBankCardList.get(i).getBankAccountNo()));
					}
					param.put("userList", userBankAccountlist);
					userinfo.setUserBankAccount(sb.toString());
					userinfo.setBatchNum(batchNum); // 批次号.
					userinfo.setSendTime(sentTime); // 发送时间.
					int insert = userinfoDao.insert(userinfo);
					if (insert == 1) {
						log.info("出借人用户信息插入成功！");
					} else {
						log.info("出借人用户信息插入失败！");
					}
				}
				list.add(param);
			}
			JSONObject json = new JSONObject();
			String nonce = Integer.toHexString(new Random().nextInt());
			json.accumulate("version", ServerURLConfig.VERSION);
			json.accumulate("batchNum", batchNum);
			json.accumulate("checkCode", tool.checkCode(list.toString()));
			json.accumulate("totalNum", list.size() + "");
			json.accumulate("sentTime", sentTime);
			json.accumulate("sourceCode", ServerURLConfig.SOURCE_CODE);
			json.accumulate("infType", InfTypeEnum.INF_TYPE_1.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);

			// System.out.println("出借人用户信息接口推送数据：" + json);
			log.info("出借人用户信息接口推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_USERINFO, "utf-8");
			// System.out.println("出借人用户信息接口推送响应：" + responseStr);
			log.info("出借人用户信息接口推送响应：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_1.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_01.getValue());
					bn.setCode(code);
					int insert = batchNumDao.insert(bn);
					if (insert == 1) {
						log.info("该批次数据状态信息插入成功！");
					} else {
						log.info("该批次数据状态信息插入失败！");
					}
					result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_0000.getValue());
					result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_0000.getText());
				} else { // 该批次数据推送失败.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_1.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
					bn.setCode(code);
					int insert = batchNumDao.insert(bn);
					if (insert == 1) {
						log.info("该批次数据状态信息插入成功！");
					} else {
						log.info("该批次数据状态信息插入失败！");
					}
					result.put("respCode", code);
					result.put("respMsg", "参考数据接入手册，错误码对照表");
				}
			} else { // 该批次数据推送失败.
				BatchNum bn = new BatchNum();
				bn.setId(IdGen.uuid());
				bn.setBatchNum(batchNum);
				bn.setSendTime(sentTime);
				bn.setInfType(InfTypeEnum.INF_TYPE_1.getValue());
				bn.setTotalNum(list.size() + "");
				bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
				bn.setCode("resp null");
				int insert = batchNumDao.insert(bn);
				if (insert == 1) {
					log.info("该批次数据状态信息插入成功！");
				} else {
					log.info("该批次数据状态信息插入失败！");
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_02.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
			BatchNum bn = new BatchNum();
			bn.setId(IdGen.uuid());
			bn.setBatchNum(batchNum);
			bn.setSendTime(sentTime);
			bn.setInfType(InfTypeEnum.INF_TYPE_1.getValue());
			bn.setTotalNum(list.size() + "");
			bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
			bn.setCode("exception");
			int insert = batchNumDao.insert(bn);
			if (insert == 1) {
				log.info("该批次数据状态信息插入成功！");
			} else {
				log.info("该批次数据状态信息插入失败！");
			}
			result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
			result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_04.getText());
		}

		return result;
	}

	/**
	 * 
	 * methods: pushSingletonInvestUserInfo <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，去掉后缀test.<br>
	 * description: 单个出借人用户信息. <br>
	 * author: Roy <br>
	 * date: 2019年5月9日 下午4:10:20
	 * 
	 * @param user
	 * @return
	 */
	public Map<String, Object> pushSingletonInvestUserInfo(UserInfo userInfo) {

		// 系统当前时间毫秒值.
		long currentTimeMillis = System.currentTimeMillis();
		// 当前推送数据批次号.
		String batchNum = ServerURLConfig.SOURCE_CODE + "_" + DateUtils.getDate("yyyyMMdd") + "_" + currentTimeMillis;
		// 发送时间.
		String sentTime = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 该批次数据集合.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		/**
		 * 出借人信息封装.
		 */
		Map<String, Object> param = new LinkedHashMap<String, Object>();
		try {

			// 国家应急中心用户信息.
			IfCertUserInfo userinfo = new IfCertUserInfo();
			/**
			 * 接口版本号.
			 */
			param.put("version", ServerURLConfig.VERSION);
			userinfo.setVersion(ServerURLConfig.VERSION);
			/**
			 * 网贷机构平台在应急中心系统的唯一编号，
			 * 网贷机构在应急中心系统注册实名之后自动生成.
			 */
			param.put("sourceCode", ServerURLConfig.SOURCE_CODE);
			userinfo.setSourceCode(ServerURLConfig.SOURCE_CODE);
			/**
			 * 用户类型1-自然人/2-企业.
			 */
			param.put("userType", UserTypeEnum.USER_TYPE_1.getValue());
			userinfo.setUserType(UserTypeEnum.USER_TYPE_1.getValue());
			/**
			 * 用户属性1-出借方/2-借款方/3-出借方+借款方/4-自代偿平台方/5-第三方代偿/6-受托支付方.
			 */
			param.put("userAttr", UserAttr.USER_ATTR_1);
			userinfo.setUserAttr(UserAttr.USER_ATTR_1);
			if (userInfo != null) { // 用户信息.
				/**
				 * 用户注册时间.
				 */
				if (userInfo.getRegisterDate() != null) {
					param.put("userCreateTime", DateUtils.formatDate(userInfo.getRegisterDate(), "yyyy-MM-dd HH:mm:ss"));
					userinfo.setUserCreateTime(DateUtils.formatDate(userInfo.getRegisterDate(), "yyyy-MM-dd HH:mm:ss"));
				} else {
					param.put("userCreateTime", DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
					userinfo.setUserCreateTime(DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
				}
				/**
				 * // 用户姓名/名称.
				 * // 出借人姓名（或企业名称）／借款人姓名（或企业名称）/自代偿平台名称/第三方担保公司名称
				 * //说明：出借人允许脱敏处理，姓氏不允许脱敏，名字可脱敏。借款人姓名、企业名称（出借/借款）、自代偿平台名称、第三方担保公司名称不可脱敏处理.
				 */
				param.put("userName", CommonStringUtils.replaceNameX(userInfo.getRealName()));
				userinfo.setUserName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
				/**
				 * 1-中国大陆；2-中国港澳台；3-国外；
				 * 说明：此处需区分自然人和企业; 如果网贷机构没有此字段数据填写-1.
				 */
				param.put("countries", CountriesEnum.COUNTRIES_NEGATIVE_1.getValue());
				userinfo.setCountries(CountriesEnum.COUNTRIES_NEGATIVE_1.getValue());
				/**
				 * 1-身份证；2-护照；3-军官证；4-台湾居民来往大陆通行证；5-港澳居民来往内地通行证；
				 * 6-外国人永久居留身份证；7-三证合一证/五证合一证/工商注册号等机构证件类型；说明：如无以上信息请联系应急中心
				 */
				param.put("cardType", CardTypeEnum.CARD_TYPE_1.getValue());
				userinfo.setCardType(CardTypeEnum.CARD_TYPE_1.getValue());
				/**
				 * 个人证件号（身份证；护照；台湾居民来往大陆通行证；港澳居民来往内地通行证；
				 * 外国人永久居留身份证）企业证件号（五证合一号；三证合一号）。
				 * 说明：个人证件号必须脱敏后4 位；所有企业证件号不可以脱敏。如果网贷机构暂时没有记录企业三证合一号或者五证合一号，
				 * 可以去国家企业信用信息公示系统等网站查询获得，完善信息之后再上报。
				 */
				param.put("userIdcard", CommonStringUtils.idEncryptAfterFour(userInfo.getCertificateNo()));
				userinfo.setUserIdcard(CommonStringUtils.idEncryptAfterFour(userInfo.getCertificateNo()));
				/**
				 * 用户明文的证件号进行加密后的hash 值（hash 值为64 位随机数字和字母）。加密方法需使用应急中心提供的工具包( 下载地址:”https://open.ifcert.org.cn”)，
				 * 按照idCardHash 方法生成的hash 值。此字段是散标接口、交易流水接口、还款计划、初始债权、转让项目、承接项目的关联字段.
				 */
				param.put("userIdcardHash", HashAndSaltUtil.tool.idCardHash(userInfo.getCertificateNo()));
				userinfo.setUserIdcardHash(HashAndSaltUtil.tool.idCardHash(userInfo.getCertificateNo()));
				/**
				 * 手机号为自然人手机号或企业法人手机号。若用户类型是1-自然人,则本项需填写自然人手机号；
				 * 若用户类型是2-企业,则本项传法人手机号码。说明：必须脱敏后4 位.
				 */
				param.put("userPhone", CommonStringUtils.mobileEncryptAfterFour(userInfo.getName()));
				userinfo.setUserPhone(CommonStringUtils.mobileEncryptAfterFour(userInfo.getName()));
				// 获取用户手机号码的Hash和Salt值.
				Map<String, String> hashAndSaltMap = HashAndSaltUtil.getPhoneHashAndSalt(userInfo.getName());
				/**
				 * 用户明文的手机号进行加密后的hash 值（hash 值为64 位随机数字和字母）。加密方法需使用应急中心提供的工具包( 下载地址:”https://open.ifcert.org.cn”)，
				 * 按照phoneHash 方法生成的hash 值。
				 */
				param.put("userPhoneHash", hashAndSaltMap.get("userPhoneHash"));
				userinfo.setUserPhoneHash(hashAndSaltMap.get("userPhoneHash"));
				/**
				 * 使用工具包中phoneHash 方法生成的salt 值.
				 */
				param.put("userUuid", hashAndSaltMap.get("userUuid"));
				userinfo.setUserUuid(hashAndSaltMap.get("userUuid"));
				/**
				 * 用户类型是企业必填，用户类型是自然人填写-1.
				 * 说明：如果网贷机构暂时没有记录此项信息，可以去国家企业信用信息公示系统等网站查询获得，完善信息之后再上报。
				 */
				param.put("userLawperson", UserLawPersonEnum.USER_LAW_PERSON_NEGATIVE_1.getValue());
				userinfo.setUserLawperson(UserLawPersonEnum.USER_LAW_PERSON_NEGATIVE_1.getValue());
				/**
				 * 注册资本，单位：万元.
				 * 用户类型是企业必填，用户类型是自然人填写-1。
				 * 说明：币种不是人民币的按照汇率转化成人民币对应的金额.
				 * 说明：如果网贷机构暂时没有记录此项信息，可以去国家企业信用信息公示系统等网站查询获得，完善信息之后再上报。
				 */
				param.put("userFund", UserFundEnum.USER_FUND_NEGATIVE_1.getValue());
				userinfo.setUserFund(UserFundEnum.USER_FUND_NEGATIVE_1.getValue());
				/**
				 * 注册省份：用户归属地的行政区号。用户类型是企业必填，用户类型是自然人填写-1，使用工具包中getCompanyAscription 方法取得（）.
				 */
				param.put("userProvince", UserProvinceEnum.USER_PROVINCE_NEGATIVE_1.getValue());
				userinfo.setUserProvince(UserProvinceEnum.USER_PROVINCE_NEGATIVE_1.getValue());
				/**
				 * 注册地址：用户类型是企业必填，用户类型是自然人填写-1。
				 * 说明：如果网贷机构暂时没有记录此项信息，可以去国家企业信用信息公示系统等网站查询获得，完善信息之后再上报。
				 */
				param.put("userAddress", UserAddressEnum.USER_ADDRESS_NAGATIVE_1.getValue());
				userinfo.setUserAddress(UserAddressEnum.USER_ADDRESS_NAGATIVE_1.getValue());
				/**
				 * 企业注册时间：用户类型是企业必填，用户类型是自然人填写-1，格式"yyyy-MM-dd".
				 * 说明：如果网贷机构暂时没有记录此项信息，可以去国家企业信用信息公示系统等网站查询获得，完善信息之后再上报。
				 */
				param.put("registerDate", RegisterDateEnum.REGISTER_DATE_NAGATIVE_1.getValue());
				userinfo.setRegisterDate(RegisterDateEnum.REGISTER_DATE_NAGATIVE_1.getValue());
				/**
				 * 用户性别：用户类型是自然人必填，用户类型是企业填写-1。
				 * 1：男；0：女；
				 */
				param.put("userSex", UserIdCardUtil.getGenderByIdCard(userInfo.getCertificateNo()));
				userinfo.setUserSex(UserIdCardUtil.getGenderByIdCard(userInfo.getCertificateNo()));
				/**
				 * 用户交易时使用的银行卡号。如果是存管银行，则按存管银行返回的脱敏数据推送。
				 * 说明：采用JSON数组方式组织数据， 如："userList":[{"userBankAccount":"6228480240389521611"},{"userBankAccount":"6228480240389521612"}].
				 */
				List<Map<String, String>> userBankAccountlist = new ArrayList<Map<String, String>>();
				CgbUserBankCard cgbUserBankCard = new CgbUserBankCard();
				cgbUserBankCard.setState(CgbUserBankCard.CERTIFY_YES); // 银行卡已认证.
				cgbUserBankCard.setUserId(userInfo.getId()); // 出借人ID.
				List<CgbUserBankCard> cgbUserBankCardList = cgbUserBankCardDao.findList(cgbUserBankCard);
				// 用户银行卡号列表，使用逗号拼接.
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < cgbUserBankCardList.size(); i++) {
					Map<String, String> userBankAccountMap = new LinkedHashMap<String, String>();
					userBankAccountMap.put("userBankAccount", StringUtils.replaceBlanK(cgbUserBankCardList.get(i).getBankAccountNo()));
					userBankAccountlist.add(userBankAccountMap);
					if (sb.length() > 0) {// 该步即不会第一位有逗号，也防止最后一位拼接逗号！
						sb.append(",");
					}
					sb.append(StringUtils.replaceBlanK(cgbUserBankCardList.get(i).getBankAccountNo()));
				}
				param.put("userList", userBankAccountlist);
				userinfo.setUserBankAccount(sb.toString());
				userinfo.setBatchNum(batchNum); // 批次号.
				userinfo.setSendTime(sentTime); // 发送时间.
				int insert = userinfoDao.insert(userinfo);
				if (insert == 1) {
					log.info("出借人用户信息插入成功！");
				} else {
					log.info("出借人用户信息插入失败！");
				}
			}
			list.add(param);
			JSONObject json = new JSONObject();
			String nonce = Integer.toHexString(new Random().nextInt());
			json.accumulate("version", ServerURLConfig.VERSION); // 接口版本号.
			json.accumulate("batchNum", batchNum); // 批次号.
			json.accumulate("checkCode", tool.checkCode(list.toString())); // 工具包checkCode方法生成.
			json.accumulate("totalNum", list.size() + "");// 本批次发送的总数据条数，一个批次最多传3000 条数据.
			json.accumulate("sentTime", sentTime); // 发送时间.
			json.accumulate("sourceCode", ServerURLConfig.SOURCE_CODE); // 平台编码（网贷机构平台在应急中心系统的唯一编号，网贷机构在应急中心系统注册实名之后自动生成）.
			json.accumulate("infType", InfTypeEnum.INF_TYPE_1.getValue());// 用户接口，传值样例：1
			json.accumulate("dataType", Global.getConfig("DATA_TYPE"));// 接口数据类型；0：调试数据；1 正式数据（接口联调阶段传0，正式推数据阶段传1）
			json.accumulate("timestamp", currentTimeMillis + "");// 获取当前系统时间戳 long timestamp = System.currentTimeMillis();
			json.accumulate("nonce", nonce); // 随机数String nonce = Integer.toHexString(new Random().nextInt());
			json.accumulate("dataList", list); // 接口数据列表.

			// System.out.println("出借人用户信息接口推送数据：" + json);
			log.info("单个出借人用户信息接口推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_USERINFO, "utf-8");
			// System.out.println("出借人用户信息接口推送响应：" + responseStr);
			log.info("单个出借人用户信息接口推送响应：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_1.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_01.getValue());
					bn.setCode(code);
					int insert = batchNumDao.insert(bn);
					if (insert == 1) {
						log.info("该批次数据状态信息插入成功！");
					} else {
						log.info("该批次数据状态信息插入失败！");
					}
					result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_0000.getValue());
					result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_0000.getText());
				} else { // 该批次数据推送失败.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_1.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
					bn.setCode(code);
					int insert = batchNumDao.insert(bn);
					if (insert == 1) {
						log.info("该批次数据状态信息插入成功！");
					} else {
						log.info("该批次数据状态信息插入失败！");
					}
					result.put("respCode", code);
					result.put("respMsg", "参考数据接入手册，错误码对照表");
				}
			} else { // 该批次数据推送失败.
				BatchNum bn = new BatchNum();
				bn.setId(IdGen.uuid());
				bn.setBatchNum(batchNum);
				bn.setSendTime(sentTime);
				bn.setInfType(InfTypeEnum.INF_TYPE_1.getValue());
				bn.setTotalNum(list.size() + "");
				bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
				bn.setCode("resp null");
				int insert = batchNumDao.insert(bn);
				if (insert == 1) {
					log.info("该批次数据状态信息插入成功！");
				} else {
					log.info("该批次数据状态信息插入失败！");
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_02.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
			BatchNum bn = new BatchNum();
			bn.setId(IdGen.uuid());
			bn.setBatchNum(batchNum);
			bn.setSendTime(sentTime);
			bn.setInfType(InfTypeEnum.INF_TYPE_1.getValue());
			bn.setTotalNum(list.size() + "");
			bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
			bn.setCode("exception");
			int insert = batchNumDao.insert(bn);
			if (insert == 1) {
				log.info("该批次数据状态信息插入成功！");
			} else {
				log.info("该批次数据状态信息插入失败！");
			}
			result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
			result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_04.getText());
		}

		return result;
	}
}
