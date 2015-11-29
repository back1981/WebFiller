package com.yj.webtool;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.yj.webtool.webdriver.CaptchaServiceRuoKuaiImpl;
import com.yj.webtool.webdriver.Config;

public class CaptchaServiceRuoKuaiImplTest {

	@Test
	public void testSolve() {
		Config.getInstance().init("F:\\studio\\webtool_workspace\\webtool\\src\\test\\resources\\config.properties");
		BufferedImage image;
		try {
			image = ImageIO.read(new File("C:\\Users\\yangjie\\Desktop\\a.jpg"));
			String result = new CaptchaServiceRuoKuaiImpl().solve(image);
			System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
