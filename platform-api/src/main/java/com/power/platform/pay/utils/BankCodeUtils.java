package com.power.platform.pay.utils;



public class BankCodeUtils {
	private static String chinaPostBankcode="01000000";  //中国邮政储蓄银行 
	private static String  chinaBankcode="01040000";            //中国银行
	private static String  chinaConstructionBankcode="01050000";  //中国建设银行
	private static String  SPDBankcode="03100000";                //上海浦东发展银行
	
	
	private static String  agriculturalBankcode="01030000";  //中国农业银行
	private static String  ICBCBankcode="01020000";              //中国工商银行   
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

	public static String  getBankCode(String name){		
		switch(name){
			case "youzheng":
				bankCode = chinaPostBankcode;
				break;
			case "zhongguo":
				bankCode = chinaBankcode;
			    break;
			case "jianshe":
				bankCode = chinaConstructionBankcode;
				break;
			case "pufa":
				bankCode = SPDBankcode;
				break;		
			
			case "nongye":
				bankCode = agriculturalBankcode;
				break;
			case "gongshang":
				bankCode = ICBCBankcode;
				break;
			
			case "zhaoshang":
				bankCode = merchantsBankcode;
				break;
			case "jiaotong":
				bankCode = communicationBankcode;
				break;			
			
			case "guangda":
				bankCode = chinaEverbrightBankcode;
				break;
			case "beijing":
				bankCode = beijingBankcode;
				break;
				
			case "minsheng":
				bankCode = minshengBankcode;
				break;
			case "zhongxin":
				bankCode = CITICBankcode;
				break;			
			
			case "huaxia":
				bankCode = huxiaBankcode;
				break;
			case "guangfa":
				bankCode = CBGBankcode;
				break;
			case "shanghai":
				bankCode = shanghai;
				break;				
			
			case "pingan":
				bankCode = pinganBankcode;
				break;
				
			case "xingye":
				bankCode = xingyeBankCode;
				break;	
		}		
		
		return bankCode;
	}
	
	
	public static String  getBankName(String bankCode){		
		switch(bankCode){
			case "01000000":
				bankName = "中国邮政储蓄银行";
				break;
			case "01040000":
				bankName = "中国银行";
			    break;
			case "01050000":
				bankName = "中国建设银行";
				break;
			case "03100000":
				bankName = "浦发银行";
				break;		
			case "01030000":
				bankName = "农业银行";
				break;
			case "01020000":
				bankName = "中国工商银行";
				break;
			case "03080000":
				bankName = "招商银行";
				break;
			case "03090000":
				bankName = "兴业银行";
				break;
			case "03010000":
				bankName = "交通银行";
				break;			
			case "03030000":
				bankName = "光大银行";
				break;
			case "04031000":
				bankName = "北京银行";
				break;				
			case "03050000":
				bankName = "民生银行";
				break;
			case "03020000":
				bankName = "中信银行";
				break;			
			case "03040000":
				bankName = "华夏银行";
				break;
			case "03060000":
				bankName = "广发银行";
				break;
			case "04012900":
				bankName = "上海银行";
				break;							
			case "03070000":
				bankName = "平安银行";
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
			case "01000000":
				limitDayAmountString = "5千";
				break;
			case "01040000":
				limitDayAmountString = "20万";
			    break;
			case "01050000":
				limitDayAmountString = "10万";
				break;
			case "03100000":
				limitDayAmountString = "5千";
				break;		
			
			case "01030000":
				limitDayAmountString = "5万";
				break;
			case "01020000":
				limitDayAmountString = "5万";
				break;
			
			case "03080000":
				limitDayAmountString = "5万";
				break;
			case "03010000":
				limitDayAmountString = "20万";
				break;			
			
			case "03030000":
				limitDayAmountString = "100万";
				break;
			case "04031000":
				limitDayAmountString = "5千";
				break;
				
			case "03050000":
				limitDayAmountString = "100万";
				break;
			case "03020000":
				limitDayAmountString = "100万";
				break;			
			
			case "03040000":
				limitDayAmountString = "100万";
				break;
			case "03060000":
				limitDayAmountString = "无限额";
				break;
			case "04012900":
				limitDayAmountString = "5千";
				break;				
			
			case "03070000":
				limitDayAmountString = "100万";
				break;		
			case "03090000":
				limitDayAmountString = "5万";
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
			case "01000000":
				limitSingleAmountString = "5千";
				break;
			case "01040000":
				limitSingleAmountString = "5万";
			    break;
			case "01050000":
				limitSingleAmountString = "5万";
				break;
			case "03100000":
				limitSingleAmountString = "5千";
				break;		
			
			case "01030000":
				limitSingleAmountString = "1万";
				break;
			case "01020000":
				limitSingleAmountString = "5万";
				break;
			
			case "03080000":
				limitSingleAmountString = "5万";
				break;
			case "03010000":
				limitSingleAmountString = "5万";
				break;			
			
			case "03030000":
				limitSingleAmountString = "50万";
				break;
			case "04031000":
				limitSingleAmountString = "5千";
				break;
				
			case "03050000":
				limitSingleAmountString = "50万";
				break;
			case "03020000":
				limitSingleAmountString = "50万";
				break;			
			
			case "03040000":
				limitSingleAmountString = "50万";
				break;
			case "03060000":
				limitSingleAmountString = "50万";
				break;
			case "04012900":
				limitSingleAmountString = "5千";
				break;				
			
			case "03070000":
				limitSingleAmountString = "50万";
				break;		
			case "03090000":
				limitSingleAmountString = "5万";
				break;
		}		
		
		return limitSingleAmountString;
	}
	
}
