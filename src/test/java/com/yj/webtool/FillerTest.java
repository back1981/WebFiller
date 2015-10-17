package com.yj.webtool;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


public class FillerTest {
	@Test
	public void fillTest(){
		Config.getInstance().init("F:\\studio\\webtool_workspace\\webtool\\src\\test\\resources\\config.properties");
		AutoSeller filler = new AutoSeller();
		long phone = 13911111107L;
		for(int i = 0; i < 3;i++) {
			System.setProperty("webdriver.firefox.bin", Config.getInstance().getProp("firefox"));
			
			Map<String, String> values = new HashMap<String, String>();
			values.put("vehicleType", "天籁");
			values.put("cel_num", String.valueOf(phone++));
			values.put("name", "李明");
			values.put("brand", "通用");
			values.put("city", "合肥");
			try {
				filler.autoSell("http://www.pahaoche.com/yuyue_140807a.w?ch=yy-mishixinxi-150928", values);
			}  catch(CaptcharSolveException e) {
				e.printStackTrace();
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
