package com.yj.webtool.highperf;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.yj.webtool.IAutoSeller;
import com.yj.webtool.webdriver.AutoSeller;
import com.yj.webtool.webdriver.DBReader;
import com.yj.webtool.webdriver.SubmitStatus;

public class AutoSellerHighPerfImpl implements IAutoSeller{
	Logger logger = Logger.getLogger(AutoSellerHighPerfImpl.class);
	public static long succ = 0;
	public static long failed = 0;
	Seller seller = new Seller();
	private OnlineForm makeForm(Map<String, String> sellerInfo) {
		OnlineForm form = new OnlineForm();
		form.setName(sellerInfo.get("name"));
		form.setMobile(sellerInfo.get("cel_num"));
		form.setVehicleType(sellerInfo.get("brand"));
		form.setExpectTime("计划卖车时间:一周内.");
		form.setCity(sellerInfo.get("city"));
		return form;
	}
	@Override
	public void autoSell(String url, List<Map<String, String>> sellerList) throws Exception {
		for(int i = 0; i < sellerList.size(); i++) {
			SubmitStatus status = SubmitStatus.SUCC;
			String alertMsg = null;
			Map<String, String> sellerInfo = sellerList.get(i);
			OnlineForm form = makeForm(sellerInfo);
			
			Seller parse = new Seller();
			form.setSessionKey(seller.createSessionKey());
			
			try {
				seller.handleServerTime(form);
				parse.appointment(form);
				parse.getYuYueQuery(form);
				parse.refreshCheckCode(form);
				parse.submit(form);
				succ++;
			} catch (Exception e) {
//				e.printStackTrace();
				status = SubmitStatus.FAILED;
				alertMsg = e.getMessage();
				failed++;
			}
			logger.info(sellerInfo + ", result=" + status + ", msg=" + alertMsg);
			new DBReader().updateStatus(status, sellerInfo.get("cel_num"));
			Thread.sleep(1000);
		}
		
	}

}
