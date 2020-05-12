package com.power.platform.pay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.pay.config.PartnerConfig;
import com.power.platform.pay.conn.HttpRequestSimple;
import com.power.platform.pay.utils.LLPayUtil;
import com.power.platform.pay.vo.CashBean;
import com.power.platform.pay.vo.CashStateInfo;
import com.power.platform.pay.vo.GateWayPayBean;
import com.power.platform.pay.vo.AuthPayInfo;
import com.power.platform.pay.vo.PrcptcdInfo;

/**
 * 
 * @author 连连支付工具类
 *
 */
public class LianLianPayUtils {
	/**
	 *  代付方法
	 * @param userId 用户id
	 * @param realName 真实姓名
	 * @param cardNo 银行卡号
	 * @param bankCode 银行编码（有银行卡号则可以不传）
	 * @param cityCode 城市编码
	 * @param brabankNname 开户行关键字
	 * @param money 代付金额
	 * @param infoOrder 订单描述
	 * @param flagCard 0对私 1对公
	 * @param notifyUrl 代付结果服务器异步通知地址（）
	 * @return Map 返回
	 *        key ret_code 结果编码  ret_msg 编码描述
	 *            on_order 订单号     dt_order 订单时间 （这两个字段需要保存起来用来做状态查询）
	 */
	 public static Map<String,Object> goCashPay(String userId,String realName,String cardNo,String bankCode,String cityCode,String brabankName,String money,String infoOrder,String notifyUrl,String flagCard)
	    {
		 	CashBean reqBean =new CashBean();
		 	reqBean.setPlatform("win11.com");
		 	reqBean.setOid_partner(PartnerConfig.OID_PARTNER);
		 	reqBean.setSign_type(PartnerConfig.SIGN_TYPE);
	     //   reqBean.setApi_version(PartnerConfig.VERSION_DF);
	        String noOrder =DateUtils.getCurrentDateTimeStr();
	        reqBean.setNo_order(noOrder);
	        //String dtOrder =LLPayUtil.getCurrentDateTimeStr(new Date());
	      //  reqBean.setDt_order(dtOrder);
	        reqBean.setMoney_order(money);
	        reqBean.setFlag_card(flagCard);
	        reqBean.setCard_no(cardNo);
	        reqBean.setAcct_name(realName);
	        reqBean.setInfo_order(infoOrder);
	        reqBean.setNotify_url(notifyUrl);
	        reqBean.setPrcptcd(getPrcptcd(bankCode, cardNo, brabankName, cityCode));
	/*      有大额行号，则不需要省、城市编码和开户行      
	        reqBean.setBrabank_name(brabankName);
	        reqBean.setBank_code(bankCode);
	        reqBean.setCity_code(cityCode);*/
	        reqBean.setUser_id(userId);
	      
		 	  // 加签名
		    String sign = LLPayUtil.addSign(JSON.parseObject(JSON
		            .toJSONString(reqBean)), PartnerConfig.TRADER_PRI_KEY,
		            PartnerConfig.MD5_KEY);
		    reqBean.setSign(sign);
	        String reqJson = JSON.toJSONString(reqBean);

	        HttpRequestSimple httpclent =  HttpRequestSimple.getInstance();
	        String resJson = httpclent.postSendHttp(PartnerConfig.SERVER + "cardandpay.htm",
	                reqJson);
	        System.out.println("结果报文为:" + resJson) ;
	        Map<String,Object> resultmap = new HashMap<String,Object>();
	        
	        resultmap.put("ret_code",JSON.parseObject(resJson).get("ret_code"));
	        resultmap.put("ret_msg",JSON.parseObject(resJson).get("ret_msg"));
	        resultmap.put("on_order", noOrder);
	      //  resultmap.put("dt_order", dtOrder);
	        return resultmap;
	    }
	 
	 /**
	  * 充值订单查询
	  * @param noOrder
	  * @param dtOrder
	  * @return
	  */
	 public static Map<String,Object> getGoPayState(String noOrder,String dtOrder){
		 return getLianLianState(noOrder, "", dtOrder);
	 }
	 
	 /**
	  * 代付订单查询
	  * @param noOrder
	  * @param typeDc
	  * @param dtOrder
	  * @return
	  */
	 public static Map<String,Object> getCashPayState(String noOrder,String typeDc,String dtOrder){
		 return getLianLianState(noOrder, typeDc, dtOrder);
	 }
	 
	 /**
	  * 连连状态查询
	  * @param noOrder
	  * @param typeDc 1 代付 支付查询不需要传
	  * @return json
	  */
	 private static  Map<String,Object> getLianLianState(String noOrder,String typeDc,String dtOrder){
		 CashStateInfo  cashStateInfo =new CashStateInfo();
		 cashStateInfo.setOid_partner(PartnerConfig.OID_PARTNER);
		 cashStateInfo.setSign_type(PartnerConfig.SIGN_TYPE);
		 cashStateInfo.setQuery_version(PartnerConfig.VERSION);
		 if(StringUtils.isNotBlank(dtOrder)){
			 cashStateInfo.setNo_order(noOrder);
		 }
		 if(StringUtils.isNotBlank(dtOrder)){
			 cashStateInfo.setDt_order(dtOrder);
		 }
		 if(StringUtils.isNotBlank(typeDc)){
			 cashStateInfo.setType_dc(typeDc);
		 }
		 String sign = LLPayUtil.addSign(JSON.parseObject(JSON
		            .toJSONString(cashStateInfo)), PartnerConfig.TRADER_PRI_KEY,
		           PartnerConfig.MD5_KEY);
		 cashStateInfo.setSign(sign);
		 String reqJson = JSON.toJSONString(cashStateInfo);
		 HttpRequestSimple httpclent =  HttpRequestSimple.getInstance();
	     String resJson = httpclent.postSendHttp(PartnerConfig.SERVER + "orderquery.htm",reqJson);
	     Map<String,Object> resultmap = new HashMap<String,Object>();
	     resultmap =JSON.parseObject(resJson);
		 return resultmap;
	 }
	 
	 /**
		 * 获取大额行号  CNAPSCodeQuery.htm
		 * @param bank_code 银行编码
		 * @param card_no 银行账号
		 * @param brabank_name 开户支行名称
		 * @param city_code 开户行所在市编码
		 * @return
		 */
	public static String getPrcptcd(String bankCode,String cardNo,String brabankName,String cityCode){
			PrcptcdInfo prcptcdInfo =new PrcptcdInfo();
			prcptcdInfo.setOid_partner(PartnerConfig.OID_PARTNER);
			prcptcdInfo.setSign_type(PartnerConfig.SIGN_TYPE);
			prcptcdInfo.setBank_code(bankCode);
			prcptcdInfo.setCard_no(cardNo);
			prcptcdInfo.setBrabank_name(brabankName);
			prcptcdInfo.setCity_code(cityCode);
			
		    String sign = LLPayUtil.addSign(JSON.parseObject(JSON
		            .toJSONString(prcptcdInfo)), PartnerConfig.TRADER_PRI_KEY,
		            PartnerConfig.MD5_KEY);
		    prcptcdInfo.setSign(sign);
			String reqJson = JSON.toJSONString(prcptcdInfo);
			HttpRequestSimple httpclent =  HttpRequestSimple.getInstance();
		    String resJson = httpclent.postSendHttp(PartnerConfig.SERVER + "CNAPSCodeQuery.htm",
		                reqJson);
		    System.out.println("结果报文为:" + resJson) ;
		    JSONArray  prcptcdstr = (JSONArray)JSON.parseObject(resJson).get("card_list");
		     String prcptcdstrBack = "";
		        if(null!=prcptcdstr  && prcptcdstr.size()>0){
		        	 for(int i =0 ;i< prcptcdstr.size();i++){
		             	String tempa = prcptcdstr.get(i).toString();
		             	System.out.println("prcptcd in jsonarray ------------  "+JSON.parseObject(tempa).get("prcptcd"));
		             	prcptcdstrBack = String.valueOf(JSON.parseObject(tempa).get("prcptcd"));
		             }       
		        }
			return prcptcdstrBack;
		}
	
	/**
	 * 连连支付充值，认证支付
	 * @param authPayBean
	 * @return
	 */
	/*public LinkedHashMap<String, String> authPay(AuthPayBean authPayBean) {
		// 构造支付请求对象
	    AuthPayInfo paymentInfo = new AuthPayInfo();
	    paymentInfo.setVersion(PartnerConfig.VERSION);
	    paymentInfo.setOid_partner(PartnerConfig.OID_PARTNER);
	    paymentInfo.setUser_id(authPayBean.getUserId());
	    paymentInfo.setSign_type(PartnerConfig.SIGN_TYPE);
	    paymentInfo.setBusi_partner(PartnerConfig.BUSI_PARTNER);
	    paymentInfo.setNo_order(authPayBean.getOrderId());
	//    paymentInfo.setDt_order(LLPayUtil.getCurrentDateTimeStr(new Date()));
	    paymentInfo.setName_goods("中投摩根充值");
	    paymentInfo.setInfo_order("中投摩根充值");
	    paymentInfo.setMoney_order(authPayBean.getAmount());
	    paymentInfo.setNotify_url(PartnerConfig.RECHARGE_NOTIFY_URL);
	    paymentInfo.setUrl_return(PartnerConfig.RECHARGE_CALLBACK_URL);
	    paymentInfo.setUserreq_ip(authPayBean.getIp());
	    paymentInfo.setUrl_order("");
	    paymentInfo.setValid_order("10080");// 单位分钟，可以为空，默认7天
	    paymentInfo.setRisk_item(createAuthPayRiskItem(authPayBean.getUserId(),authPayBean.getRegisterDate(),authPayBean.getRealName(),authPayBean.getCertificateNo()));
	  //  paymentInfo.setTimestamp(LLPayUtil.getCurrentDateTimeStr(new Date()));
	    // 商戶从自己系统中获取用户身份信息（认证支付必须将用户身份信息传输给连连，且修改标记flag_modify设置成1：不可修改）
		paymentInfo.setId_type("0");
		paymentInfo.setId_no(authPayBean.getCertificateNo());
		paymentInfo.setAcct_name(authPayBean.getRealName());
		paymentInfo.setFlag_modify("1");	
		// 协议号和卡号同时存在时，优先将协议号送给连连，不要将协议号和卡号都送给连连
	    if (!LLPayUtil.isnull(authPayBean.getNoAgree())){
	        paymentInfo.setNo_agree(authPayBean.getNoAgree());
	    } else{
	      paymentInfo.setCard_no(authPayBean.getBankCard());
	    }
	    paymentInfo.setBack_url("http://www.lianlianpay.com/");
	    // 加签名
	    String sign = LLPayUtil.addSign(JSON.parseObject(JSON
	            .toJSONString(paymentInfo)), PartnerConfig.TRADER_PRI_KEY,
	            PartnerConfig.MD5_KEY);
	    paymentInfo.setSign(sign);
	      LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
	      params.put("version", paymentInfo.getVersion());
	      params.put("oid_partner", paymentInfo.getOid_partner());
	      params.put("user_id", paymentInfo.getUser_id());
	      params.put("sign_type", paymentInfo.getSign_type());
	      params.put("busi_partner", paymentInfo.getBusi_partner());
	      params.put("no_order", paymentInfo.getNo_order());
	      params.put("dt_order", paymentInfo.getDt_order());
	      params.put("name_goods", paymentInfo.getName_goods());
	      params.put("info_order", paymentInfo.getInfo_order());
	      params.put("money_order", paymentInfo.getMoney_order());
	      params.put("notify_url", paymentInfo.getNotify_url());
	      params.put("url_return", paymentInfo.getUrl_return());
	      params.put("userreq_ip", paymentInfo.getUserreq_ip());
	      params.put("url_order", paymentInfo.getUrl_order());
	      params.put("valid_order", paymentInfo.getValid_order());
	      params.put("timestamp", paymentInfo.getTimestamp());
	      params.put("bank_code", paymentInfo.getBank_code());
	      params.put("sign", paymentInfo.getSign());
	      params.put("risk_item", paymentInfo.getRisk_item());
	      params.put("no_agree", paymentInfo.getNo_agree());
	      params.put("id_type", paymentInfo.getId_type());
	      params.put("id_no", paymentInfo.getId_no());
	      params.put("acct_name", paymentInfo.getAcct_name());
	      params.put("flag_modify", paymentInfo.getFlag_modify());
	      params.put("card_no", paymentInfo.getCard_no());
	      params.put("back_url", paymentInfo.getBack_url());
		  return params;
	}*/
	/**
	 * 连连支付充值，网银支付
	 * @param gateWayPayBean
	 * @return
	 */
	public LinkedHashMap<String, String> gateWayPay(GateWayPayBean gateWayPayBean) {
		// 构造支付请求对象
		AuthPayInfo paymentInfo = new AuthPayInfo();
		paymentInfo.setVersion(PartnerConfig.VERSION);
		paymentInfo.setOid_partner(PartnerConfig.OID_PARTNER);
		paymentInfo.setUser_id(gateWayPayBean.getUserId());
		paymentInfo.setSign_type(PartnerConfig.SIGN_TYPE);
		paymentInfo.setBusi_partner(PartnerConfig.BUSI_PARTNER);
		paymentInfo.setNo_order(gateWayPayBean.getOrderId());
	//	paymentInfo.setDt_order(LLPayUtil.getCurrentDateTimeStr(new Date()));
		paymentInfo.setName_goods("中投摩根充值");
		paymentInfo.setInfo_order("中投摩根充值");
		paymentInfo.setMoney_order(gateWayPayBean.getAmount());
	//	paymentInfo.setNotify_url(PartnerConfig.RECHARGE_NOTIFY_URL);
	//    paymentInfo.setUrl_return(PartnerConfig.RECHARGE_CALLBACK_URL);
		paymentInfo.setUserreq_ip(gateWayPayBean.getIp());
		paymentInfo.setUrl_order("");
		paymentInfo.setValid_order("10080");// 单位分钟，可以为空，默认7天
		paymentInfo.setBank_code(gateWayPayBean.getBankCode());
	//	paymentInfo.setTimestamp(LLPayUtil.getCurrentDateTimeStr(new Date()));
		paymentInfo.setRisk_item(createGateWayRiskItem(gateWayPayBean.getUserId(),gateWayPayBean.getRegisterDate()));   
		paymentInfo.setPay_type("1");
		// 加签名
		String sign = LLPayUtil.addSign(JSON.parseObject(JSON
				.toJSONString(paymentInfo)), PartnerConfig.TRADER_PRI_KEY,
				PartnerConfig.MD5_KEY);
		paymentInfo.setSign(sign);
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("version", paymentInfo.getVersion());
		params.put("oid_partner", paymentInfo.getOid_partner());
		params.put("user_id", paymentInfo.getUser_id());
		params.put("sign_type", paymentInfo.getSign_type());
		params.put("busi_partner", paymentInfo.getBusi_partner());
		params.put("no_order", paymentInfo.getNo_order());
		params.put("dt_order", paymentInfo.getDt_order());
		params.put("name_goods", paymentInfo.getName_goods());
		params.put("info_order", paymentInfo.getInfo_order());
		params.put("money_order", paymentInfo.getMoney_order());
		params.put("notify_url", paymentInfo.getNotify_url());
		params.put("url_return", paymentInfo.getUrl_return());
		params.put("userreq_ip", paymentInfo.getUserreq_ip());
		params.put("url_order", paymentInfo.getUrl_order());
		params.put("valid_order", paymentInfo.getValid_order());
		params.put("timestamp", paymentInfo.getTimestamp());
		params.put("bank_code", paymentInfo.getBank_code());
		params.put("sign", paymentInfo.getSign());
		params.put("risk_item", paymentInfo.getRisk_item());
		params.put("pay_type", paymentInfo.getPay_type());
		return params;
	}

	 private String createAuthPayRiskItem(String userId,Date registerDate,String realName,String certificateNo){
		 	SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	        JSONObject riskItemObj = new JSONObject();
	        riskItemObj.put("frms_ware_category", "2009");  //required
	        riskItemObj.put("frms_is_real_name","0" );
	        riskItemObj.put("user_info_mercht_userno", userId);  //required
	        riskItemObj.put("user_info_dt_register",format.format(registerDate));     //required
	        riskItemObj.put("user_info_full_name", realName);  //required
	        riskItemObj.put("user_info_id_type", 0);
	        riskItemObj.put("user_info_id_no", certificateNo);          //required      //required
	        riskItemObj.put("user_info_identify_state",1);      //required
	        riskItemObj.put("user_info_identify_type", 4);   //required        
	        return riskItemObj.toString();
	 }
	 
	 private String createGateWayRiskItem(String userId,Date registerDate){
		 	SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	        JSONObject riskItemObj = new JSONObject();
	        riskItemObj.put("frms_ware_category", "2009");  //required
	        riskItemObj.put("user_info_mercht_userno", userId);  //required
	        riskItemObj.put("user_info_dt_register",format.format(registerDate));     //required
	        riskItemObj.put("user_info_id_type", 0);
	        riskItemObj.put("user_info_identify_state",0);      //required
	        return riskItemObj.toString();
	 } 
	/* 
	public String rechargeQuery(String orderId,Date createDate ) {
		
		 // 构造支付请求对象
		JSONObject reqObj = new JSONObject();
		reqObj.put("oid_partner",PartnerConfig.OID_PARTNER);
		reqObj.put("sign_type",PartnerConfig.SIGN_TYPE);
		reqObj.put("no_order", orderId);
		reqObj.put("dt_order", LLPayUtil.getCurrentDateTimeStr(recharge.getBeginDate()));
		reqObj.put("query_version", "1.1");
	    // 加签名
	    String sign = LLPayUtil.addSign(reqObj, PartnerConfig.TRADER_PRI_KEY,
	            PartnerConfig.MD5_KEY);
	    reqObj.put("sign", sign);
	    String reqJSON = reqObj.toString();
        logger.info("充值信息查询请求报文【" + reqJSON + "】");
        String resJSON = HttpRequestSimple.getInstance().postSendHttp(
                ServerURLConfig.RECHARGE_QUERY, reqJSON);
        
        if(!LLPayUtil.checkSign(resJSON, PartnerConfig.YT_PUB_KEY,
                PartnerConfig.MD5_KEY)){
        	throw new WinException("签名错误");
        }
                
        logger.info("充值信息查询返回结果【" + reqJSON + "】");
        JSONObject retObject = JSONObject.parseObject(resJSON);
        if("SUCCESS".equals(retObject.getString("result_pay"))){
        	recharge.setState(RechargeStateType.SUCCESS);
			recharge.setEndDate(retObject.getDate("settle_date"));
			
			WTrustAccount trustAccount = wTrustAccountMapper
					.selectByPrimaryKeyForLock(recharge.getTrustAccountId());
			if (trustAccount == null)
				throw new WinException("托管帐号不存在,rechargeId:" + recharge.getId()
						+ ", trustAccountId:" + recharge.getTrustAccountId());
			WUserAccount userAccount = wUserAccountMapper
					.selectByPrimaryKey(recharge.getUserId());
			if (userAccount == null)
				throw new WinException("用户帐号不存在,rechargeId:" + recharge.getId()
						+ ", userId:" + recharge.getUserId());
			recharge.setAmount(Double.parseDouble(retObject.getString("money_order")));
			recharge.setSn(retObject.getString("oid_paybill"));
			recharge.setBank(LianLianBankCode.dict.get(retObject.getString("bank_code")));

			trustAccount.setAvailableAmount(Win11Methods.add(trustAccount.getAvailableAmount()
					, Double.parseDouble(retObject.getString("money_order"))));
			trustAccount.setRechargeCount(trustAccount.getRechargeCount() + 1);
			trustAccount.setRechargeAmount(trustAccount.getRechargeAmount()
					+ Double.parseDouble(retObject.getString("money_order")));
			trustAccount.setTotalAmount(trustAccount.getTotalAmount()
					+ Double.parseDouble(retObject.getString("money_order")));

			userAccount.setAvailableAmount(userAccount.getAvailableAmount()
					+ Double.parseDouble(retObject.getString("money_order")));
			userAccount.setRechargeCount(userAccount.getRechargeCount() + 1);
			userAccount.setRechargeAmount(userAccount.getRechargeAmount()
					+ Double.parseDouble(retObject.getString("money_order")));
			userAccount.setTotalAmount(userAccount.getTotalAmount()
					+ Double.parseDouble(retObject.getString("money_order")));
			wTrustAccountMapper.updateByPrimaryKeySelective(trustAccount);
			wUserAccountMapper.updateByPrimaryKeySelective(userAccount);
			wRechargeMapper.updateByPrimaryKeySelective(recharge);
			
		
			WTransDetail wTransDetail = new WTransDetail();
			wTransDetail.setId(IdWorkers.idWorker.nextId());
			wTransDetail.setAmount(Double.parseDouble(retObject.getString("money_order")));
			wTransDetail.setInOutType(TransInOutType.IN);
			wTransDetail.setTransDate(new Date());
			wTransDetail.setUserId(recharge.getUserId());
			wTransDetail.setTransId(recharge.getId());
			wTransDetail.setTrustAccountNo(recharge.getTrustAccountNo());
			wTransDetail.setTrustType(TrustType.LIAN_LIAN);
			wTransDetail.setType(TransType.RECHARGE);
			wTransDetailMapper.insert(wTransDetail);
			if(!ProgramRuningModel.DEBUGH.equals(win11Props.getProgramRuningModel())){
				//发送为微信信息
				sendWeiXinMsg(recharge);
			}
			//抵用卷
			addVoucher(recharge);
		
			WUserBankCardExample bankExample = new WUserBankCardExample();
			WUserBankCardExample.Criteria bankCriteria = bankExample.createCriteria();
			bankCriteria.andUserIdEqualTo(recharge.getUserId());
			String lianlianBankNo = retObject.getString("card_no");
			bankCriteria.andBankAccountNoLike(lianlianBankNo !=null && lianlianBankNo.length()>4?"%"+lianlianBankNo.substring(lianlianBankNo.length()-4)+"%":"");
			List<WUserBankCard> bankCards = userBankCardMapper.selectByExample(bankExample);
			if(bankCards !=null && bankCards.size()>0 && bankCards.get(0).getState()==UserBankState.NO){
				WUserBankCard userBankCard = bankCards.get(0);
				userBankCard.setState(UserBankState.YES);
				userBankCard.setTrustAccountId(trustAccount.getId());
				userBankCard.setBindDate(new Date());
				userBankCard.setIsDefault(BankCardDefaultType.DEFAULT_YES);
				userBankCard.setBankNo(BankNoType.dict.get(retObject.getString("bank_code")));
				userBankCard.setSn(retObject.getString("oid_paybill"));
				userBankCardMapper.updateByPrimaryKeySelective(userBankCard);
			}
        }else if("FAILURE".equals(retObject.getString("result_pay"))){
        	recharge.setState(RechargeStateType.ERROR);
        	wRechargeMapper.updateByPrimaryKeySelective(recharge);
        }
        
		return "SUCCESS";
	}*/
	 /**
	  * 
	  * @return
	  *//*
	 public LinkedHashMap<String, String> cashPay() {
			String bankNo = cash.getBankAccount();				
			String bankcodeStr= queryCardBin(bankNo);
			JSONObject reqObj = JSON.parseObject(bankcodeStr);
			String bank_code = reqObj.getString("bank_code");		
			//构造提现对象
			int flag_card =0;
			CashBean reqBean = new CashBean();
	        reqBean.setApi_version(PartnerConfig.VERSION);
	        reqBean.setOid_partner(PartnerConfig.OID_PARTNER);
	        reqBean.setSign_type(PartnerConfig.SIGN_TYPE);
	        reqBean.setUser_id(cash.getUserId()+"");
	        reqBean.setAcct_name(user.getRealName());
	        reqBean.setCard_no(bankNo);
	        reqBean.setBank_code(bank_code);
	        reqBean.setNo_order(cash.getIdStr());
	        reqBean.setDt_order(LLPayUtil.getCurrentDateTimeStr(cash.getBeginDate()));
	       
	       
	        HttpRequestSimple httpclent =  HttpRequestSimple.getInstance();
	        
	        CashBean  reqBean2 = new CashBean();
	        reqBean2.setOid_partner(PartnerConfig.OID_PARTNER);
	        reqBean2.setSign_type(PartnerConfig.SIGN_TYPE);        
	        reqBean2.setCard_no(bankNo);
	        reqBean2.setBank_code(bank_code);
	        reqBean2.setBrabank_name(cash.getBrabankName());
	        reqBean2.setCity_code(cash.getCityCode());
	        
	       //RSA签名
	      	String sign2 = LLPayUtil.addSign(JSON.parseObject(JSON
	      			            .toJSONString(reqBean2)), PartnerConfig.TRADER_PRI_KEY,
	      			            PartnerConfig.MD5_KEY);
	      	reqBean2.setSign(sign2);
	        String reqJson2 = JSON.toJSONString(reqBean2);
	        String resCard =  httpclent.postSendHttp("https://yintong.com.cn/traderapi/" + "CNAPSCodeQuery.htm",reqJson2);        
	        logger.info("resCard --------  "+ resCard);           
	        logger.info("prcptcd ------------             "+JSON.parseObject(resCard).get("card_list"));
	        
	        JSONArray  prcptcdstr = (JSONArray)JSON.parseObject(resCard).get("card_list");
	        String prcptcdstrBack = "";
	        if(null!=prcptcdstr  && prcptcdstr.size()>0){
	        	 for(int i =0 ;i< prcptcdstr.size();i++){
	             	logger.info(prcptcdstr.get(0).toString());
	             	String tempa = prcptcdstr.get(0).toString();
	             	logger.info("prcptcd in jsonarray ------------  "+JSON.parseObject(tempa).get("prcptcd"));
	             	System.out.println("prcptcd in jsonarray ------------  "+JSON.parseObject(tempa).get("prcptcd"));
	             	prcptcdstrBack = String.valueOf(JSON.parseObject(tempa).get("prcptcd"));
	             }       
	             
	        }
	       
	        if(null!=user.getRealName() && user.getRealName().equals("朱志强")){
	        	 reqBean.setBrabank_name("什刹海支行");
	             reqBean.setCity_code("110000");
	             reqBean.setPrcptcd(prcptcdstrBack);
	        }    
	        
	        
	        reqBean.setBrabank_name(cash.getBrabankName());
	        reqBean.setCity_code(cash.getCityCode());
	        reqBean.setPrcptcd(prcptcdstrBack);
	        reqBean.setMoney_order(cash.getAmount()+"");
	        reqBean.setFlag_card("0");
	        reqBean.setInfo_order("中投摩根用户提现");
	        reqBean.setNotify_url(bgRetUrl);
	        
	        //RSA签名
	      	String sign = LLPayUtil.addSign(JSON.parseObject(JSON
	      			            .toJSONString(reqBean)), PartnerConfig.TRADER_PRI_KEY,
	      			            PartnerConfig.MD5_KEY);
	        reqBean.setSign(sign);       
	        String reqJson = JSON.toJSONString(reqBean);
	        System.out.println("提现签名串：-------  "+reqJson);
	        
	        String resJson = httpclent.postSendHttp("https://yintong.com.cn/traderapi/" + "cardandpay.htm",reqJson);
	        Map resultmap = new HashMap();
	        resultmap.put("ret_code",JSON.parseObject(resJson).get("ret_code"));
	        resultmap.put("ret_msg",JSON.parseObject(resJson).get("ret_msg"));
	        return resultmap;
		}	*/
}
