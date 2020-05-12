package com.power.platform.activity.service;

import java.util.Date;

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

import com.power.platform.activity.dao.ARateCouponDicDao;
import com.power.platform.activity.dao.UserRateCouponHistoryDao;
import com.power.platform.activity.entity.ARateCouponDic;
import com.power.platform.activity.entity.AUserAwardsHistory;
import com.power.platform.activity.entity.AVouchersDic;
import com.power.platform.activity.entity.UserRateCouponHistory;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;

@Service("userRateCouponHistoryService")
public class UserRateCouponHistoryService extends CrudService<UserRateCouponHistory> {

	private static final Logger LOG = LoggerFactory.getLogger(UserRateCouponHistoryService.class);
	/**
	 * 1：抵用券.
	 */
	public static final String USER_RATE_COUPON_HISTORY_TYPE_1 = "1";
	/**
	 * 2：加息券.
	 */
	public static final String USER_RATE_COUPON_HISTORY_TYPE_2 = "2";

	@Resource
	private UserRateCouponHistoryDao userRateCouponHistoryDao;
	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private ARateCouponDicDao aRateCouponDicDao;

	public Page<UserRateCouponHistory> findRateCouponPage(Page<UserRateCouponHistory> page, UserRateCouponHistory entity) {

		entity.setPage(page);
		page.setList(userRateCouponHistoryDao.findRateCouponList(entity));
		return page;
	}

	@Override
	protected CrudDao<UserRateCouponHistory> getEntityDao() {

		return userRateCouponHistoryDao;
	}

	/**
	 * 批量充值加息券
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
			Double rate = 0.0;
			for (int i = 1; i <= rownum; i++) {
				HSSFRow row=sheet.getRow(i);
				HSSFCell cell = row.getCell((short) 0);
				name = cell.getStringCellValue();
				
				HSSFCell cell2 = row.getCell((short) 1);
				rate = cell2.getNumericCellValue();
				if(name == null || name.trim().length() != 11){
					sb.append("第"+i+"条数据【"+name+"】有问题，导入失败;");
					throw new Exception(sb.toString());
				}else{
					//根据手机号查询用户
					UserInfo user = userInfoDao.getUserInfoByPhone(name.trim());
					if(user!=null){
						// 查询时否有该面值加息券
						UserRateCouponHistory userRateCouponHistory = new UserRateCouponHistory();
						ARateCouponDic aRateCouponDic = aRateCouponDicDao.findByRate(rate);
						

						if(aRateCouponDic != null){
							userRateCouponHistory.setId(String.valueOf(IdGen.randomLong()));
							userRateCouponHistory.setAwardId(aRateCouponDic.getId());
							userRateCouponHistory.setCreateDate(new Date());
							userRateCouponHistory.setUpdateDate(new Date());
							userRateCouponHistory.setUserId(user.getId());
							userRateCouponHistory.setOverdueDate(DateUtils.getSpecifiedMonthAfter(new Date(), 60));
							userRateCouponHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_1);
							userRateCouponHistory.setType("2");//类型为:加息劵
							userRateCouponHistory.setValue(rate.toString());
							int j = userRateCouponHistoryDao.insert(userRateCouponHistory);
							if(i>0){
								LOG.info("{用户}"+name.trim()+"发放{"+rate+"}%加息劵成功");
							}
						}else{
							LOG.info("第"+i+"条数据【"+rate+"】没有该面值的抵用券;");
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

}