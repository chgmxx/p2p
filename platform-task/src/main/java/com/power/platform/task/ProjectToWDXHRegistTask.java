package com.power.platform.task;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.xml.namespace.QName;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.java.security.TrustAllTrustManager;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.security.SSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WGuaranteeCompany;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.userinfo.entity.UserInfo;

@Service("projectToWDXHRegistTask")
@Lazy(false)
public class ProjectToWDXHRegistTask {
	private static final Logger logger = Logger
			.getLogger(ProjectToWDXHRegistTask.class);

	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Autowired
	private WloanSubjectService wloanSubjectService;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private WloanTermInvestDao wloanTermInvestDao;
	@Autowired
	private  WloanTermProjectDao wloanTermProjectDao;
	
	/**
	 * 每天23点执行一次
	 * 
	 */
	@Scheduled(cron = "0 0 23 * * ?")
//	@Scheduled(cron = "0 11 11 * * ?")
	public void registerToWDXH() {

		try {
			logger.info("定时器ProjectToWDXHRegistTask产登项目开始");
			RPCServiceClient client = new RPCServiceClient();
			Options options = client.getOptions();
			 String address = "https://bjp2p.com.cn:8443/platformService?wsdl";//正式地址
//			String address = "https://test.bjp2p.com.cn:8443/platformService?wsdl";// 测试地址

			// String address = "http://localhost:8081/platformService";
			//
			// HTTPS需加下面的几行代码
			final SSLContext sslCtx = SSLContext.getInstance("TLS");
			sslCtx.init(null,
					new TrustManager[] { new TrustAllTrustManager() }, null);
			options.setProperty(
					HTTPConstants.CUSTOM_PROTOCOL_HANDLER,
					new Protocol(
							"https",
							(ProtocolSocketFactory) new SSLProtocolSocketFactory(
									sslCtx), 8443));

			EndpointReference epf = new EndpointReference(address);
			options.setTo(epf);

			String user = "ztmg";
			String pwd = "ztmg@123!~";

			String date = DateUtils.getDate(new Date(), "yyyy-MM-dd");
			String specifiedDayBefore = DateUtils.getSpecifiedDayBefore(date);
			
			String beginTimeFromOnline = specifiedDayBefore + " 23:00:00";// 开始时间
			String beginTimeFromFull = specifiedDayBefore + " 23:00:00";// 满标开始时间
			String endTimeToOnline = date + " 23:00:00";// 结束时间
			String endTimeToFull = date + " 23:00:00";// 结束时间
			
			// 散标（安心投）
			WloanTermProject project = new WloanTermProject();
			project.setBeginTimeFromOnline(beginTimeFromOnline);
			project.setEndTimeToOnline(endTimeToOnline);
			List<WloanTermProject> projectList = wloanTermProjectService.findList(project);
			QName qName = new QName(
					"http://supervise.service.app.mp.zkbc.net/",
					"productRegistration");
			for (WloanTermProject wloanTermProject : projectList) {
//				if(wloanTermProject.getProjectProductType().equals("1")){//安心投
					String scatteredinvestString = getScatteredinvestString(wloanTermProject);
					// //散标
					
					Object[] result = client.invokeBlocking(qName, new Object[] {
							user, pwd, scatteredinvestString },
							new Class[] { String.class });
					 System.out.println(result[0]);
//				}else if(wloanTermProject.getProjectProductType().equals("2")){//供应链
//					// 债权转让
//					String transferString =getTransferString(wloanTermProject);
//					Object[] result = client.invokeBlocking(qName, new Object[]{user, pwd,transferString}, new Class[]{String.class});
//			        System.out.println(result[0]);
//				}
				
			}
			
			//更新项目
			WloanTermProject project2 = new WloanTermProject();
			project2.setBeginTimeFromFull(beginTimeFromFull);
			project2.setEndTimeToFull(endTimeToFull);
			List<WloanTermProject> projectList2 = wloanTermProjectService.findList(project2);
			for(WloanTermProject wloanTermProject:projectList2){
				String updateString = getUpdateProductString(wloanTermProject,wloanTermProject.getState());
				QName qName2 = new QName("http://supervise.service.app.mp.zkbc.net/", "productStatusUpdate");
		        Object[]  result1 = client.invokeBlocking(qName2, new Object[]{user, pwd, updateString}, new Class[]{String.class});
		        System.out.println(result1[0]);
			}
			
			

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("定时器ProjectToWDXHRegistTask产登项目异常");
		}

	}
	
	/**
	 * 每天23点执行一次
	 * 
	 */
	@Scheduled(cron = "0 0 22 3 * ?")//每月三号晚上十点执行
//	@Scheduled(cron = "0 04 11 * * ?")//每月三号晚上十点执行
	public void manageInfo() {

		try {
			logger.info("定时器manageInfo产登项目开始");
			RPCServiceClient client = new RPCServiceClient();
			Options options = client.getOptions();
			// String address = "https://www.bjp2p.com.cn:8443/platformService";
			 String address = "https://bjp2p.com.cn:8443/platformService?wsdl";//正式地址
//			String address = "https://test.bjp2p.com.cn:8443/platformService?wsdl ";// 测试地址

			// String address = "http://localhost:8081/platformService";
			//
			// HTTPS需加下面的几行代码
			final SSLContext sslCtx = SSLContext.getInstance("TLS");
			sslCtx.init(null,
					new TrustManager[] { new TrustAllTrustManager() }, null);
			options.setProperty(
					HTTPConstants.CUSTOM_PROTOCOL_HANDLER,
					new Protocol(
							"https",
							(ProtocolSocketFactory) new SSLProtocolSocketFactory(
									sslCtx), 8443));

			EndpointReference epf = new EndpointReference(address);
			options.setTo(epf);

			String user = "ztmg";
			String pwd = "ztmg@123!~";

			//经营信息
	        String operationDataString  = getOperationData();
	        
	      //经营信息
	        QName qName = new QName("http://supervise.service.app.mp.zkbc.net/", "addOperationData");
	        Object[]  result2 = client.invokeBlocking(qName, new Object[]{user, pwd, operationDataString}, new Class[]{String.class});
	        System.out.println(result2[0]);

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("定时器manageInfo产登项目异常");
		}

	}
	
    public  String getOperationData(){
        Map ecmsgM = new LinkedHashMap<>();
        
        ecmsgM.put("source_code", "BJ20170413002");// 原平台注册编号
        String date = DateUtils.getDate();
        String date2 = DateUtils.getAddDaysDate(-30).toString();
        ecmsgM.put("upload_date", date);//上传数据时间
        ecmsgM.put("data_begin_period", date2);//统计开始时间
        ecmsgM.put("data_end_period", date);//统计结束时间
        
        Map map = new HashMap();
        map = wloanTermProjectDao.selectForWDXHOne();//累计借贷金额(元) 累计借款笔数
        Double totalLoanMoney =  (Double) map.get("total_loan_money");
//        Long totalLong = Long.parseLong(map.get("total_loan_money").toString());
        String str=new BigDecimal(totalLoanMoney).toString();
        ecmsgM.put("total_loan_money",str);//累计借贷金额(元)
        ecmsgM.put("total_loan_num", map.get("total_loan_num"));//累计借款笔数
        
        map = wloanTermProjectDao.selectForWDXHTwo();//累计借贷余额  累计借贷余额笔数  当前借款人数
        Double totalLoanBalanceMoney =  (Double) map.get("total_loan_balance_money");
        String str2=new BigDecimal(totalLoanBalanceMoney).toString();
        ecmsgM.put("total_loan_balance_money", str2);//累计借贷余额
        ecmsgM.put("total_loan_balance_num", map.get("total_loan_balance_num"));//累计借贷余额笔数
        ecmsgM.put("cur_borrow_users", map.get("cur_borrow_users"));//当期借款人数
        
        map = wloanTermProjectDao.selectForWDXHThree();//累计借款人数
        ecmsgM.put("total_borrow_users", map.get("total_borrow_users"));//累计借款人数
        
        map = wloanTermProjectDao.selectForWDXHFour();//累计投资人数
        ecmsgM.put("total_invest_users", map.get("total_invest_users"));//累计投资人数
        
        map = wloanTermProjectDao.selectForWDXHFive();//当前投资人数
        ecmsgM.put("cur_invest_users", map.get("cur_invest_users"));//当期投资人数
        
        
        ecmsgM.put("topten_repay_rate", "0%");//平台前十大融资人融资待还余额占比
        ecmsgM.put("top_repay_rate", "0%");//平台单一融资人最大融资待还余额占比
        ecmsgM.put("related_loan_money", "0");//关联关系借款总额
        ecmsgM.put("related_loan_num", "0");//关联关系借款笔数
        ecmsgM.put("overdue_loan_num", "0");//逾期笔数
        ecmsgM.put("overdue_loan_money", "0");//逾期欠款总额
        ecmsgM.put("overdue_ninety_loan_num", "0");//逾期90天以上的笔数
        ecmsgM.put("overdue_ninety_loan_money", "0");//逾期90天以上的总额
        ecmsgM.put("payed_risk_money", "0");//风险保证金代偿总额
        ecmsgM.put("payed_risk_num", "0");// 风险保证金代偿笔数
        ecmsgM.put("total_recharge", "0");// 充值手续费
        ecmsgM.put("total_deposit", "免费(2元/笔，第三方收取，目前由理财范为用户承担)");// 提现手续费
        ecmsgM.put("identity_auth_fee", "0");// 身份认证费
        ecmsgM.put("degree_auth_fee", "0");// 学历认证费
        ecmsgM.put("video_auth_fee", "0");//  视频认证费
        ecmsgM.put("interest_fee", "0");//利息管理费
        ecmsgM.put("service_fee", "年化1.75%-3%");// 服务费
        ecmsgM.put("transer_fee", "0");// 债权转让手续费
        
        return JSON.toJSONString(ecmsgM);
    }
	

	/***
	 * 债权转让数据实例 getTransferString：<对函数的简单描述>
	 * 
	 * @return
	 * @throws Exception
	 * @author huawei
	 * @see <参见的内容>
	 */
	public  String getTransferString(WloanTermProject project) throws Exception {
		// 债权转让
		Map ecmsgM = new HashMap();

		ecmsgM.put("product_reg_type", "03");// 01 散标类，02 理财计划 03 债权转让
		ecmsgM.put("product_name", project.getName());
		ecmsgM.put("product_mark", "抵押贷");//产品分类
		ecmsgM.put("source_code", "BJ20170413002");// 原平台注册编号
		ecmsgM.put("source_product_code", project.getId());
		
		String nameIDCard = "";
		// 使用加密的索引
		 if (null != project) { // 项目详情.
		 WloanSubject wloanSubject = wloanSubjectService.get(project.getSubjectId());
		 if (null != wloanSubject) { // 融资主体.
		 // 借款人.
		 CreditUserInfo creditUserInfo = creditUserInfoService.get(wloanSubject.getLoanApplyId());
		 if (null != creditUserInfo) {
			
		 ecmsgM.put("transfer_name",creditUserInfo.getName());
		 ecmsgM.put("transfer_idcard",creditUserInfo.getCertificateNo());
		 ecmsgM.put("transfer_sex", " ");//性别
		 nameIDCard =
		 creditUserInfo.getName()+creditUserInfo.getCertificateNo();
		 }
		 } else {
		
		 }
		 }
		
		// 采用加密缩影
		ecmsgM.put("transfer_name_idcard_digest",
				DigestUtils.sha256((nameIDCard).getBytes("UTF-8"), false));

		ecmsgM.put("hold_time", project.getSpan()+"天");
		ecmsgM.put("overplus_time", project.getSpan()+"天");
		ecmsgM.put("amt", project.getAmount());
		ecmsgM.put("transfer_rate", project.getAnnualRate()/12+"%");
		ecmsgM.put("transfer_fee", "0");
		ecmsgM.put("remark", "");
		return JSON.toJSONString(ecmsgM);
	}

	/**
	 * 更新产品状态 getUpdateProductString：<对函数的简单描述>
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 * @author huawei
	 * @see <参见的内容>
	 */
	public  String getUpdateProductString(WloanTermProject project,String state)
			throws UnsupportedEncodingException, Exception {
		Map ecmsgM = new HashMap();
		// 更新产品状态 1-满标（截标）／2-流标／3-还款结束／4-逾期

		ecmsgM.put("source_code", "BJ20170413002");
		ecmsgM.put("source_product_code", project.getId());
		if(state.equals("5")){
			ecmsgM.put("product_status", "1");
			ecmsgM.put("produc_status_desc", "满标");
			
			List<Map> investorlist = new ArrayList<>();
			List<WloanTermInvest> list = wloanTermInvestDao.findProjectInvestNumbers(project.getId());
			for (int i = 0; i < list.size(); i++) {
				UserInfo userInfo = list.get(i).getUserInfo();
				String investorDigest = DigestUtils.sha256(
						(userInfo.getRealName() + i + userInfo.getCertificateNo()).getBytes("UTF-8"), false);
				Map<String, String> inMap = new HashMap<>();
				inMap.put("investor_name_idcard_digest", investorDigest);
				inMap.put("invest_amt", list.get(i).getAmount().toString());
				investorlist.add(inMap);
			}
			ecmsgM.put("investorlist", investorlist);
			
			
		}else if(state.equals("7")){
			ecmsgM.put("product_status", "3");
			ecmsgM.put("produc_status_desc", "还款结束");
			
		}
		
		ecmsgM.put("product_date", DateUtils.formatDate(project.getFullDate(), "yyyyMMddHHMMss"));
	
		// System.out.println(ifs.productStatusUpdate("pa","p",JSON.toJSONString(ecmsgM)));
		return JSON.toJSONString(ecmsgM);
	}

	/***
	 * 散标数据实例 getScatteredinvestString：<对函数的简单描述>
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 * @author huawei
	 * @see <参见的内容>
	 */
	public  String getScatteredinvestString(
			WloanTermProject wloanTermProject)
			throws UnsupportedEncodingException, Exception {
		Map ecmsgM = new HashMap();
		// 散标
		ecmsgM.put("product_reg_type", "01");// 01 散标类，02 理财计划 03 债权转让
		ecmsgM.put("product_name", wloanTermProject.getName());
		ecmsgM.put("product_mark", "抵押贷");
		ecmsgM.put("source_code", "BJ20170413002");// 原平台注册编号
		ecmsgM.put("source_product_code", wloanTermProject.getId());
		String nameIDCard = "";
		// 使用加密的索引
		 if (null != wloanTermProject) { // 项目详情.
		 WloanSubject wloanSubject = wloanSubjectService.get(wloanTermProject.getSubjectId());
		 if (null != wloanSubject) { // 融资主体.
		 // 借款人.
		 CreditUserInfo creditUserInfo = creditUserInfoService.get(wloanSubject.getLoanApplyId());
		 if (null != creditUserInfo) {
		 ecmsgM.put("borrow_name",creditUserInfo.getName());
		 ecmsgM.put("borrow_idcard",creditUserInfo.getCertificateNo());
		 ecmsgM.put("borrow_sex", " ");//性别
		 nameIDCard =
		 creditUserInfo.getName()+creditUserInfo.getCertificateNo();
		 }
		 } else {
		
		 }
		 }
		ecmsgM.put("amount", wloanTermProject.getAmount());
		String annualRate = wloanTermProject.getAnnualRate()/12/100+"";
		if(annualRate.length()>8){
			annualRate = annualRate.substring(0, 7);
		}
		
		ecmsgM.put("rate", annualRate);
		ecmsgM.put("term_type", "天 ");
		ecmsgM.put("term", wloanTermProject.getSpan());
		ecmsgM.put("pay_type", "3");// 1-等额本息2-等额本金3-按月付息到期还本4-一次性还本付息5-其他
		Double feeRate = wloanTermProject.getFeeRate();
		if(feeRate>1){
			ecmsgM.put("service_cost", feeRate);
		}else{
			ecmsgM.put("service_cost", wloanTermProject.getAmount()*feeRate);
		}
		ecmsgM.put("risk_margin", "0 ");
		ecmsgM.put("loan_type", "抵质押 ");
		ecmsgM.put("loan_credit_rating", "A");
		WGuaranteeCompany wguarantee = wloanTermProject.getWgCompany();
		if(wguarantee==null){
			ecmsgM.put("security_info", "");
		}else{
			String securityInfo = wguarantee.getName();
			ecmsgM.put("security_info", securityInfo);
		}
		
		ecmsgM.put("collateral_desc", "");// 抵押物描述
		ecmsgM.put("collateral_info", "6个月 ");// 抵押物处置周期
		ecmsgM.put("overdue_limmit", "");// 逾期期限
		ecmsgM.put("bad_debt_limmit", "");// 坏账期限
		ecmsgM.put("amount_limmts", wloanTermProject.getMinAmount());
		ecmsgM.put("amount_limmtl", wloanTermProject.getMaxAmount());
		ecmsgM.put("allow_transfer", "1");// 0-是/1-否 是否允许债券转让
		ecmsgM.put("close_limmit", wloanTermProject.getSpan()/30);// 合同生效后，平台不接受借款人提前还款申请的期限
		ecmsgM.put("security_type", "抵押担保 ");// 担保方式
		ecmsgM.put("project_source", "线下");// 项目来源
		ecmsgM.put("source_product_url",
				"https://www.cicmorgan.com/invest_details_axt.html?id="+wloanTermProject.getId());// 原产品链接
		ecmsgM.put("remark", "");
		ecmsgM.put("borrow_name_idcard_digest",
				DigestUtils.sha256((nameIDCard).getBytes("UTF-8"), false));
		return JSON.toJSONString(ecmsgM);
	}


}
