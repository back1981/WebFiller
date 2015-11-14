package com.yj.webtool;

public enum SubmitStatus {
	INIT(0),SUCC(1),FAILED(2);
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
