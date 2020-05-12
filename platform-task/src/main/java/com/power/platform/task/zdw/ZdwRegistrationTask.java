package com.power.platform.task.zdw;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.credit.entity.voucher.CreditVoucher;
import com.power.platform.credit.service.voucher.CreditVoucherService;
import com.power.platform.credit.service.ztmgLoanBasicInfo.ZtmgLoanBasicInfoService;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.zdw.dao.ZdwProOrderInfoDao;
import com.power.platform.zdw.entity.User;
import com.power.platform.zdw.entity.ZdwProOrderInfo;
import com.power.platform.zdw.entity.ZdwRegistrationInfo;
import com.power.platform.zdw.service.ZdwLoginService;
import com.power.platform.zdw.service.ZdwProOrderInfoService;
import com.power.platform.zdw.service.ZdwRegisterService;
import com.power.platform.zdw.service.ZdwRegistrationInfoService;
import com.power.platform.zdw.type.ProOrderStatusEnum;

@Service
@Lazy(false)
public class ZdwRegistrationTask {

	private static final Logger log = Logger.getLogger(ZdwRegistrationTask.class);
	@Resource
	private ZdwProOrderInfoService zdwProOrderInfoService;
	@Resource
	private ZdwLoginService zdwLoginService;
	@Resource
	private ZdwRegisterService zdwRegisterService;
	@Resource
	private WloanTermProjectService wloanTermProjectService;
	@Resource
	private WloanSubjectService wloanSubjectService;
	@Resource
	private ZtmgLoanBasicInfoService ztmgLoanBasicInfoService;
	@Resource
	private CreditVoucherService creditVoucherService;
	@Resource
	private ZdwRegistrationInfoService zdwRegistrationInfoService;
	@Resource
	private ZdwProOrderInfoDao zdwProOrderInfoDao;
	
	// 每天19:00，执行中登网初始登记.
	// @Scheduled(cron = "0 0 19 * * ?")
	public void zdwRegistration() {

		log.info("zdwRegistration---中登网执行初始登记--开始 ...");
		try {
			ZdwProOrderInfo zdwProOrderInfo = new ZdwProOrderInfo();
			zdwProOrderInfo.setStatus(ProOrderStatusEnum.PRO_ORDER_STATUS_01.getValue());
			List<ZdwProOrderInfo> list = zdwProOrderInfoService.findList(zdwProOrderInfo);
			List<WloanTermProject> arrayList = new ArrayList<>();
			List<ZdwProOrderInfo> zdwProOrderList = new ArrayList<>();
			if (!list.isEmpty() && list.size() >= 5) {
				// 登录中登网
				User user = new User(null, null);
				String loginResult = zdwLoginService.login(user);
				String timelimit = "0.5";
				if ("登录成功".equals(loginResult.trim())) {
					log.info("中登网：" + loginResult);
					int count = 0;
					String title ="";
					int n = (list.size() / 5) * 5;
					for (int i = 0; i < n; i++) {
//					for (int i = 0; i < (list.size() / 5) * 5; i++) {
						WloanTermProject wloanTermProject = wloanTermProjectService.get(list.get(i).getProId());
						if (wloanTermProject.getSpan() > 180) {
							timelimit = "1.0";
						}
						zdwProOrderList.add(list.get(i));
						arrayList.add(wloanTermProject);
						if (arrayList.size() == 5) {
							count++;
							if(count<10) {
								title = "ztmg-" + DateUtils.getDate().replaceAll("-", "") + "0"+ count;
							}else {
								title = "ztmg-" + DateUtils.getDate().replaceAll("-", "") +count;
							}
							// 登陆成功后进行登记.
							JSONObject obj = new JSONObject();
							obj.put("userName", user.getUserName());
							obj.put("password", user.getPassword());
							obj.put("timelimit", timelimit);
							obj.put("title", title);
							String resultMessage = addRegister(obj, arrayList, user);
							log.info("中登网执行初始登记，结果：" + resultMessage);
							timelimit = "0.5";
							arrayList.clear();
							//修改满标落单状态
							if("成功".equals(resultMessage.trim())) {
								for (ZdwProOrderInfo zpo : zdwProOrderList) {
									zpo.setStatus(ProOrderStatusEnum.PRO_ORDER_STATUS_00.getValue());
									zpo.setUpdateDate(new Date());
									zdwProOrderInfoDao.update(zpo);
								}
							}
							zdwProOrderList.clear();
						}
					}
					log.info("zdwRegistration---中登网执行初始登记--结束 ...");
				} else if ("登录其他错误".equals(loginResult)) {
					log.info("中登网重新登陆，结果：" + loginResult);
				} else if ("登录异常".equals(loginResult)) {
					log.info("中登网重新登陆，结果：" + loginResult);
				} else if ("false".equals(loginResult)) {
					log.info("中登网登陆，验证码识别失败！");
				}
			} else {
				log.info("定时器zdwRegisterTask中登网初始记录--满标落单少于5条");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("【定时器zdwRegisterTask中登网初始记录出现问题");
		}
	}
	private String addRegister(JSONObject obj,List<WloanTermProject> arrayList,User user) {
		String result = "初始记录失败";
		//一。填表人/基本信息
		String result1 = zdwRegisterService.addUserInfo(obj);
		System.out.println("一。填表人/基本信息----"+result1);
		log.info("一。填表人/基本信息----"+result1);
		if("登记填报人和质权人信息成功".equals(result1.trim())) {
			//二。出质人信息
			String addPledgerResult =  addPledger(arrayList);
			System.out.println("二。出质人信息----"+addPledgerResult);
			log.info("二。出质人信息----"+addPledgerResult);
			if("登记出质人信息成功".equals(addPledgerResult.trim())) {
				//三。质押财产信息
				System.out.println("第三步开始");
				result = addPledgedProperty(arrayList,user);
				System.out.println("三。登记质押财产信息----"+result);
				log.info("三。登记质押财产信息----"+result);
			}
		}
		return result;
		
	}
	
	private String addPledgedProperty(List<WloanTermProject> arrayList,User user) {
		JSONObject obj = new JSONObject();
		double maincontractsum = 0.00D;
		double collateralsum = 0.00D;
		String description = "";
		String proIdList = "";
		for (WloanTermProject wloanTermProject : arrayList) {
			proIdList+=wloanTermProject.getId()+",";
			StringBuffer s = new StringBuffer();
			String str = "";
			if("XY".equals(wloanTermProject.getSn().substring(0, 2))) {
				str = wloanTermProject.getProjectCase().split("\r\n")[0];
			}else {
				str = wloanTermProject.getProjectCase();
			}
			maincontractsum +=wloanTermProject.getAmount();
			List<CreditVoucher> list = creditVoucherService.findCreditVoucher(wloanTermProject.getId());
			if(!list.isEmpty()&&!"".equals(str.trim())) {
				String[] sp = str.split("元。");
				s.append(sp[0]);
				s.append("元。原发票号为：");
				for (CreditVoucher creditVoucher : list) {
					collateralsum +=Double.parseDouble(creditVoucher.getMoney());
					s.append("No."+creditVoucher.getNo()+"，");
				}
				s.append("剩余履约期限为"+wloanTermProject.getSpan()/30+"个月。");
				for (int i = 1; i < sp.length; i++) {
					s.append(sp[i]);
				}
				description += s.toString()+"\r\n";
			}
		}
		if(!"".equals(description.trim())) {
			description = description.substring(0, description.length()-2);
		}
		log.info("maincontractsum="+maincontractsum);
		log.info("collateralsum="+collateralsum);
		log.info("description="+description);
		BigDecimal m = new BigDecimal(maincontractsum);  
		double mDouble = m.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
		BigDecimal c = new BigDecimal(collateralsum);  
		double cDouble = c.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
		obj.put("userName", user.getUserName());
		obj.put("password", user.getPassword());
		obj.put("maincontractno", "");
		obj.put("maincontractcurrency", "人民币");
		obj.put("maincontractsum", mDouble);
		obj.put("loanbegindate", "");
		obj.put("loanenddate", "");
		obj.put("contractno", "");
		obj.put("contractcurrency", "人民币");
		obj.put("collateralsum", cDouble);
		obj.put("description", description);
//		obj.put("description", "本次拟质押应收帐款为原债权人四川广乐食品有限公司与原债务人山西美特好连锁超市股份有限公司旗下的清徐县美特好农产品配送物流有限公司签署的编号6732为《商品采购合同》项下的新增应收帐款,共计：189,496.66 元。原发票号为：No.01492921，No.08897420，剩余履约期限为3个月。原债权人拟通过质押上述应收帐款的全部或部分价值，在本平台融资 152,500.00元，到期后由原债务人支付融资本金\n本次拟质押应收帐款为原债权人江西金宝城食品工业有限公司与债务人宁波熙耘科技有限公司签署的编号为 SP-19-066 的《商品采销购销合同》项下的新增应收帐款。共计：46,351.78元。原发票号为：No.03304275，剩余履约期限为3个月。原债权人拟通过质押上述应收帐款的全部或部分价值，在本平台融资35,562.26元，到期后由原债务人支付融资本金。");
		Map<String,String> map = zdwRegisterService.addPledgedPropertyInfo(obj);
		if("登记质押财产信息成功".equals(map.get("msg"))) {
			//保存初始登记的返回记录
			if(!"".equals(proIdList.trim())) {
				proIdList = proIdList.substring(0, proIdList.length()-1);
			}
			ZdwRegistrationInfo zri = new ZdwRegistrationInfo();
			zri.setNum(arrayList.size()+"");
			zri.setProIdList(proIdList);
			zri.setCheckInNo(map.get("registration_number"));
			zri.setModifyCode(map.get("modification_code"));
			zri.setProveFilePath(map.get("path"));
			zri.setStatus("00");
			zri.setCreateDate(new Date());
			zri.setRemarks("中登网-初始登记");
			zri.setDelFlag("0");
			zdwRegistrationInfoService.save(zri);
			return "成功";
		}
		return "登记质押财产信息失败";
	}

	private String addPledger(List<WloanTermProject> arrayList) {
		List list = new ArrayList();
		List<WloanTermProject> wloanTermProjectList = new ArrayList();
		for (WloanTermProject wloanTermProject : arrayList) {
			String subjectId = wloanTermProject.getSubjectId();
			if(!list.contains(subjectId)) {
				list.add(subjectId);
				wloanTermProjectList.add(wloanTermProject);
			}
		}
		if(!wloanTermProjectList.isEmpty()) {
			User user = new User(null, null);
			for (WloanTermProject wloanTermProject : wloanTermProjectList) {
				WloanSubject wloanSubject = wloanSubjectService.getSubject(wloanTermProject.getSubjectId());
				JSONObject obj = new JSONObject();
				obj.put("userName", user.getUserName());
				obj.put("password", user.getPassword());
				obj.put("province", wloanSubject.getProvince());
				obj.put("city", wloanSubject.getCity());
				obj.put("debtorName", wloanSubject.getCompanyName());
				obj.put("orgCode", wloanSubject.getBusinessNo());
				obj.put("businessCode", wloanSubject.getBusinessNo());
				obj.put("lei", "");
				obj.put("scale", "小型企业");
				obj.put("responsiblePerson", wloanSubject.getLoanUser());
				obj.put("address", wloanSubject.getCounty() + wloanSubject.getStreet());
				String s = zdwRegisterService.addPledgerInfo(obj);
				System.out.println(s);
				if("请按接口规范进行登记".equals(s.trim())||"登记出质人信息失败".equals(s.trim())||"登记出质人信息异常".equals(s.trim())||"请重新登录".equals(s.trim())) {
					return s;
				}
			}
		}
		
		return "登记出质人信息成功";
	}

}
