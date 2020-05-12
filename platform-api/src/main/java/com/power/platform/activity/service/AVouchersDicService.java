package com.power.platform.activity.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.power.platform.activity.dao.AUserAwardsHistoryDao;
import com.power.platform.activity.dao.AVouchersDicDao;
import com.power.platform.activity.entity.AUserAwardsHistory;
import com.power.platform.activity.entity.AVouchersDic;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;

@Service("aVouchersDicService")
public class AVouchersDicService extends CrudService<AVouchersDic> {

	private static final Logger LOG = LoggerFactory.getLogger(AVouchersDicService.class);

	/**
	 * 状态，1：未使用，可以变更及删除.
	 */
	public static final String A_VOUCHERS_DIC_STATE_1 = "1";

	/**
	 * 状态，2：使用中，不可变更及删除.
	 */
	public static final String A_VOUCHERS_DIC_STATE_2 = "2";

	@Resource
	private AVouchersDicDao aVouchersDicDao;
	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private AUserAwardsHistoryDao aUserAwardsHistoryDao;

	public List<AVouchersDic> findAll() {

		return null;
	}

	@Override
	protected CrudDao<AVouchersDic> getEntityDao() {

		logger.info("fn:getEntityDao,{获取当前DAO}");
		return aVouchersDicDao;
	}

	public List<AVouchersDic> findAllList() {

		return aVouchersDicDao.findAllAVouchersDics();
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateAVouchersDic(AVouchersDic aVouchersDic) {

		int flag = 0;
		try {
			flag = aVouchersDicDao.update(aVouchersDic);
			logger.info("fn:updateARateCouponDic,{修改保存成功}");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:updateARateCouponDic,{修改保存异常：" + e.getMessage() + "}");
		}
		return flag;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertAVouchersDic(AVouchersDic aVouchersDic) {

		int flag = 0;
		try {
			flag = aVouchersDicDao.insert(aVouchersDic);
			logger.info("fn:insertARateCouponDic,{新增保存成功}");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:insertARateCouponDic,{新增保存异常：" + e.getMessage() + "}");
		}
		return flag;
	}
	
	/**
	 * 批量充值抵用券
	 * @param file
	 */
	@Transactional(readOnly=false,rollbackFor=Exception.class)
	public String upload(MultipartFile file){
		
		String message = "";
		try{
		if(file!=null&&file.getSize()>0){
			StringBuffer sb = new StringBuffer();
			//读取excle
			POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);
			int rownum = sheet.getLastRowNum();
			String name = null;	
			Double amount = 0.0;
			for (int i = 1; i <= rownum; i++) {
				HSSFRow row=sheet.getRow(i);
				HSSFCell cell = row.getCell((short) 0);
				name = cell.getStringCellValue();
				
				HSSFCell cell2 = row.getCell((short) 1);
				amount = cell2.getNumericCellValue();
				if(name == null || name.trim().length() != 11){
					sb.append("第"+i+"条数据【"+name+"】有问题，导入失败;");
					throw new Exception(sb.toString());
				}else{
					//根据手机号查询用户
					UserInfo user = userInfoDao.getUserInfoByPhone(name.trim());
					if(user!=null){
						// 查询时否有该面值抵用券
						AUserAwardsHistory aUserAwardsHistory = new AUserAwardsHistory();
						AVouchersDic aVouchersDic = aVouchersDicDao.findByVoucher(amount);

						if(aVouchersDic != null){
							aUserAwardsHistory.setId(String.valueOf(IdGen.randomLong()));
							aUserAwardsHistory.setAwardId(aVouchersDic.getId());
							aUserAwardsHistory.setCreateDate(new Date());
							aUserAwardsHistory.setUpdateDate(new Date());
							aUserAwardsHistory.setUserId(user.getId());
							aUserAwardsHistory.setOverdueDate(DateUtils.getSpecifiedMonthAfter(new Date(), 60));
							aUserAwardsHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_1);
							aUserAwardsHistory.setType("1");//类型为:抵用劵
							aUserAwardsHistory.setValue(amount.toString());
							int j = aUserAwardsHistoryDao.insert(aUserAwardsHistory);
							if(i>0){
								LOG.info("{用户}"+name.trim()+"发放{"+amount+"}元抵用劵成功");
							}
						}else{
							LOG.info("第"+i+"条数据【"+amount+"】没有该面值的抵用券;");
							throw new Exception(sb.toString());
						}
					}else{
						sb.append("第"+i+"条数据【"+name+"】尚未注册，请检查;");
					}
				}
			}
			if(sb.length() < 1){
				sb.append("导入成功！");
			}
			message = sb.toString();
		}else{
			message = "导入信息为空！";
		}}catch (Exception e) {
			// TODO: handle exception
			message = "批量充值失败，请联系开发人员";
		}
		return message;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public AVouchersDic findByVoucher(Double voucher) {
		// TODO Auto-generated method stub
		return aVouchersDicDao.findByVoucher(voucher);
	}

}
