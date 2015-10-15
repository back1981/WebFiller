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
		// 如果你的 FireFox 没有安装在默认目录，那么必须在程序中设置
		// System.setProperty("webdriver.firefox.bin",
		// "D:\\Program Files\\Mozilla Firefox\\firefox.exe");
		// 创建一个 FireFox 的浏览器实例
		
		// 让浏览器访问 Baidu
		driver.get(url);
		// 用下面代码也可以实现
		// driver.navigate().to("http://www.baidu.com");

		// 获取 网页的 title
		System.out.println("1 Page title is: " + driver.getTitle());

		// 通过 id 找到 input 的 DOM
		// WebElement elVehicleType = driver.findElement(By.id("vehicleType"));

		// 输入关键字
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

		// 提交 input 所在的 form
		elBrand.submit();

		// 通过判断 title 内容等待搜索页面加载完毕，间隔10秒
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
		// 显示搜索结果页面的 title
		System.out.println("2 Page title is: " + driver.getTitle());

		// 关闭浏览器
		// driver.quit();
	}
}
