package com.power.platform.zdw.type;

public enum LeiEnum {

	/**
	 * A-农、林、牧、渔业
	 */
	LEI_A("A", "农、林、牧、渔业"),
	/**
	 * B-采矿业
	 */
	LEI_B("B", "采矿业"),
	/**
	 * C-制造业
	 */
	LEI_C("C", "制造业"),
	/**
	 * D-电力、热力、燃气及水生产和供应业
	 */
	LEI_D("D", "电力、热力、燃气及水生产和供应业"),
	/**
	 * E-建筑业
	 */
	LEI_E("E", "建筑业"),
	/**
	 * F-批发和零售业
	 */
	LEI_F("F", "批发和零售业"),
	/**
	 * G-交通运输、仓储和邮政业
	 */
	LEI_G("G", "交通运输、仓储和邮政业"),
	/**
	 * H-住宿和餐饮业
	 */
	LEI_H("H", "住宿和餐饮业"),
	/**
	 * I-信息传输、软件和信息技术服务业
	 */
	LEI_I("I", "信息传输、软件和信息技术服务业"),
	/**
	 * J-金融业
	 */
	LEI_J("J", "金融业"),
	/**
	 * K-房地产业
	 */
	LEI_K("K", "房地产业"),
	/**
	 * L-租赁和商务服务业
	 */
	LEI_L("L", "租赁和商务服务业"),
	/**
	 * M-科学研究和技术服务业
	 */
	LEI_M("M", "科学研究和技术服务业"),
	/**
	 * N-水利、环境和公共设施管理业
	 */
	LEI_N("N", "水利、环境和公共设施管理业"),
	/**
	 * O-居民服务、修理和其他服务业
	 */
	LEI_O("O", "居民服务、修理和其他服务业"),
	/**
	 * P-教育
	 */
	LEI_P("P", "教育"),
	/**
	 * Q-卫生和社会工作
	 */
	LEI_Q("Q", "卫生和社会工作"),
	/**
	 * R-文化、体育和娱乐业
	 */
	LEI_R("R", "文化、体育和娱乐业"),
	/**
	 * S-公共管理、社会保障和社会组织
	 */
	LEI_S("S", "公共管理、社会保障和社会组织"),
	/**
	 * T-国际组织
	 */
	LEI_T("T", "国际组织"),
	/**
	 * 9999-其他
	 */
	LEI_9999("9999", "其他");

	private LeiEnum(String value, String text) {

		this.value = value;
		this.text = text;
	}

	private String value;
	private String text;

	public String getValue() {

		return value;
	}

	public void setValue(String value) {

		this.value = value;
	}

	public String getText() {

		return text;
	}

	public void setText(String text) {

		this.text = text;
	}

}
