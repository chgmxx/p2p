package com.power.platform.pay.utils;



public class CGBBankCodeUtils {
	private static String chinaPostBankcode="PSBC";  //中国邮政储蓄银行 
	private static String  chinaBankcode="BOC";            //中国银行
	private static String  chinaConstructionBankcode="01050000";  //中国建设银行
	private static String  SPDBankcode="03100000";                //上海浦东发展银行
	
	
	private static String  agriculturalBankcode="01030000";  //中国农业银行
	
	private static String  merchantsBankcode="03080000";          //招商银行 
	private static String  communicationBankcode="03010000";      //交通银行
	private static String  xingyeBankCode = "03090000";			// 兴业银行
	
	
	private static String  chinaEverbrightBankcode="03030000";  //中国光大银行
	private static String  beijingBankcode="04031000";         //北京银行
	private static String  minshengBankcode="03050000";           //中国民生银行
	private static String  CITICBankcode="03020000";             //中信银行
	
	
	private static String  huxiaBankcode="03040000";   //华夏银行
	private static String  CBGBankcode="03060000";      //广东发展银行
	private static String  shanghai="04012900";          //上海银行 
	
	private static String  pinganBankcode="03070000";   //平安银行	
    private static String bankCode="";
    private static String bankName="";
    private static String limitDayAmountString="";
    private static String limitSingleAmountString="";
    
    private static String  ICBCBankcode="ICBC";//中国工商银行   
    private static String  ABCBankcode="ABC";  //中国农业银行
    private static String  BOCbankcode="BOC";  //中国银行
    private static String  CCBbankcode="CCB";  //中国建设银行
    private static String  BOCOMbankcode="BOCOM";//交通银行
    private static String  CMBCbankcode="CMBC";//民生银行
    private static String  CIBbankcode="CIB"; //兴业银行
    private static String  CEBbankcode="CEB"; //光大银行
    private static String  PABbankcode="PAB"; //平安银行
    private static String  CNCBbankcode="CNCB";//中信银行
    private static String  BCCBbankcode="BCCB";//北京银行
    private static String  GDBbankcode="GDB";//广发银行
    private static String  CMBbankcode="CMB";//招商银行
    private static String  PSBCbankcode="PSBC";//邮政银行
    private static String  SPDBCMBbankcode="SPDB";//浦发银行
    private static String  HXBbankcode="HXB";//华夏银行
    
	
	
	public static String  getBankName(String bankCode){		
		switch(bankCode){
		    case "ICBC":
			bankName = "中国工商银行";
			break;
			case "ABC":
				bankName = "农业银行";
				break;
			case "BOC":
				bankName = "中国银行";
			    break;
			case "CCB":
				bankName = "中国建设银行";
				break;
			case "BOCOM":
				bankName = "交通银行";
				break;	
			case "CMBC":
				bankName = "民生银行";
				break;	
			case "CIB":
				bankName = "兴业银行";
				break;
			case "CEB":
				bankName = "光大银行";
				break;	
			case "PAB":
				bankName = "平安银行";
				break;	
			case "CNCB":
				bankName = "中信银行";
				break;	
			case "BCCB":
				bankName = "北京银行";
				break;	
			case "GDB":
				bankName = "广发银行";
				break;		
			case "CMB":
				bankName = "招商银行";
				break;	
			case "PSBC":
				bankName = "中国邮政储蓄银行";
				break;
			case "SPDB":
				bankName = "浦发银行";
				break;		
			case "HXB":
				bankName = "华夏银行";
				break;
		}		
		
		return bankName;
	}
	
	/**
	 * 单日限额
	 * @param name
	 * @return
	 */
	public static String  getDayLimit(String bankCode){		
		switch(bankCode){
	    case "ICBC":
	    	limitDayAmountString = "5万元";
		    break;
		case "ABC":
			limitDayAmountString = "维护中";
			break;
		case "BOC":
			limitDayAmountString = "20万元";
		    break;
		case "CCB":
			limitDayAmountString = "5万元";
			break;
		case "BOCOM":
			limitDayAmountString = "20万元";
			break;	
		case "CMBC":
			limitDayAmountString = "5万元";
			break;	
		case "CIB":
			limitDayAmountString = "5万元";
			break;
		case "CEB":
			limitDayAmountString = "5万元";
			break;	
		case "PAB":
			limitDayAmountString = "5万元";
			break;	
		case "CNCB":
			limitDayAmountString = "2.5万元";
			break;	
		case "BCCB":
			limitDayAmountString = "5000元";
			break;	
		case "GDB":
			limitDayAmountString = "无限额";
			break;		
		case "CMB":
			limitDayAmountString = "2万元";
			break;	
		case "PSBC":
			limitDayAmountString = "5000元";
			break;
		case "SPDB":
			limitDayAmountString = "5万元";
			break;		
		case "HXB":
			limitDayAmountString = "2000元";
			break;
	}		
	
		return limitDayAmountString;
	}
	
	/**
	 * 单笔限额
	 * @param name
	 * @return
	 */
	public static String  getSingleLimit(String bankCode){		
		switch(bankCode){
	    case "ICBC":
	    	limitSingleAmountString = "5万元";
		    break;
		case "ABC":
			limitSingleAmountString = "维护中";
			break;
		case "BOC":
			limitSingleAmountString = "5万元";
		    break;
		case "CCB":
			limitSingleAmountString = "5万元";
			break;
		case "BOCOM":
			limitSingleAmountString = "10万元";
			break;	
		case "CMBC":
			limitSingleAmountString = "1000元";
			break;	
		case "CIB":
			limitSingleAmountString = "5万元";
			break;
		case "CEB":
			limitSingleAmountString = "5万元";
			break;	
		case "PAB":
			limitSingleAmountString = "5万元";
			break;	
		case "CNCB":
			limitSingleAmountString = "5000元";
			break;	
		case "BCCB":
			limitSingleAmountString = "5000元";
			break;	
		case "GDB":
			limitSingleAmountString = "无限额";
			break;		
		case "CMB":
			limitSingleAmountString = "1万元";
			break;	
		case "PSBC":
			limitSingleAmountString = "5000元";
			break;
		case "SPDB":
			limitSingleAmountString = "5万元";
			break;		
		case "HXB":
			limitSingleAmountString = "1000元";
			break;
	}			
		
		return limitSingleAmountString;
	}
	
}
