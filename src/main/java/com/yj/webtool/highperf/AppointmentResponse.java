package com.yj.webtool.highperf;

public class AppointmentResponse extends BaseResponse {
	private String bookingId = null;
	private String cityId = null;
	public String getBookingId() {
		return bookingId;
	}
	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}
	
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	
	
}
