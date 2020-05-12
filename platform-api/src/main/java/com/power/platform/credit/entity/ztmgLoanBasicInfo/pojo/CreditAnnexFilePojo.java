package com.power.platform.credit.entity.ztmgLoanBasicInfo.pojo;

/**
 * 
 * 类: CreditAnnexFilePojo <br>
 * 描述: 附件POJO. <br>
 * 作者: Mr.Roy <br>
 * 时间: 2018年5月7日 上午11:33:36
 */
public class CreditAnnexFilePojo {

	private String id; // 主键.
	private String otherId; // 外键.
	private String url; // URL.
	private String type;

	public String getId() {

		return id;
	}

	public void setId(String id) {

		this.id = id;
	}

	public String getOtherId() {

		return otherId;
	}

	public void setOtherId(String otherId) {

		this.otherId = otherId;
	}

	public String getUrl() {

		return url;
	}

	public void setUrl(String url) {

		this.url = url;
	}

	public String getType() {

		return type;
	}

	public void setType(String type) {

		this.type = type;
	}

}
