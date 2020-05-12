package com.power.platform.sys.entity;

import java.util.List;
import java.util.Date;
import com.power.platform.common.persistence.DataEntity;

public class AnnexFile extends DataEntity<AnnexFile> {

	private static final long serialVersionUID = 1L;

	private String url;

	private String otherId;

	private String type;

	private String returnUrl;

	private String dictType;

	private String title;

	private String label;

	private List<String> urlList;

	public List<String> getUrlList() {

		return urlList;
	}

	public void setUrlList(List<String> urlList) {

		this.urlList = urlList;
	}

	public String getLabel() {

		return label;
	}

	public void setLabel(String label) {

		this.label = label;
	}

	public String getUrl() {

		return url;
	}

	public void setUrl(String url) {

		this.url = url;
	}

	public String getOtherId() {

		return otherId;
	}

	public void setOtherId(String otherId) {

		this.otherId = otherId;
	}

	public String getType() {

		return type;
	}

	public void setType(String type) {

		this.type = type;
	}

	public String getReturnUrl() {

		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {

		this.returnUrl = returnUrl;
	}

	public String getDictType() {

		return dictType;
	}

	public void setDictType(String dictType) {

		this.dictType = dictType;
	}

	public String getTitle() {

		return title;
	}

	public void setTitle(String title) {

		this.title = title;
	}

}
