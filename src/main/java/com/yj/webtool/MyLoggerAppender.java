package com.yj.webtool;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Priority;

public class MyLoggerAppender extends DailyRollingFileAppender {

	@Override
	public boolean isAsSevereAsThreshold(Priority priority) {
		// ֻ�ж��Ƿ���ȣ������ж����ȼ�
		return this.getThreshold().equals(priority);
	}
}
