package com.power.platform.lanmao.common;

public enum SignatureAlgorithm {

	SHA1WithRSA("SHA1WithRSA"),

	MD5WithRSA("MD5WithRSA");

	private String signAlgorithm;

	private SignatureAlgorithm(String signAlgorithm) {

		this.signAlgorithm = signAlgorithm;
	}

	public String getSignAlgorithm() {

		return signAlgorithm;
	}
}
