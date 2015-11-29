package com.yj.webtool;

import org.junit.Test;

public class DateTest {
	@Test
	public void test() {
		System.out.println(System.currentTimeMillis());
		System.out.println(System.currentTimeMillis() + String.valueOf((long)(Math.random() * 10000)));
	}
}
