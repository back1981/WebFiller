package com.yj.webtool.webdriver;

import java.awt.image.BufferedImage;

public interface CaptchaService {
	public String solve(BufferedImage img) throws Exception;
}
