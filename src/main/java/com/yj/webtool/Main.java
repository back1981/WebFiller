package com.yj.webtool;

import java.util.List;
import java.util.Map;

public class Main {
	public static void main(String[] args) {
		try {
			if(args.length < 1) {
				System.out.println("Usage:java -jar webtool.jar c:/config.properties");
				return;
			}
			String configFilePath = args[0];
			Config.getInstance().init(configFilePath);
//			Config.getInstance().init("F:\\studio\\webtool_workspace\\webtool\\src\\test\\resources\\config.properties");
			System.setProperty("webdriver.firefox.bin", Config.getInstance().getProp("firefox"));
			List<Map<String, String>> sellers = new DBReader().read();
			new AutoSeller().autoSell(Config.getInstance().getProp("url"), sellers);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
