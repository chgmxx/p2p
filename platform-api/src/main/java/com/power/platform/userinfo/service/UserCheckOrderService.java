package com.power.platform.userinfo.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.pay.cash.entity.UserCash;
import com.power.platform.pay.cash.service.UserCashService;
import com.power.platform.pay.config.PartnerConfig;
import com.power.platform.pay.conn.HttpRequestSimple;
import com.power.platform.pay.recharge.entity.UserRecharge;
import com.power.platform.pay.recharge.service.UserRechargeService;
import com.power.platform.pay.utils.LLPayUtil;
import com.power.platform.pay.vo.CashBean;
import com.power.platform.pay.vo.CashStateInfo;
import com.power.platform.sys.entity.User;
import com.power.platform.userinfo.dao.UserCheckOrderDao;
import com.power.platform.userinfo.entity.UserCheckOrder;


/**
 * 订单对账Service
 * @author yb
 * @version 2017-09-04
 */
@Service("userCheckOrderService")
public class UserCheckOrderService extends CrudService<UserCheckOrder> {

	@Resource
	private UserCheckOrderDao userCheckOrderDao;
	@Resource
	private UserRechargeService userRechargeService;
	@Resource
	private UserCashService userCashService;
	
	public UserCheckOrder get(String id) {
		return super.get(id);
	}
	
	public List<UserCheckOrder> findList(UserCheckOrder userCheckOrder) {
		return super.findList(userCheckOrder);
	}
	
	public Page<UserCheckOrder> findPage(Page<UserCheckOrder> page, UserCheckOrder userCheckOrder) {
		return super.findPage(page, userCheckOrder);
	}
	
	@Transactional(readOnly = false)
	public void save(UserCheckOrder userCheckOrder) {
		super.save(userCheckOrder);
	}
	
	@Transactional(readOnly = false)
	public void delete(UserCheckOrder userCheckOrder) {
		super.delete(userCheckOrder);
	}

	@Override
	protected CrudDao<UserCheckOrder> getEntityDao() {
		// TODO Auto-generated method stub
		return userCheckOrderDao;
	}

	/**
	 * 订单对账清空表数据
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int deleteAll() {
		// TODO Auto-generated method stub
		return userCheckOrderDao.deleteAll();
	}

	/**
	 * 用户充值提现订单查询对账
	 * @throws ParseException 
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void checkOrder() throws ParseException {
		// TODO Auto-generated method stub
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    Date now = new Date();
        String beginEndDate = sdf.format(now);
        String endBeginDate = DateUtils.getSpecifiedDayAfter(beginEndDate);
        
		//N1.查询当日充值记录,获取当日所有充值订单号
		UserRecharge entity = new UserRecharge();
		entity.setBeginBeginDate(sdf.parse(beginEndDate));
		entity.setEndBeginDate(sdf.parse(endBeginDate));
		List<UserRecharge> rechargeList = userRechargeService.findList(entity);
		if(rechargeList!=null && rechargeList.size()>0){
			for(UserRecharge userRecharge:rechargeList){
				//N2.根据订单号查询订单状态是否成功,并记录失败状态的记录
				if(userRecharge.getSn()!=null){
					String sn = userRecharge.getSn();
					logger.info("充值订单"+sn+"对账开始");
			        String resJson = orderQuery(sn);
			        JSONObject jsonObject = JSONObject.parseObject(resJson);
			        String retCode = jsonObject.getString("ret_code");
			        String retMsg = jsonObject.getString("ret_msg");
			        logger.info("充值对账查询结果"+retCode+"#####"+retMsg);
			        if(retCode.equals("0000")){
			        	logger.info("记录充值对账结果");
			        	String resultPay = jsonObject.getString("result_pay");//支付结果
			        	String moneyOrder = jsonObject.getString("money_order");//交易金额
			            String memo = jsonObject.getString("memo");//支付备注
			            String cardNo = jsonObject.getString("card_no");//银行卡号
			            logger.info("充值对账结果"+resultPay+"###"+moneyOrder+"###"+memo+"###"+cardNo);
			            if(!resultPay.equals("SUCCESS")){
			            	//N3.失败记录计录
			            	UserCheckOrder userCheckOrder = new UserCheckOrder();
			            	userCheckOrder.setId(String.valueOf(IdGen.randomLong()));
			            	userCheckOrder.setName(userRecharge.getUserInfo().getRealName());
			            	userCheckOrder.setPhone(userRecharge.getUserInfo().getName());
			            	userCheckOrder.setSn(sn);
			            	userCheckOrder.setAmount(moneyOrder);
			            	userCheckOrder.setOrderDate(userRecharge.getBeginDate());
			            	String state = resultPay.equals("WAITING")?"等待支付":(resultPay.equals("PROCESSING")?"银行支付处理中":(resultPay.equals("REFUND")?"退款":"失败"));
			            	userCheckOrder.setState(state);
			            	userCheckOrder.setRemarks(memo);
			            	userCheckOrder.setType("充值");
			            	int i = userCheckOrderDao.insert(userCheckOrder);
			            	if(i>0){
			            		logger.info("充值订单号"+sn+"记录成功");
			            	}
			            }
			        }else{
			        	logger.info(retCode+"#####"+retMsg);
			        }
				}else{
					logger.info("充值ID为"+userRecharge.getId()+"暂无订单号");
				}
			}
		}
		//N3.查询当日提现记录,获取当日所有提现订单号
		UserCash entity1 = new UserCash();
		entity1.setBeginBeginDate(sdf.parse(beginEndDate));
		entity1.setEndBeginDate(sdf.parse(endBeginDate));
		List<UserCash> cashList = userCashService.findList(entity1);
		if(cashList!=null && cashList.size()>0){
			for(UserCash userCash:cashList){
				if(userCash.getSn()!=null){
					String sn = userCash.getSn();
					logger.info("提现订单"+sn+"对账开始");
					String resJson = orderQuery(sn);
			        JSONObject jsonObject = JSONObject.parseObject(resJson);
			        String retCode = jsonObject.getString("ret_code");
			        String retMsg = jsonObject.getString("ret_msg");
			        logger.info("提现对账查询结果"+retCode+"#####"+retMsg);
			        if(retCode.equals("0000")){
			        	String resultPay = jsonObject.getString("result_pay");//支付结果
			        	String moneyOrder = jsonObject.getString("money_order");//交易金额
			            String memo = jsonObject.getString("memo");//支付备注
			            String cardNo = jsonObject.getString("card_no");//银行卡号
			            logger.info("提现对账结果"+resultPay+"###"+moneyOrder+"###"+memo+"###"+cardNo);
			            if(!resultPay.equals("SUCCESS")){
			            	//N4.根据订单号查询订单状态是否成功,并记录失败状态的记录
			            	UserCheckOrder userCheckOrder = new UserCheckOrder();
			            	userCheckOrder.setId(String.valueOf(IdGen.randomLong()));
			            	userCheckOrder.setName(userCash.getUserInfo().getRealName());
			            	userCheckOrder.setPhone(userCash.getUserInfo().getName());
			            	userCheckOrder.setSn(sn);
			            	userCheckOrder.setAmount(moneyOrder);
			            	userCheckOrder.setOrderDate(userCash.getBeginDate());
			            	String state = resultPay.equals("WAITING")?"等待支付":(resultPay.equals("PROCESSING")?"银行支付处理中":(resultPay.equals("REFUND")?"退款":"失败"));
			            	userCheckOrder.setState(state);
			            	userCheckOrder.setRemarks(memo);
			            	userCheckOrder.setType("提现");
			            	int i = userCheckOrderDao.insert(userCheckOrder);
			            	if(i>0){
			            		logger.info("提现订单号"+sn+"记录成功");
			            	}
			            }
			        }else{
			        	logger.info(retCode+"#####"+retMsg);
			        }
				}else{
					logger.info("提现ID为"+userCash.getId()+"暂无订单号");
				}
			}
		}
	}
	
	/**
	 * 请求连连支付订单查询接口
	 * @param sn
	 * @return
	 */
	public String orderQuery(String sn){
		CashStateInfo orderBean = new CashStateInfo();
		orderBean.setOid_partner(PartnerConfig.OID_PARTNER);
		orderBean.setSign_type(PartnerConfig.SIGN_TYPE);
		orderBean.setNo_order(sn);
		orderBean.setDt_order(DateUtils.getDateStr());
		orderBean.setQuery_version("1.1");
	 	  // 加签名
	    String sign = LLPayUtil.addSign(JSON.parseObject(JSON
	            .toJSONString(orderBean)), PartnerConfig.TRADER_PRI_KEY,
	            PartnerConfig.MD5_KEY);
	    orderBean.setSign(sign);
        String reqJson = JSON.toJSONString(orderBean);

        HttpRequestSimple httpclent =  HttpRequestSimple.getInstance();
        String resJson = httpclent.postSendHttp("https://queryapi.lianlianpay.com/orderquery.htm",
                reqJson);
        System.out.println("结果报文为:" + resJson) ;
        return resJson;
	}

	public List<UserCheckOrder> findAllList() {
		// TODO Auto-generated method stub
		return userCheckOrderDao.findAllList();
	}
	
}