package com.yj.webtool.mail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.yj.webtool.highperf.AutoSellerHighPerfImpl;
  

public class MailTimerTask extends TimerTask{
	Logger logger = Logger.getLogger(MailTimerTask.class);
	private static long lastSucc = 0;
	private static long lastFailed = 0;
	private static long lastSendTime = 0;
	@Override
	public void run() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String from = sdf.format(new Date(lastSendTime));
		String now = sdf.format(new Date());
		
		logger.info("send mail, submit succ=" + AutoSellerHighPerfImpl.succ + ", failed=" + AutoSellerHighPerfImpl.failed);
		
		String msg = "从 [" + from + "] 到 [" + now + "] 提交统计数据：\n";
		msg += "提交成功:" + (AutoSellerHighPerfImpl.succ - lastSucc) + "次\n";
		msg += "提交失败:" + (AutoSellerHighPerfImpl.failed - lastFailed) + "次\n";
		lastSucc = AutoSellerHighPerfImpl.succ;
		lastFailed = AutoSellerHighPerfImpl.failed;
		lastSendTime = System.currentTimeMillis();
		Mail.send(msg);
		
	}

}
