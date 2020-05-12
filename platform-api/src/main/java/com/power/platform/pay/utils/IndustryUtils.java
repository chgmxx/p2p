package com.power.platform.pay.utils;

public class IndustryUtils {
	private static String  INDUSTRY_TYPE01="INDUSTRY_01";  //农林牧渔业
	private static String  INDUSTRY_TYPE02="INDUSTRY_02";  //采矿业
	private static String  INDUSTRY_TYPE03="INDUSTRY_03";  //制造业
	private static String  INDUSTRY_TYPE04="INDUSTRY_04";  //电力热力燃气及水生产
	private static String  INDUSTRY_TYPE05="INDUSTRY_05";  //供应业
	private static String  INDUSTRY_TYPE06="INDUSTRY_06";  //建筑业 
	private static String  INDUSTRY_TYPE07="INDUSTRY_07";  //批发和零售业 
	private static String  INDUSTRY_TYPE08="INDUSTRY_08";  //交通运输仓储业
	private static String  INDUSTRY_TYPE09="INDUSTRY_09";  // 住宿和餐饮业
	private static String  INDUSTRY_TYPE10="INDUSTRY_10";  //信息传输软件和信息技术服务业
	private static String  INDUSTRY_TYPE11="INDUSTRY_11";  //金融业
	private static String  INDUSTRY_TYPE12="INDUSTRY_12";  //房地产业
	private static String  INDUSTRY_TYPE13="INDUSTRY_13";  //租赁和商务服务业
	private static String  INDUSTRY_TYPE14="INDUSTRY_14";  //科研和技术服务业
	private static String  INDUSTRY_TYPE15="INDUSTRY_15";  //水利环境和公共设施管理业
	private static String  INDUSTRY_TYPE16="INDUSTRY_16";  //居民服务修理和其他服务业 
	private static String  INDUSTRY_TYPE17="INDUSTRY_17";  //教育	
	private static String  INDUSTRY_TYPE18="INDUSTRY_18";  //卫生和社会工作
	private static String  INDUSTRY_TYPE19="INDUSTRY_19";  //文化体育和娱乐业
	private static String  INDUSTRY_TYPE20="INDUSTRY_20";  //公共管理
	private static String  INDUSTRY_TYPE21="INDUSTRY_21";  //社会保障和社会组织
    private static String industryName="";
    
	public static String  getindustryName(String industryCode){		
		switch(industryCode){
			case "INDUSTRY_01":
				industryName = "农林牧渔业";
				break;
			case "INDUSTRY_02":
				industryName = "采矿业";
			    break;
			case "INDUSTRY_03":
				industryName = "制造业";
				break;
			case "INDUSTRY_04":
				industryName = "电力热力燃气及水生产";
				break;		
			case "INDUSTRY_05":
				industryName = "供应业";
				break;
			case "INDUSTRY_06":
				industryName = "建筑业";
				break;
			case "INDUSTRY_07":
				industryName = "批发和零售业";
				break;
			case "INDUSTRY_08":
				industryName = "交通运输仓储业";
				break;
			case "INDUSTRY_09":
				industryName = "住宿和餐饮业";
				break;			
			case "INDUSTRY_10":
				industryName = "信息传输软件和信息技术服务业";
				break;
			case "INDUSTRY_11":
				industryName = "金融业";
				break;				
			case "INDUSTRY_12":
				industryName = "房地产业";
				break;
			case "INDUSTRY_13":
				industryName = "租赁和商务服务业";
				break;			
			case "INDUSTRY_14":
				industryName = "科研和技术服务业";
				break;
			case "INDUSTRY_15":
				industryName = "水利环境和公共设施管理业";
				break;
			case "INDUSTRY_16":
				industryName = "居民服务修理和其他服务业";
				break;							
			case "INDUSTRY_17":
				industryName = "教育";
				break;	
			case "INDUSTRY_18":
				industryName = "卫生和社会工作";
				break;
			case "INDUSTRY_19":
				industryName = "文化体育和娱乐业";
				break;
			case "INDUSTRY_20":
				industryName = "公共管理";
				break;
			case "INDUSTRY_21":
				industryName = "社会保障和社会组织";
				break;
				
		}		
		
		return industryName;
	}
}
