package com.power.platform.current.service.redeem;


import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.current.dao.redeem.WloanCurrentUserRedeemDao;
import com.power.platform.current.entity.WloanCurrentUserInvest;
import com.power.platform.current.entity.invest.WloanCurrentProjectInvest;
import com.power.platform.current.entity.moment.WloanCurrentMomentInvest;
import com.power.platform.current.entity.redeem.WloanCurrentUserRedeem;
import com.power.platform.current.service.WloanCurrentProjectService;
import com.power.platform.current.service.invest.WloanCurrentProjectInvestService;
import com.power.platform.current.service.invest.WloanCurrentUserInvestService;
import com.power.platform.current.service.moment.WloanCurrentMomentInvestService;
import com.power.platform.trandetail.entity.UserTransDetail;
import com.power.platform.trandetail.service.UserTransDetailService;
import com.power.platform.userinfo.entity.UserAccountInfo;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserAccountInfoService;
import com.power.platform.userinfo.service.UserInfoService;

/**
 * 活期赎回Service
 * @author yb
 * @version 2016-01-13
 */
@Service("wloanCurrentUserRedeemService")
@Transactional(readOnly=false)
public class WloanCurrentUserRedeemService extends CrudService<WloanCurrentUserRedeem> {
	
	/**
	 * 活期赎回审批状态---待审批
	 */
	public static Integer redeem_state1 = 1;
	
	/**
	 * 活期赎回审批状态---已审批
	 */
	public static Integer redeem_state2 = 2;
	
	/**
	 * 活期赎回审批状态---审批失败
	 */
	public static Integer redeem_state3 = 3;
	
	
	@Resource
	private WloanCurrentUserRedeemDao wloanCurrentUserRedeemDao;
	@Resource
	private UserInfoService userInfoService;
	@Resource
	private UserAccountInfoService userAccountInfoService;
	@Resource
	private WloanCurrentProjectInvestService wloanCurrentProjectInvestService;
	@Resource
	private WloanCurrentUserInvestService wloanCurrentUserInvestService;
	@Resource
	private UserTransDetailService userTransDetailService;
	@Resource
	private WloanCurrentProjectService wloanCurrentProjectService;
	@Resource
	private WloanCurrentMomentInvestService wloanCurrentMomentInvestService;
	
	
	
	@Override
	protected CrudDao<WloanCurrentUserRedeem> getEntityDao() {
		return wloanCurrentUserRedeemDao;
	}

	/**
	 * 转让债权合同Map
	 * @param transfer_person 甲方
	 * @param assignee_person 乙方
	 * @param redeemamount 转让金额
	 * @return
	 */
	public Map<String, String> contractMap(String transfer_person,
			String assignee_person, Double redeemamount) {
		// TODO Auto-generated method stub
		Date nowTime = new Date();
		Calendar cal = Calendar.getInstance();
		UserInfo Jperson = userInfoService.get(transfer_person);
		if(Jperson==null){
			Jperson = userInfoService.getCgb(transfer_person);
		}
		UserInfo Yperson = userInfoService.get(transfer_person);
		if(Yperson==null){
			Yperson = userInfoService.getCgb(transfer_person);
		}
        //模板参数
	  	Map<String, String> map =new HashMap<String, String>();
		map.put("transfer_person", Jperson.getRealName());
		map.put("assignee_person", Yperson.getRealName());
		map.put("year",String.valueOf(cal.get(Calendar.YEAR)));
		map.put("month", String.valueOf(cal.get(Calendar.MONTH)));
		map.put("day", String.valueOf(cal.get(Calendar.DATE)));
		map.put("contract_no", "NO_"+DateUtils.getCurrentDateTimeStr());
		map.put("transfer_amout", String.valueOf(redeemamount));
		map.put("username", Jperson.getRealName());
		map.put("vip_no", UUID.randomUUID().toString());
		map.put("term_date", "");
		map.put("start_date", "");
		map.put("end_date", "");
		map.put("year_interest", "8.8");
		map.put("back_date", "");
		map.put("back_mode", "活期赎回");
		logger.info("转让债权合同Map结束");
		return map;
	}

	/**
	 * 活期赎回处理
	 * @param transfer_person 甲方(赎回人)
	 * @param assignee_person 乙方(接债权人)
	 * @param redeemamount  转让金额
	 * @param ip  ip地址
	 */
	public String[] redeem(WloanCurrentUserRedeem wloanCurrentUserRedeem, String ip) {
		// TODO Auto-generated method stub
		String[] InvestIds = null;
		String InvestId = "";
		//操作更新账户表标识
		int canredeem = 0;
		//'陈'总共投资金额
		Double YredeemAmount = 0d;
		//赎回金额
		Double redeemamount = wloanCurrentUserRedeem.getAmount();
		//查询甲方(赎回人)账户信息
		String transfer_person = wloanCurrentUserRedeem.getUserInfo1().getId();
		UserAccountInfo JuserAccountInfo = userAccountInfoService.getUserAccountInfo(transfer_person);
		//查询乙方(接债权人)[陈]账户信息
		String assignee_person = wloanCurrentUserRedeem.getUserInfo().getId();
		UserAccountInfo YuserAccountInfo = userAccountInfoService.getUserAccountInfo(assignee_person);
		//乙方可用金额大于零
		if(YuserAccountInfo.getAvailableAmount()>0)
		{
			Double shengyuAmount = redeemamount;//剩余金额
			//查询甲方(赎回人)融资金额(按投资时间升序排列)
			List<WloanCurrentProjectInvest>  wloanCurrentProjectInvestList = wloanCurrentProjectInvestService.findListOrderBy(transfer_person);
			if(wloanCurrentProjectInvestList!=null && wloanCurrentProjectInvestList.size()>0)
			{
				InvestIds = new String[wloanCurrentProjectInvestList.size()];
				for(WloanCurrentProjectInvest loanCurrentProjectInvest:wloanCurrentProjectInvestList)
				{
					if(loanCurrentProjectInvest.getAmount()-redeemamount == 0 && shengyuAmount > 0)
					{
						logger.info("[项目投资金额]"+loanCurrentProjectInvest.getAmount() + "[赎回金额]" + redeemamount);
			            shengyuAmount = shengyuAmount - redeemamount;
			            logger.info("[更新预投资表状态.在投金额]开始=====>>>");
			            updateWloanCurrentUserInvest(loanCurrentProjectInvest.getUserInvest(), loanCurrentProjectInvest.getAmount());
			            logger.info("<<<=====[更新预投资表状态.在投金额]结束");
			            //================================陈=======================================
			            String userInvest = IdGen.uuid();
			            logger.info("[陈]投资项目等额金额开始===>>>");
			            InvestId = saveWloanCurrentProjectInvest(loanCurrentProjectInvest.getProjectId(), assignee_person, redeemamount, userInvest);
			            logger.info("<<<===[陈]投资项目等额金额结束");
			            logger.info("[陈]投资等额金额开始===>>>");
			            savewloanCurrentUserInvest(userInvest, assignee_person, redeemamount, ip);
			            logger.info("<<<===[陈]投资等额金额结束");
			            logger.info("[陈][生成交易流水记录]开始");
			            saveUserTranDetail(userInvest, assignee_person, YuserAccountInfo.getId(), redeemamount, YuserAccountInfo.getAvailableAmount());
			            logger.info("[陈][生成交易流水记录]结束");
			            logger.info("[将'甲方'融资数据]逻辑删除开始");
			            loanCurrentProjectInvest.setDelFlag("1");
			            wloanCurrentProjectInvestService.delete(loanCurrentProjectInvest);
			            logger.info("[将'甲方'融资数据]逻辑删除开始");
			            YredeemAmount = YredeemAmount + redeemamount;
			            redeemamount = 0d;
			            canredeem = 1;
					}
					else if(loanCurrentProjectInvest.getAmount()<redeemamount &&  shengyuAmount >=0 )
					{
						logger.info("[项目投资金额]"+loanCurrentProjectInvest.getAmount() + "[赎回金额]" + redeemamount);
			            logger.info("[更新预投资表状态.在投金额]开始=====>>>");
			            updateWloanCurrentUserInvest(loanCurrentProjectInvest.getUserInvest(),loanCurrentProjectInvest.getAmount());
			            logger.info("<<<=====[更新预投资表状态.在投金额]结束");
			            //================================陈=======================================
			            logger.info("[陈]投资项目等额金额开始");
			            String userInvest = IdGen.uuid();
			            InvestId = saveWloanCurrentProjectInvest(loanCurrentProjectInvest.getProjectId(), assignee_person, loanCurrentProjectInvest.getAmount(), userInvest);
			            logger.info("[陈]投资项目等额金额结束");
			            logger.info("[陈]投资等额金额开始");
			            savewloanCurrentUserInvest(userInvest, assignee_person, loanCurrentProjectInvest.getAmount(), ip);
			            logger.info("[陈]投资等额金额结束");
			            logger.info("['陈'生成交易流水记录]开始");
			            saveUserTranDetail(userInvest, assignee_person, YuserAccountInfo.getId(), loanCurrentProjectInvest.getAmount(), YuserAccountInfo.getAvailableAmount());
			            logger.info("['陈'生成交易流水记录]结束");
			            logger.info("[将'甲方'融资数据]逻辑删除开始");
			            loanCurrentProjectInvest.setDelFlag("1");
			            wloanCurrentProjectInvestService.delete(loanCurrentProjectInvest);
			            logger.info("[将'甲方'融资数据]逻辑删除开始");
			            YredeemAmount = YredeemAmount + loanCurrentProjectInvest.getAmount();
			            redeemamount = redeemamount - loanCurrentProjectInvest.getAmount();
			            shengyuAmount = redeemamount;
			            canredeem = 1;
						
					}
					else if(loanCurrentProjectInvest.getAmount()>redeemamount &&  shengyuAmount >0)
					{
						logger.info("[项目投资金额]"+loanCurrentProjectInvest.getAmount() + "[赎回金额]" + redeemamount);
						shengyuAmount = loanCurrentProjectInvest.getAmount() - redeemamount;
						//投资金额进行余款自动再投资
			            logger.info("投资项目余款金额开始===>>>");
			            loanCurrentProjectInvest.setAmount(shengyuAmount);
			            wloanCurrentProjectInvestService.save(loanCurrentProjectInvest);
			            logger.info("<<<===投资项目余款金额结束");
						//=========================================
			            logger.info("[更新预投资表状态.在投金额]开始=====>>>");
			            WloanCurrentUserInvest wloanCurrentUserInvestInfo = wloanCurrentUserInvestService.get(loanCurrentProjectInvest.getUserInvest());
			            if(shengyuAmount==0)
			            {
			            	wloanCurrentUserInvestInfo.setState(WloanCurrentUserInvestService.WLOAN_CURRENT_USER_INVEST_STATE_3);
			            }
			            wloanCurrentUserInvestInfo.setOnLineAmount(shengyuAmount);
			            wloanCurrentUserInvestService.save(wloanCurrentUserInvestInfo);
			            logger.info("<<<=====[更新预投资表状态.在投金额]结束");
			            //================================陈=======================================
			            String userInvest = IdGen.uuid();
			            logger.info("[陈]投资项目等额金额开始===>>>");
			            InvestId = saveWloanCurrentProjectInvest(loanCurrentProjectInvest.getProjectId(), assignee_person, redeemamount, userInvest);
			            logger.info("<<<===[陈]投资项目等额金额结束");
			            logger.info("[陈]投资等额金额开始===>>>");
			            savewloanCurrentUserInvest(userInvest, assignee_person, redeemamount, ip);
			            logger.info("<<<===[陈]投资等额金额结束");
			            logger.info("['陈'生成交易流水记录]开始");
			            saveUserTranDetail(userInvest, assignee_person, YuserAccountInfo.getId(), redeemamount, YuserAccountInfo.getAvailableAmount());
			            logger.info("['陈'生成交易流水记录]结束");
			            YredeemAmount = YredeemAmount + redeemamount;
			            redeemamount = 0d;
			            shengyuAmount = 0d;
			            canredeem = 1;
					}
				}
				
				InvestIds = InvestId.split(",");
			}
			String userInvestId = "";
			if(redeemamount>0){
			//查询被拆分待投资记录
			logger.info("[投资用户剩余资金信息]开始");
			List<WloanCurrentMomentInvest> wloanCurrentMomentInvestList = wloanCurrentMomentInvestService.findListByUserId(transfer_person);
			if(wloanCurrentMomentInvestList!=null && wloanCurrentMomentInvestList.size()>0){
				for(WloanCurrentMomentInvest loanCurrentMomentInvest:wloanCurrentMomentInvestList){
					if(loanCurrentMomentInvest.getAmount()-redeemamount == 0 && redeemamount > 0){
						userInvestId = loanCurrentMomentInvest.getUserInvest();
						logger.info("[操作数据的ID]"+loanCurrentMomentInvest.getId());
						loanCurrentMomentInvest.setDelFlag("1");
						loanCurrentMomentInvest.setState(WloanCurrentMomentInvestService.WLOAN_CURRENT_MOMENT_INVEST_STATE_SALED);
						wloanCurrentMomentInvestService.delete(loanCurrentMomentInvest);
						//保证当拆分表中有记录时并对其操作后 对应数据的投资表数据联动
						updateWloanCurrentUserInvest(userInvestId, redeemamount);
						redeemamount = 0d;
					}
					else if(loanCurrentMomentInvest.getAmount() > redeemamount && redeemamount > 0 ){
						userInvestId = loanCurrentMomentInvest.getUserInvest();
						loanCurrentMomentInvest.setAmount(loanCurrentMomentInvest.getAmount()-redeemamount);
						wloanCurrentMomentInvestService.save(loanCurrentMomentInvest);
						//保证当拆分表中有记录时并对其操作后 对应数据的投资表数据联动
						updateWloanCurrentUserInvest(userInvestId, redeemamount);
						redeemamount = 0d;
					}
					else if(loanCurrentMomentInvest.getAmount() < redeemamount && redeemamount > 0 ){
						userInvestId = loanCurrentMomentInvest.getUserInvest();
						logger.info("[操作数据的ID]"+loanCurrentMomentInvest.getId());
						loanCurrentMomentInvest.setDelFlag("1");
						loanCurrentMomentInvest.setState(WloanCurrentMomentInvestService.WLOAN_CURRENT_MOMENT_INVEST_STATE_SALED);
						wloanCurrentMomentInvestService.delete(loanCurrentMomentInvest);
						//保证当拆分表中有记录时并对其操作后 对应数据的投资表数据联动
						updateWloanCurrentUserInvest(userInvestId, loanCurrentMomentInvest.getAmount());
						//redeemamount = redeemamount - loanCurrentMomentInvest.getAmount();
					}
				}
				canredeem = 2;
				InvestIds = new String[1];
				InvestIds[0]="暂未活期融资投资记录";
			}
			logger.info("[投资用户剩余资金信息]结束");
			}
			logger.info("[剩余操作赎回金额为:"+redeemamount+"]");
			if(redeemamount>0){
			//当天投资活期当天赎回
			logger.info("[当天投资活期当天赎回]开始");
			List<WloanCurrentUserInvest> wloanCurrentUserInvestList = wloanCurrentUserInvestService.findListOrderBy(transfer_person);
			
			if(wloanCurrentUserInvestList!=null && wloanCurrentUserInvestList.size()>0)
			{
				for(WloanCurrentUserInvest wloanCurrentUserInvest:wloanCurrentUserInvestList){
					
					if(wloanCurrentUserInvest.getOnLineAmount() - redeemamount == 0 && redeemamount > 0){
						updateWloanCurrentUserInvest(wloanCurrentUserInvest.getId(), redeemamount);
						redeemamount = 0d;
					}
					else if(wloanCurrentUserInvest.getOnLineAmount() > redeemamount && redeemamount > 0){
						updateWloanCurrentUserInvest(wloanCurrentUserInvest.getId(), redeemamount);
						redeemamount = 0d;
					}
					else if(wloanCurrentUserInvest.getOnLineAmount() < redeemamount && redeemamount > 0){
						updateWloanCurrentUserInvest(wloanCurrentUserInvest.getId(), wloanCurrentUserInvest.getOnLineAmount());
						redeemamount = redeemamount - wloanCurrentUserInvest.getAmount();
					}
						
				}
				canredeem = 2;
				InvestIds = new String[1];
				InvestIds[0]="暂未活期融资投资记录";
			}
			logger.info("[当天投资活期当天赎回]结束");
			}
			
			
		}
		
		if(canredeem>0)
		{
			
            logger.info("[更新客户账户表]开始");
            updateUserAccountJ(JuserAccountInfo, wloanCurrentUserRedeem.getAmount());
            logger.info("[更新客户账户表]结束");
            
            logger.info("[更新'陈'客户账户表]开始");
            updateUserAccountY(YuserAccountInfo, YredeemAmount);
            logger.info("[更新'陈'客户账户表]结束");
			
			wloanCurrentUserRedeem.setState(WloanCurrentUserRedeemService.redeem_state2);
			save(wloanCurrentUserRedeem);
			UserTransDetail userTransDetail = userTransDetailService.getByTransId(wloanCurrentUserRedeem.getId());
			userTransDetail.setState(UserTransDetailService.tran_type2);
			userTransDetailService.save(userTransDetail);
		}
		if(InvestIds==null)
		{
			InvestIds = new String[0]; 
		}
		return InvestIds;
		
	}
	
	/**
	 * 插入活期赎回数据
	 * @param wloanCurrentUserRedeem
	 */
	@Transactional(readOnly=false)
	public void insert(WloanCurrentUserRedeem wloanCurrentUserRedeem) {
		logger.info("[插入活期赎回数据]结束");
		wloanCurrentUserRedeemDao.insert(wloanCurrentUserRedeem);
		logger.info("[插入活期赎回数据]开始");
	}

	/**
	 * 根据用户ID查询赎回申请金额
	 * @param id
	 * @return
	 */
	public Double findRedeem(String userId,Integer state) {
		Double amount = wloanCurrentUserRedeemDao.findRedeem(userId,state);
		return amount;
	}


	
	
	
	
	
	
	
	
	
	/**
	 * 更新活期在投金额,状态
	 * @param userInvestId
	 * @param amount
	 */
	public void updateWloanCurrentUserInvest(String userInvestId,Double amount)
	{
		//根据[活期项目投资记录表-用户活期投资表ID]查询[活期用户项目投资记录表]
        WloanCurrentUserInvest wloanCurrentUserInvestInfo = wloanCurrentUserInvestService.get(userInvestId);
        if(wloanCurrentUserInvestInfo.getOnLineAmount()-amount == 0){
            wloanCurrentUserInvestInfo.setState(WloanCurrentUserInvestService.
                    WLOAN_CURRENT_USER_INVEST_STATE_3);//全部赎回状态变更为已赎回
            wloanCurrentUserInvestInfo.setOnLineAmount(0d);//活期在投金额
        }else{
        	wloanCurrentUserInvestInfo.setOnLineAmount(wloanCurrentUserInvestInfo.getOnLineAmount()-amount);
        	wloanCurrentUserInvestInfo.setState(wloanCurrentUserInvestInfo.getState().equals(WloanCurrentUserInvestService.WLOAN_CURRENT_USER_INVEST_STATE_2)?
								                                        		 WloanCurrentUserInvestService.WLOAN_CURRENT_USER_INVEST_STATE_2:WloanCurrentUserInvestService.WLOAN_CURRENT_USER_INVEST_STATE_1);
        }
        wloanCurrentUserInvestService.save(wloanCurrentUserInvestInfo);
	}
	
	/**
	 * '陈'放款记录表新增数据
	 * @param projectId
	 * @param userId
	 * @param amount
	 * @param userInvestId
	 * @return
	 */
	public String saveWloanCurrentProjectInvest(String projectId,String assignee_person,Double amount,String userInvestId)
	{
		String InvestId = "";
        WloanCurrentProjectInvest wloanCurrentProjectInvest = new WloanCurrentProjectInvest();
        wloanCurrentProjectInvest.setId(IdGen.uuid());
        InvestId = InvestId +","+wloanCurrentProjectInvest.getId();
        logger.info("存放项目投资表ID"+InvestId);
        wloanCurrentProjectInvest.setProjectId(projectId);
        wloanCurrentProjectInvest.setUserid(assignee_person);
        wloanCurrentProjectInvest.setAmount(amount);
        wloanCurrentProjectInvest.setBidDate(new Date());
        wloanCurrentProjectInvest.setUserInvest(userInvestId);
        wloanCurrentProjectInvestService.insert(wloanCurrentProjectInvest);
        return InvestId;
	}
	
	/**
	 * '陈'投资记录表新增数据
	 * @param userInvestId
	 * @param assignee_person
	 * @param amount
	 * @param ip
	 */
	public void savewloanCurrentUserInvest(String userInvestId,String assignee_person,Double amount,String ip)
	{
        WloanCurrentUserInvest wloanCurrentUserInvestInfo = new WloanCurrentUserInvest();
        wloanCurrentUserInvestInfo.setId(userInvestId);//ID
        UserInfo userinfo = new UserInfo();
        userinfo.setId(assignee_person);//[陈]用户ID
        wloanCurrentUserInvestInfo.setUserInfo(userinfo);
        wloanCurrentUserInvestInfo.setAmount(amount);//投资金额
        wloanCurrentUserInvestInfo.setOnLineAmount(amount);//在投金额
        wloanCurrentUserInvestInfo.setBidDate(new Date());//投资日期
        wloanCurrentUserInvestInfo.setIp(ip);//投资IP
        wloanCurrentUserInvestInfo.setState(WloanCurrentUserInvestService.
        		                                   WLOAN_CURRENT_USER_INVEST_STATE_2);//状态[已投资]
        wloanCurrentUserInvestInfo.setBidState(WloanCurrentUserInvestService.
        		                                   WLOAN_CURRENT_USER_INVEST_BID_STATE_1);//投资状态[成功]
        wloanCurrentUserInvestInfo.setRemarks("活期投资");//备注
        wloanCurrentUserInvestInfo.setDelFlag("0");//正常标识[正常]
        wloanCurrentUserInvestService.insertWloanCurrentUserInvest(wloanCurrentUserInvestInfo);
	}

	/**
	 * 生成交易明细记录
	 * @param userInvestId
	 * @param assignee_person
	 * @param accountId
	 * @param amount
	 * @param availableAmount
	 */
	public void saveUserTranDetail(String userInvestId,String assignee_person,String accountId,Double amount,Double availableAmount )
	{
        UserTransDetail userTransDetail1 = new UserTransDetail();
        userTransDetail1.setId(String.valueOf(new IdGen().randomLong()));//ID
        userTransDetail1.setTransId(userInvestId);//交易ID
        userTransDetail1.setUserId(assignee_person);//[陈]用户ID
        userTransDetail1.setTransDate(new Date());//交易时间
        userTransDetail1.setTrustType(UserTransDetailService.trust_type2);//交易类型[活期投资]
        userTransDetail1.setAccountId(accountId);//[陈]账户ID
        userTransDetail1.setAmount(amount);//[陈]投资金额
        userTransDetail1.setAvaliableAmount(availableAmount);//[陈]账户可用余额
        userTransDetail1.setInOutType(UserTransDetailService.out_type);//收支状态[支出]
        userTransDetail1.setRemarks("活期投资");//备注
        userTransDetail1.setState(UserTransDetailService.tran_type2);//状态[成功]
        userTransDetailService.insert(userTransDetail1);
	}
	
	/**
	 * 更新甲方账户信息
	 * @param userAccountInfo
	 * @param redeemAmount
	 */
	public void updateUserAccountJ(UserAccountInfo userAccountInfo,Double redeemAmount)
	{
        logger.info("[客户更新前可用余额为:"+userAccountInfo.getAvailableAmount()+"]");
        userAccountInfo.setAvailableAmount(userAccountInfo.getAvailableAmount()+redeemAmount);
        logger.info("[客户更新后可用余额为:"+userAccountInfo.getAvailableAmount()+"]");
        logger.info("[客户更新前活期在投金额为:"+userAccountInfo.getCurrentAmount()+"]");
        userAccountInfo.setCurrentAmount(userAccountInfo.getCurrentAmount()-redeemAmount);
        logger.info("[客户更新后活期在投金额为:"+userAccountInfo.getCurrentAmount()+"]");
        userAccountInfoService.save(userAccountInfo);
	}
	
	/**
	 * 更新乙方账户信息
	 * @param userAccountInfo
	 * @param redeemAmount
	 */
	public void updateUserAccountY(UserAccountInfo userAccountInfo,Double redeemAmount)
	{
        logger.info("['陈'客户更新前可用余额为:"+userAccountInfo.getAvailableAmount()+"]");
        userAccountInfo.setAvailableAmount(userAccountInfo.getAvailableAmount()-redeemAmount);
        logger.info("['陈'客户更新后可用余额为:"+userAccountInfo.getAvailableAmount()+"]");
        logger.info("['陈'客户更新前活期在投金额为:"+userAccountInfo.getCurrentAmount()+"]");
        userAccountInfo.setCurrentAmount(userAccountInfo.getCurrentAmount()+redeemAmount);
        logger.info("['陈'客户更新后活期在投金额为:"+userAccountInfo.getCurrentAmount()+"]");
        logger.info("['陈'客户更新前活期累计投资金额为:"+userAccountInfo.getCurrentTotalAmount()+"]");
        userAccountInfo.setCurrentTotalAmount(userAccountInfo.getCurrentTotalAmount()+redeemAmount);
        logger.info("['陈'客户更新后活期累计投资金额为:"+userAccountInfo.getCurrentTotalAmount()+"]");
        userAccountInfoService.save(userAccountInfo);
	}
}