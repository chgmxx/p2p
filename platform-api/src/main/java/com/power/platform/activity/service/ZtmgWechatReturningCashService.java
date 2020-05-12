package com.power.platform.activity.service;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.power.platform.activity.dao.ZtmgWechatReturningCashDao;
import com.power.platform.activity.entity.AVouchersDic;
import com.power.platform.activity.entity.UserVouchersHistory;
import com.power.platform.activity.entity.ZtmgWechatReturningCash;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.dao.CgbUserTransDetailDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.excel.ImportExcel;
import com.power.platform.trandetail.dao.UserTransDetailDao;
import com.power.platform.trandetail.entity.UserTransDetail;
import com.power.platform.trandetail.service.UserTransDetailService;
import com.power.platform.userinfo.dao.UserAccountInfoDao;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserAccountInfo;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.weixin.service.WeixinSendTempMsgService;

/**
 * 
 * 类: ZtmgWechatReturningCashService <br>
 * 描述: 微信返现Service. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年9月8日 上午10:29:23
 */
@Service
@Transactional(readOnly = true)
public class ZtmgWechatReturningCashService extends CrudService<ZtmgWechatReturningCash> {

	private static final Logger LOG = LoggerFactory.getLogger(ZtmgWechatReturningCashService.class);

	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private UserAccountInfoDao userAccountInfoDao;
	@Resource
	private UserTransDetailDao userTransDetailDao;
	@Resource
	private ZtmgWechatReturningCashDao ztmgWechatReturningCashDao;
	@Resource
	private WeixinSendTempMsgService weixinSendTempMsgService;
	@Resource
	private CgbUserTransDetailDao cgbUserTransDetailDao;
	@Resource
	private CgbUserAccountDao cgbUserAccountDao;
	@Resource
	private RedPacketService redPacketService;

	@Override
	protected CrudDao<ZtmgWechatReturningCash> getEntityDao() {

		return ztmgWechatReturningCashDao;
	}

	/**
	 * 
	 * 方法: findExcelReportPage <br>
	 * 描述: 财务报表需求，列表展示. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年9月8日 上午10:34:26
	 * 
	 * @param page
	 * @param entity
	 * @return
	 */
	public Page<ZtmgWechatReturningCash> findExcelReportPage(Page<ZtmgWechatReturningCash> page, ZtmgWechatReturningCash entity) {

		entity.setPage(page);
		page.setList(ztmgWechatReturningCashDao.findExcelReportList(entity));
		return page;
	}

	/**
	 * 账户返现
	 * 
	 * @param file
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public String upload(MultipartFile file) {

		String message = "受理成功";
		int q =0;
		try {
			
		ImportExcel ie = new ImportExcel(file, 1, 0);
		for (int i = 1; i < ie.getLastDataRowNum(); i++) {
			q = i;
			Row row = ie.getRow(i);
			StringBuffer rowDateSb = new StringBuffer();
			for (int j = 0; j < ie.getLastCellNum(); j++) {
				Object val = ie.getCellValue(row, j);
				if (val instanceof String) { // String类型不作处理.
				}
				if (val instanceof Integer) { // Integer类型处理.
					val = new DecimalFormat("0").format(val);
				}
				if (val instanceof Double) { // Double类型处理.
					val = new DecimalFormat("0").format(val);
				}
				if (val instanceof Float) { // Float类型处理.
					val = new DecimalFormat("0").format(val);
				}
				rowDateSb.append(val).append("|");
			}
			String rowDateStr = rowDateSb.toString();
			List<String> asList = Arrays.asList(rowDateStr.split("\\|"));
			//
			String returnCashId = UUID.randomUUID().toString().replace("-", "");
			ZtmgWechatReturningCash entity = new ZtmgWechatReturningCash();
			entity.setId(returnCashId); // 该条数据主键ID.
			for (int x = 0; x < asList.size(); x++) {
				if (x == 0) { // 手机号码.
					String mobilePhone = asList.get(x);
					UserInfo user = userInfoDao.getUserInfoByPhone(mobilePhone);
					if (user != null) { // 出借人ID.
						entity.setUser_id(user.getId());
						entity.setAccountId(user.getAccountId());
						entity.setMobilePhone(user.getName());
						entity.setRealName(user.getRealName());
					}
				}
				if (x == 1) { // 返还金额额.
					String amount = asList.get(x);
					// 面额.
					entity.setPayAmount(Double.valueOf(amount));
				}
			}
			entity.setCreateDate(new Date());
			entity.setUpdateDate(new Date());
			entity.setState(ZtmgWechatReturningCash.STATE_DONING);//处理中
			
			//调用红包返利API
			Map<String, String> redPackMap =  redPacketService.giveRedPacket(entity.getMobilePhone(), "8003", entity.getPayAmount(), entity.getId());
			if(redPackMap.get("respSubCode").equals("000100")){
				int flag = ztmgWechatReturningCashDao.insert(entity);
				if (flag == 1) {
					logger.info("第" + i + "条数据【" + entity.getMobilePhone() + "】账户返利受理成功");
				} else {
					logger.info(this.getClass() + "-该批次账户返现批量充值-第" + i + "条数据-充值失败");
				}
			}

		}

		} catch (Exception e) {
			// TODO: handle exception
			message = "受理失败,第"+q+"条数据有误,请检查";
			System.out.println(e.getMessage());
		}
		return message;
	}

}