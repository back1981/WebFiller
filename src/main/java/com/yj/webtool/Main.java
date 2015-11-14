package com.yj.webtool;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class Main {
	
	public static void main(String[] args) {
		Logger logger = Logger.getLogger(Main.class);
		try {
			if(args.length < 1) {
				System.out.println("Usage:java -jar webtool.jar c:/config.properties");
				return;
			}
			String configFilePath = args[0];
			Config.getInstance().init(configFilePath);
//			Config.getInstance().init("F:\\studio\\webtool_workspace\\webtool\\src\\test\\resources\\config.properties");
			System.setProperty("webdriver.firefox.bin", Config.getInstance().getProp("firefox"));
			long interval = Long.parseLong(Config.getInstance().getProp("submitInterval"));
			while(true) {
				long start = System.currentTimeMillis();
				List<Map<String, String>> sellers = new DBReader().read();
				logger.info("records num:" + sellers.size());
				if(sellers.size() > 0) {
					new AutoSeller().autoSell(Config.getInstance().getProp("url"), sellers);
				}
				long end = System.currentTimeMillis();
				long sleep = interval * 1000 - (end - start);
				if(sleep > 0) {
					Thread.sleep(sleep);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
