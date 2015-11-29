package com.yj.webtool.highperf;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class Seller {
	Logger logger = Logger.getLogger(Seller.class);
	String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	Client client = Client.create();
	String sessionKey = null;

//	public void parser(String url) {
//		try {
//			logger.info("open url");
//			Document doc = Jsoup.connect(url).get();
//			logger.info("url opened:");
//			// Thread.sleep(3000);
//			Element el = doc.getElementById("yuyueId");
//			logger.info("yuyueId found:" + el.attr("value"));
//
//			el = doc.getElementById("tokenKey");
//			logger.info("tokenKey found:" + el.val());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

	public String createSessionKey() {
		String retval = System.currentTimeMillis()
				+ String.valueOf((long) (Math.random() * 10000));
//		System.out.println(retval);
		return retval;
	}

	public void appointment(OnlineForm form) throws Exception {
		WebResource webResource = client
				.resource("http://gw2.pahaoche.com/wghttp/internal/appointment?sessionKey="
						+ sessionKey);
		MultivaluedMap params = new MultivaluedMapImpl();
		params.add("sessionKey", sessionKey);
		params.add("yuyueId", "");
		params.add("channel", "yy-mishixinxi-150928");
		params.add("from", "GW");
		params.add("tokenKey", form.getTokenKey());
		params.add("tokenValue", form.getTokenValue());
		params.add("expectTime", form.getExpectTime());
		params.add("name", form.getName());
		params.add("mobile", form.getMobile());
		params.add("city", form.getCity());
		params.add("vehicleType", form.getVehicleType());
		ClientResponse response = webResource.queryParams(params)
				.accept("application/json").get(ClientResponse.class);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}

		String output = response.getEntity(String.class);

		AppointmentResponse objResponse = (AppointmentResponse) GsonUtils
				.fromJson(output, AppointmentResponse.class);
		if ("false".equalsIgnoreCase(objResponse.getResult())) {
			throw new Exception(objResponse.getMessage());
		}
		form.setYuyueId(objResponse.getBookingId());
//		System.out.println("Output from Server .... \n");
//		System.out.println(output);

	}

	public String refreshCheckCode() {
		WebResource webResource = client
				.resource("http://gw2.pahaoche.com/wghttp/randomImageServlet?Rand=4&sessionKey="
						+ sessionKey);
		ClientResponse response = webResource.accept("application/json").get(
				ClientResponse.class);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}

		return sessionKey;

	}

	public String uuid() {
		int radix = 62;
		byte[] chars = CHARS.getBytes();
		byte[] uuid = new byte[36];
		uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
		uuid[14] = '4';
		int r = 0;
		for (int i = 0; i < 36; i++) {
			if (uuid[i] == 0) {
				r = 0 | (int) (Math.random() * 16);
				uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
			}
		}
		String strUuid = new String(uuid);
		logger.info("uuid:" + strUuid);
		return strUuid;
	}

	public void handleServerTime(OnlineForm form) {

		String tokenKey = uuid();
		form.setTokenKey(tokenKey);
		WebResource webResource = client
				.resource("http://gw2.pahaoche.com/wghttp/internal/accessToken/"
						+ tokenKey);

		ClientResponse response = webResource.accept("application/json").get(
				ClientResponse.class);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}

		String getTokenResponse = response.getEntity(String.class);
		// Gson gson = new Gson();
		GetTokenResponse tokenResponse = (GetTokenResponse) GsonUtils.fromJson(
				getTokenResponse, GetTokenResponse.class);
		// System.out.println(tokenResponse);
		form.setTokenValue(tokenResponse.getToken());
	}
	
	
	public void submit(OnlineForm form) {
		MultivaluedMap params = new MultivaluedMapImpl();
		params.add("yuyueId", form.getYuyueId());
		params.add("channel", "yy-mishixinxi-150928");
		params.add("from", "GW");
		params.add("tokenKey", form.getTokenKey());
		params.add("tokenValue", form.getTokenValue());
		params.add("expectTime", form.getExpectTime());
		params.add("name", form.getName());
		params.add("mobile", form.getMobile());
		params.add("city", form.getCity());
		params.add("vehicleType", form.getVehicleType());
		WebResource webResource = client
				.resource("http://www.pahaoche.com/sellcar/express.html");
		ClientResponse response = webResource.type("application/x-www-form-urlencoded") .post(ClientResponse.class, params);
//		String output = response.getEntity(String.class);
//		System.out.println(output);
	}

	public static void main(String[] args) {
		// new
		// Parser().parser("http://www.pahaoche.com/yuyue_140807a.w?ch=yy-mishixinxi-150928");
		// new Parser().getYuyueId();
		OnlineForm form = new OnlineForm();
		form.setName("小白");
		form.setMobile("13978958697");
		form.setVehicleType("BMW X1");
		form.setExpectTime("计划卖车时间:一周内.");
		form.setCity("上海");
		
		Seller parse = new Seller();
		form.setSessionKey(parse.createSessionKey());
		parse.handleServerTime(form);
		try {
			parse.appointment(form);
			parse.submit(form);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
