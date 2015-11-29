package com.yj.webtool.webdriver;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class SubmitThread extends Thread{
	BlockingQueue<Map<String, String>> queue = null;
	String url = null;
	public SubmitThread(BlockingQueue queue, String url) {
		this.queue = queue;
		this.url = url;
	}
	public void run() {
		AutoSeller autoSeller = new AutoSeller();
		while(true) {
			try {
				Map<String, String> sellerInfo = queue.take();
				autoSeller.autoSell(url, sellerInfo);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
