/**
 * 手机号校验
 */
var regPhone = /^1[345789]\d{9}$/;

/**
 * 密码校验 密码格式为6-16位数字加字母
 */
var regPwd = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$/;

/**
 * 金额校验
 */
var regAmount = /^\d+(\.\d{1,2})?$/;

/**
 * 汉字校验
 */
var regWord = /^[\u4e00-\u9fa5]+$/;

/**
 * 手机号校验
 * @param phone
 * @returns {Boolean}
 */
function checkPhone(phone){
	if(!regPhone.test(phone)) {
		return false;
	}else{
		return true;
	}
}

/**
 * 密码校验
 * @param pwd
 * @returns {Boolean}
 */
function checkPwd(pwd){
	if(!regPwd.test(pwd)) {
		return false;
	}else{
		return true;
	}
}

/**
 * 金额校验
 * @param amount
 * @returns {Boolean}
 */
function checkAmount(amount){
	if(!regAmount.test(amount)) {
		return false;
	}else{
		return true;
	}
}

/**
 * 汉字校验
 * @param word
 * @returns {Boolean}
 */
function checkWord(word){
	if(!regWord.test(word)) {
		return false;
	}else{
		return true;
	}
}
