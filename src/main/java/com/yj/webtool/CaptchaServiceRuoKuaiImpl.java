package com.yj.webtool;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class CaptchaServiceRuoKuaiImpl implements CaptchaService {

	public String solve(BufferedImage img) throws Exception {
		String username = Config.getInstance().getProp("ruokuai.username");
		String password = Config.getInstance().getProp("ruokuai.password");
		String softId = Config.getInstance().getProp("ruokuai.softId");
		String softKey = Config.getInstance().getProp("ruokuai.softKey");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, "png", baos);
		String result = RuoKuai
				.createByPost(username, password, "1040", "90", softId,
						softKey, baos.toByteArray());
		System.out.println(result);
		SAXReader reader = new SAXReader();
		Document document = reader.read(new ByteArrayInputStream(result.getBytes()));
		Element root = document.getRootElement();
		result = root.element("Result").getText();
		return result;
	}

}
