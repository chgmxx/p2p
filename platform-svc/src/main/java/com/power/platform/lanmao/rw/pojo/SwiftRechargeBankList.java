package com.power.platform.lanmao.rw.pojo;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

/**
 * 快捷充值支持银行和单笔、单日充值上限配置
 * @author pm
 *
 */
public final class SwiftRechargeBankList {
	private SwiftRechargeBankList() {}
	public static HashMap<String, String> swiftBankList = new HashMap<String, String>(); 
	static {
		swiftBankList.put("ICBK", "50000,50000");// 工商银行
		swiftBankList.put("ABOC", "1100,10000");//农业银行
		swiftBankList.put("PCBC", "50000,50000");//建设银行
		swiftBankList.put("BKCH", "50000,50000");//中国银行
		swiftBankList.put("COMM", "50000,50000");//交通银行
		swiftBankList.put("EVER", "650,20000");//光大银行
		swiftBankList.put("GDBK", "50000,50000");//广发银行
		swiftBankList.put("FJIB", "50000,50000");//兴业银行
		swiftBankList.put("SZDB", "50000,50000");//平安银行
		swiftBankList.put("SPDB", "50000,50000");//浦发银行
		swiftBankList.put("BOSH", "50000,50000");//上海银行
		swiftBankList.put("PSBC", "5000,5000");//邮储银行
		swiftBankList.put("CIBK", "10000,10000");//中信银行
		swiftBankList.put("BJCN", "20000,20000");//北京银行
		swiftBankList.put("HXBK", "650,20000");//华夏银行
	}
	
	/**
	 *   通过银行编码查找是否支持， 如果支持返回swiftVo对象；否则返回null
	 * @return
	 */
	public static SwiftVO getByNo(String bankNo) {
		SwiftVO swiftVo = null;
		if(!StringUtils.isBlank(bankNo)) {
			String values = swiftBankList.get(bankNo);
			if(StringUtils.isBlank(values)) {
				return swiftVo;
			}else {
				String[] arr = values.split(",");
				swiftVo = new SwiftVO();
				swiftVo.setBankNo(bankNo);
				swiftVo.setMaxByEachStroke(Integer.valueOf(arr[0]));
				swiftVo.setMaxByEachDay(Integer.valueOf(arr[1]));
				swiftVo.setMin(5);
				return swiftVo;
			}
		}
		return swiftVo;
	}
}
