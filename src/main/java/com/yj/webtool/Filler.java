package com.yj.webtool;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Filler {
	private CaptchaService captchaService = new CaptchaServiceRuoKuaiImpl();
	WebDriver driver = new FirefoxDriver();

	public Filler() {
		
	}
	public CaptchaService getCaptchaService() {
		return captchaService;
	}

	public void setCaptchaService(CaptchaService captchaService) {
		this.captchaService = captchaService;
	}

	public BufferedImage getAutoCodeImg(WebDriver driver) {
		try {
			byte[] arrScreen = ((TakesScreenshot) driver)
					.getScreenshotAs(OutputType.BYTES);
			BufferedImage imageScreen = ImageIO.read(new ByteArrayInputStream(
					arrScreen));
			WebElement cap = driver.findElement(By.id("checkCode"));

			Dimension capDimension = cap.getSize();
			Point capLocation = cap.getLocation();
			BufferedImage imgCap = imageScreen.getSubimage(capLocation.x,
					capLocation.y, capDimension.width, capDimension.height);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(imgCap, "png", os);
			return imgCap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public void fill(String url, Map<String, String> values) {
		// ������ FireFox û�а�װ��Ĭ��Ŀ¼����ô�����ڳ���������
		// System.setProperty("webdriver.firefox.bin",
		// "D:\\Program Files\\Mozilla Firefox\\firefox.exe");
		// ����һ�� FireFox �������ʵ��
		
		// ����������� Baidu
		driver.get(url);
		// ���������Ҳ����ʵ��
		// driver.navigate().to("http://www.baidu.com");

		// ��ȡ ��ҳ�� title
		System.out.println("1 Page title is: " + driver.getTitle());

		// ͨ�� id �ҵ� input �� DOM
		// WebElement elVehicleType = driver.findElement(By.id("vehicleType"));

		// ����ؼ���
		// elVehicleType.sendKeys(values.get("vehicleType"));

		WebElement elName = driver.findElement(By.id("name"));
		elName.sendKeys(values.get("name"));

		WebElement elPhone = driver.findElement(By.id("cel_num"));
		elPhone.sendKeys(values.get("cel_num"));

		WebElement elSelCity = driver.findElement(By.id("selectCity"));
//		driver.findElement(By.id("city")).sendKeys(values.get("city"));
		driver.findElement(By.id("city")).click();

		JavascriptExecutor js = (JavascriptExecutor) driver;

		List<WebElement> elCityList = elSelCity.findElements(By.tagName("a"));
		
		for (WebElement elCity : elCityList) {
			System.out.println("id" + elCity.getAttribute("id"));
			System.out.println("text:" + elCity.getText());
			System.out.println("text:" + elCity.getAttribute("innerHTML"));
			String city = elCity.getAttribute("innerHTML");
			if (city.equals(values.get("city"))) {
//				elCity.click();
				 js.executeScript("document.getElementById('" + elCity.getAttribute("id") +"').click()");
				break;
			}
		}

		WebElement elBrand = driver.findElement(By.id("brand"));
		elBrand.sendKeys(values.get("brand"));

		BufferedImage img = getAutoCodeImg(driver);
		if (img != null) {
			try {
				ImageIO.write(img, "png", new File("f:\\auth.png"));
				String captcha = captchaService.solve(img);
				WebElement elAuth = driver.findElement(By.id("auth"));
				elAuth.sendKeys(captcha);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// �ύ input ���ڵ� form
		elBrand.submit();

		// ͨ���ж� title ���ݵȴ�����ҳ�������ϣ����10��
//		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
//			public Boolean apply(WebDriver d) {
//				return d.getTitle().toLowerCase().endsWith("ztree");
//			}
//		});
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// ��ʾ�������ҳ��� title
		System.out.println("2 Page title is: " + driver.getTitle());

		// �ر������
		// driver.quit();
	}
}
