package com.yj.webtool;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


public class FillerTest {
	@Test
	public void fillTest(){
		Filler filler = new Filler();
		System.setProperty("webdriver.firefox.bin", "E:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe");
		
		Map<String, String> values = new HashMap<String, String>();
		values.put("vehicleType", "����");
		values.put("cel_num", "13911111111");
		values.put("name", "����");
		values.put("brand", "ͨ��");
		values.put("city", "����");
		filler.fill("http://www.pahaoche.com/yuyue_140807a.w?ch=yy-mishixinxi-150928", values);
	}
}
