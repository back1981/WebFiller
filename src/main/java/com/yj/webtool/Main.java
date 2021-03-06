package com.yj.webtool;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.yj.webtool.highperf.AutoSellerHighPerfImpl;
import com.yj.webtool.mail.MailTimerTask;
import com.yj.webtool.webdriver.AutoSeller;
import com.yj.webtool.webdriver.Config;
import com.yj.webtool.webdriver.DBReader;
import com.yj.webtool.webdriver.SubmitThread;

public class Main {
	public static void startMailTask() {
		Calendar cal = Calendar.getInstance();
		// cal.add(Calendar.HOUR, 1);
		long mailInterval = Long.parseLong(Config.getInstance().getProp(
				"mailInterval")) * 1000;
		new Timer().schedule(new MailTimerTask(), cal.getTime(), mailInterval);
	}

	public static void main(String[] args) {
		Logger logger = Logger.getLogger(Main.class);
		try {
			if (args.length < 1) {
				System.out
						.println("Usage:java -jar webtool.jar c:/config.properties");
				return;
			}
			String configFilePath = args[0];
			Config.getInstance().init(configFilePath);
			Main.startMailTask();

			// Config.getInstance().init("F:\\studio\\webtool_workspace\\webtool\\src\\test\\resources\\config.properties");
			System.setProperty("webdriver.firefox.bin", Config.getInstance()
					.getProp("firefox"));
			 System.setProperty("webdriver.chrome.driver",
			 Config.getInstance().getProp("chrome"));
			// System.setProperty("webdriver.ie.driver",
			// Config.getInstance().getProp("ie"));
			long interval = Long.parseLong(Config.getInstance().getProp(
					"submitInterval"));
			String url = Config.getInstance().getProp("url");
			boolean openBrowser = Boolean.valueOf(Config.getInstance().getProp(
					"open_browser"));
			
			BlockingQueue<Map<String, String>> queue = null;
//			if(openBrowser) {
//				queue = createThread(url);
//			}
			while (true) {
				try {
					long start = System.currentTimeMillis();
					List<Map<String, String>> sellers = new DBReader().read();
					logger.info("records num:" + sellers.size());
					if (sellers.size() > 0) {
						if (openBrowser) {
//							 queue.addAll(sellers);
							 
							new AutoSeller().autoSell(url, sellers);
						} else {
							new AutoSellerHighPerfImpl().autoSell(url, sellers);
						}
					}
					long end = System.currentTimeMillis();
					long sleep = interval * 1000 - (end - start);
					if (sleep > 0) {
						Thread.sleep(sleep);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static BlockingQueue<Map<String, String>> createThread(String url) {
		BlockingQueue<Map<String, String>> queue = new LinkedBlockingQueue<Map<String, String>>();
		int threadCount = Integer.parseInt(Config.getInstance().getProp(
				"thread"));
		for (int i = 0; i < threadCount; i++) {
			new SubmitThread(queue, url).start();
		}
		return queue;
	}
}
