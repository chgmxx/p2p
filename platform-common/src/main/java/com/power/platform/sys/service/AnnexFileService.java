package com.power.platform.sys.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.sys.dao.AnnexFileDao;
import com.power.platform.sys.entity.AnnexFile;

@Service("annexFileService")
@Transactional(readOnly = true)
public class AnnexFileService extends CrudService<AnnexFile> {

	private static final Logger logger = Logger.getLogger(AnnexFileService.class);

	@Resource
	private AnnexFileDao annexFileDao;

	 
	public List<AnnexFile> findAnnexFilesByWloanTermDoc(AnnexFile annexFile) {

		List<AnnexFile> list = null;
		try {
			list = annexFileDao.findList(annexFile);
			String urlStr = "";
			List<String> urlList = null;
			if (list != null && list.size() > 0) {
				for (AnnexFile aFile : list) {
					urlStr = aFile.getUrl();
					urlList = new ArrayList<String>();
					String urlArr[] = urlStr.split("\\|");
					for (int i = 1; i < urlArr.length; i++) {
						urlList.add(urlArr[i]);
					}
					// 照片列表.
					aFile.setUrlList(urlList);
				}
			}
			logger.info("fn:findAnnexFilesByWloanTermDoc,{查询定期融资档案附件信息成功}");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:findAnnexFilesByWloanTermDoc,{查询定期融资档案附件信息异常：" + e.getMessage() + "}");
		}

		return list;
	}

	 
	public List<AnnexFile> findAll() {

		// TODO Auto-generated method stub
		return null;
	}

	 
	protected CrudDao<AnnexFile> getEntityDao() {

		return annexFileDao;
	}

	 
	public Integer findCount(AnnexFile annexFile) {

		return annexFileDao.findCount(annexFile);
	}

	 
	public List<AnnexFile> findAnnexFileMap(String otherId) {
		List<AnnexFile> oldlist = annexFileDao.findAnnexFileMap(otherId);
		List<AnnexFile> newList = new ArrayList<AnnexFile>();
		String urlStr = "";
		List<String> urlList = null;
		for (AnnexFile annexFile : oldlist) {
			urlStr = annexFile.getUrl();
			urlList = new ArrayList<String>();
			String urlArr[] = urlStr.split("\\|");
			for (int i = 1; i < urlArr.length; i++) {
				urlList.add(urlArr[i]);
			}
			annexFile.setUrlList(urlList);
			newList.add(annexFile);
		}
		return newList;
	}

	@Transactional(readOnly=false)
	public void deleteAnnexFile(String id) {
		annexFileDao.deleteAnnexFile(id);		
	}

    /**
     * 根据OtherId 查询飞附件
     * @param id
     * @return
     */
	public AnnexFile findByOtherId(String id) {
		// TODO Auto-generated method stub
		return annexFileDao.findByOtherId(id);
	}

}
