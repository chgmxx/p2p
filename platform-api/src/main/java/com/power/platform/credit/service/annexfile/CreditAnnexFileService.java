package com.power.platform.credit.service.annexfile;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.annexfile.CreditAnnexFileDao;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;

/**
 * 
 * 类: CreditAnnexFileService <br>
 * 描述: 个人信贷附件Service. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年3月28日 上午9:11:18
 */
@Service("creditAnnexFileService")
public class CreditAnnexFileService extends CrudService<CreditAnnexFile> {

	/**
	 * 信贷资质，附件地址前缀.
	 */
	private static final String VIEW_FILE_PATH = Global.getConfig("credit_file_path");

	/**
	 * 资料类型，3：承诺函.
	 */
	public static final String CREDIT_PROJECT_DATA_TYPE_3 = "3";
	public static final String CREDIT_PROJECT_DATA_TYPE_7 = "7";
	/**
	 * 资料类型，8：营业执照.
	 */
	public static final String CREDIT_PROJECT_DATA_TYPE_8 = "8";
	/**
	 * 资料类型，9：银行许可证.
	 */
	public static final String CREDIT_PROJECT_DATA_TYPE_9 = "9";
	/**
	 * 资料类型，10：法人身份证.
	 */
	public static final String CREDIT_PROJECT_DATA_TYPE_10 = "10";

	/**
	 * 资质审核信息传递文本表单字段2个(String类型的参数).
	 */
	public static final int IS_TEXT_FORM_FIELD_2 = 2;

	/**
	 * '1'：审核中.
	 */
	public static final String CREDIT_ANNEX_FILE_STATE_1 = "1";
	/**
	 * '2'：通过.
	 */
	public static final String CREDIT_ANNEX_FILE_STATE_2 = "2";
	/**
	 * '3'：未通过.
	 */
	public static final String CREDIT_ANNEX_FILE_STATE_3 = "3";

	/**
	 * '1'：基本信息.
	 */
	public static final String CREDIT_ANNEX_FILE_TYPE_1 = "1";
	/**
	 * '2'：家庭信息.
	 */
	public static final String CREDIT_ANNEX_FILE_TYPE_2 = "2";
	/**
	 * '3'：户籍信息.
	 */
	public static final String CREDIT_ANNEX_FILE_TYPE_3 = "3";
	/**
	 * '4'：房产信息.
	 */
	public static final String CREDIT_ANNEX_FILE_TYPE_4 = "4";
	/**
	 * '5'：现住址信息.
	 */
	public static final String CREDIT_ANNEX_FILE_TYPE_5 = "5";
	/**
	 * '6'：车产信息.
	 */
	public static final String CREDIT_ANNEX_FILE_TYPE_6 = "6";
	/**
	 * '7'：公司信息.
	 */
	public static final String CREDIT_ANNEX_FILE_TYPE_7 = "7";
	/**
	 * '8'：联保信息.
	 */
	public static final String CREDIT_ANNEX_FILE_TYPE_8 = "8";
	/**
	 * '9'：车辆外观前照.
	 */
	public static final String CREDIT_ANNEX_FILE_TYPE_9 = "9";
	/**
	 * '10'：车辆外观后照.
	 */
	public static final String CREDIT_ANNEX_FILE_TYPE_10 = "10";
	/**
	 * '11'：车辆外观左照.
	 */
	public static final String CREDIT_ANNEX_FILE_TYPE_11 = "11";
	/**
	 * '12'：车辆外观右照.
	 */
	public static final String CREDIT_ANNEX_FILE_TYPE_12 = "12";
	/**
	 * '13'：里程表照片.
	 */
	public static final String CREDIT_ANNEX_FILE_TYPE_13 = "13";
	/**
	 * '14'：车辆后备箱照片.
	 */
	public static final String CREDIT_ANNEX_FILE_TYPE_14 = "14";
	/**
	 * '15'：铭牌招牌.
	 */
	public static final String CREDIT_ANNEX_FILE_TYPE_15 = "15";
	/**
	 * '16'：驾驶证.
	 */
	public static final String CREDIT_ANNEX_FILE_TYPE_16 = "16";
	/**
	 * '17'：车辆行驶本.
	 */
	public static final String CREDIT_ANNEX_FILE_TYPE_17 = "17";
	/**
	 * '18'：征信报告.
	 */
	public static final String CREDIT_ANNEX_FILE_TYPE_18 = "18";
	/**
	 * '19'：购车发票.
	 */
	public static final String CREDIT_ANNEX_FILE_TYPE_19 = "19";
	/**
	 * '20'：交强险合同.
	 */
	public static final String CREDIT_ANNEX_FILE_TYPE_20 = "20";
	/**
	 * '21'：商业险合同.
	 */
	public static final String CREDIT_ANNEX_FILE_TYPE_21 = "21";

	/**
	 * '1'：基本信息备注.
	 */
	public static final String CREDIT_ANNEX_FILE_REMARK_1 = "基本信息";
	/**
	 * '2'：家庭信息备注.
	 */
	public static final String CREDIT_ANNEX_FILE_REMARK_2 = "家庭信息";
	/**
	 * '3'：户籍信息备注.
	 */
	public static final String CREDIT_ANNEX_FILE_REMARK_3 = "户籍信息";
	/**
	 * '4'：房产信息备注.
	 */
	public static final String CREDIT_ANNEX_FILE_REMARK_4 = "房产信息";
	/**
	 * '5'：现住址信息备注.
	 */
	public static final String CREDIT_ANNEX_FILE_REMARK_5 = "现住址信息";
	/**
	 * '6'：车产信息备注.
	 */
	public static final String CREDIT_ANNEX_FILE_REMARK_6 = "车产信息";
	/**
	 * '7'：公司信息备注.
	 */
	public static final String CREDIT_ANNEX_FILE_REMARK_7 = "公司信息";
	/**
	 * '8'：联保信息备注.
	 */
	public static final String CREDIT_ANNEX_FILE_REMARK_8 = "联保信息";
	/**
	 * '9'：车辆外观前照备注.
	 */
	public static final String CREDIT_ANNEX_FILE_REMARK_9 = "车辆外观前照片";
	/**
	 * '10'：车辆外观后照备注.
	 */
	public static final String CREDIT_ANNEX_FILE_REMARK_10 = "车辆外观后照片";
	/**
	 * '11'：车辆外观左照备注.
	 */
	public static final String CREDIT_ANNEX_FILE_REMARK_11 = "车辆外观左照片";
	/**
	 * '12'：车辆外观右照备注.
	 */
	public static final String CREDIT_ANNEX_FILE_REMARK_12 = "车辆外观右照片";
	/**
	 * '13'：里程表照片备注.
	 */
	public static final String CREDIT_ANNEX_FILE_REMARK_13 = "里程表照片";
	/**
	 * '14'：车辆后备箱照片备注.
	 */
	public static final String CREDIT_ANNEX_FILE_REMARK_14 = "车辆后备箱照片";
	/**
	 * '15'：铭牌招牌备注.
	 */
	public static final String CREDIT_ANNEX_FILE_REMARK_15 = "铭牌招牌照片";
	/**
	 * '16'：驾驶证照备注.
	 */
	public static final String CREDIT_ANNEX_FILE_REMARK_16 = "驾驶证照片";
	/**
	 * '17'：车辆行驶本照备注.
	 */
	public static final String CREDIT_ANNEX_FILE_REMARK_17 = "车辆行驶本照片";
	/**
	 * '18'：机动车登记照备注.
	 */
	public static final String CREDIT_ANNEX_FILE_REMARK_18 = "机动车登记照片";
	/**
	 * '19'：购车发票照备注.
	 */
	public static final String CREDIT_ANNEX_FILE_REMARK_19 = "购车发票照片";
	/**
	 * '20'：交强险合同照备注.
	 */
	public static final String CREDIT_ANNEX_FILE_REMARK_20 = "交强险合同照片";
	/**
	 * '21'：商业险合同照备注.
	 */
	public static final String CREDIT_ANNEX_FILE_REMARK_21 = "商业险合同照片";

	public static final String remark = null;

	@Resource
	private CreditAnnexFileDao creditAnnexFileDao;

	@Override
	protected CrudDao<CreditAnnexFile> getEntityDao() {

		return creditAnnexFileDao;
	}

	/**
	 * 
	 * 方法: findCreditCarPage <br>
	 * 描述: 信贷车贷资质审核. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年5月12日 上午9:46:07
	 * 
	 * @param page
	 * @param entity
	 * @return
	 */
	public Page<CreditAnnexFile> findCreditCarPage(Page<CreditAnnexFile> page, CreditAnnexFile entity) {

		entity.setPage(page);
		List<CreditAnnexFile> list = creditAnnexFileDao.findCreditAnnexFileListByType(entity);
		for (CreditAnnexFile model : list) {
			model.setUrl(VIEW_FILE_PATH + model.getUrl());
		}
		page.setList(list);
		return page;
	}

	/**
	 * 
	 * 方法: insertCreditAnnexFile <br>
	 * 描述: 新增信贷客户附件信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月27日 下午2:50:49
	 * 
	 * @param creditAnnexFile
	 * @param i
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertCreditAnnexFile(CreditAnnexFile creditAnnexFile, int i, String id) {

		// 主键ID.
		creditAnnexFile.setId(id);
		// 创建时间.
		creditAnnexFile.setCreateDate(new Date(System.currentTimeMillis() + 1000 * i));
		// 更新时间.
		creditAnnexFile.setUpdateDate(new Date(System.currentTimeMillis() + 1000 * i));

		int flag = 0;
		try {
			flag = creditAnnexFileDao.insert(creditAnnexFile);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("fn:insertCreditAnnexFile,{" + e.getMessage() + "}");
		}
		return flag;
	}

	/**
	 * 
	 * 方法: findCreditAnnexFileList <br>
	 * 描述: 根据客户的各种信贷信息id查找附件列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月1日 上午9:42:20
	 * 
	 * @param creditAnnexFileId
	 * @return
	 */
	public List<CreditAnnexFile> findCreditAnnexFileList(String otherId) {

		return creditAnnexFileDao.findCreditAnnexFileList(otherId);
	}

	/**
	 * 
	 * 方法: findCreditAnnexFileList <br>
	 * 描述: 根据客户ID及资质类型查询附件表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年5月10日 下午6:33:09
	 * 
	 * @param otherId
	 * @param type
	 * @return
	 */
	public List<CreditAnnexFile> findCreditAnnexFileList(CreditAnnexFile creditAnnexFile) {

		return creditAnnexFileDao.findCreditAnnexFileListByType(creditAnnexFile);
	}

	/**
	 * 
	 * 方法: deleteCreditAnnexFileById <br>
	 * 描述: 物理删除附件信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月5日 上午11:10:18
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int deleteCreditAnnexFileById(String id) {

		int flag = 0;
		try {
			flag = creditAnnexFileDao.deleteById(id);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("fn:deleteCreditAnnexFileById,{" + e.getMessage() + "}");
		}
		return flag;
	}

	/**
	 * 用于后台查询资料信息
	 * 
	 * @param page
	 * @param creditAnnexFile
	 * @return
	 */
	public Page<CreditAnnexFile> findPage1(Page<CreditAnnexFile> page, CreditAnnexFile creditAnnexFile) {

		// TODO Auto-generated method stub
		creditAnnexFile.setPage(page);
		page.setList(creditAnnexFileDao.findList1(creditAnnexFile));
		return page;
	}

	/**
	 * 投资端项目资料展示
	 * 
	 * @param annexFile
	 * @return
	 */
	public List<CreditAnnexFile> findCreditAnnexFileToInvestment(CreditAnnexFile annexFile) {

		// TODO Auto-generated method stub
		return creditAnnexFileDao.findCreditAnnexFileToInvestment(annexFile);
	}
	
	//供应商销户
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void deleteCreditAnnexFileByUserId(String userId) {
		creditAnnexFileDao.deleteCreditAnnexFileByUserId(userId);
	}

}