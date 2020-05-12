package com.power.platform.zdw.type;

/**
 * 
 * class: CodeDiscernEnum <br>
 * description: 验证码识别枚举类. <br>
 * author: Roy <br>
 * date: 2019年7月11日 上午11:38:06
 */
public enum CodeDiscernEnum {

	ERROR_CODE_0(0, "识别成功"), ERROR_CODE_10012(10012, "请求超过次数限制");

	private CodeDiscernEnum(Integer value, String text) {

		this.value = value;
		this.text = text;
	}

	private Integer value;
	private String text;

	public Integer getValue() {

		return value;
	}

	public void setValue(Integer value) {

		this.value = value;
	}

	public String getText() {

		return text;
	}

	public void setText(String text) {

		this.text = text;
	}

}
