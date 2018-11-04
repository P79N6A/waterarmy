package com.xiaopeng.waterarmy.common.enums;

/**
 * 字符集编码的枚举
 * @author iason
 * @date 2017年8月21日 上午11:18:20
 */
public enum CharsetEnum {
	/**
	 * UTF-8
	 */
	UTF_8("UTF-8"),
	/**
	 * GBK
	 */
	GBK("GBK"),

	/**
	 * GB2312
	 */
	GB2312("GB2312");
	private String value = null;

	private CharsetEnum(String value){
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
