package com.yj.webtool.webdriver;

public enum SubmitStatus {
	INIT(0),SUCC(1),FAILED(2),CHECK_CODE_ERR(3);
	private int status = 0;
	private SubmitStatus(int status) {
		this.status = status;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
