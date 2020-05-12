package com.power.platform.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.sys.entity.AnnexFile;

/**
 * 区域DAO接口
 * @author wangjingsong
 * @version 2014-05-16
 */
@MyBatisDao
public interface AnnexFileDao extends CrudDao<AnnexFile> {
	public Integer findCount(AnnexFile annexFile);
	
	public List<AnnexFile> findAnnexFileMap(String otherId);
	
	public void deleteAnnexFile(String id);

	public AnnexFile findByOtherId(@Param("otherId") String id);
}
