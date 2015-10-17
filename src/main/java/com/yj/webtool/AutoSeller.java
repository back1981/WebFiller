package com.yj.webtool;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class AutoSeller {
	Logger logger = Logger.getLogger(AutoSeller.class);
	private CaptchaService captchaService = new CaptchaServiceRuoKuaiImpl();
	WebDriver driver = new FirefoxDriver();

	public AutoSeller() {
		
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
//			e.printStackTrace();
			logger.error("get captcha image failed", e);
			
		}
		return null;

	}
	
	public void autoSell(String url, List<Map<String, String>> sellerList) throws Exception {
		List<Map<String, String>> retryList = new LinkedList<Map<String, String>>();
		for(Map<String, String> sellerInfo: sellerList) {
			try {
				autoSell(url, sellerInfo);
			} catch(CaptcharSolveException e) {
				retryList.add(sellerInfo);
			} 
		}
		autoSell(url, sellerList);
	}

	public boolean autoSell(String url, Map<String, String> sellerInfo) throws Exception {
		
		// ������ FireFox û�а�װ��Ĭ��Ŀ¼����ô�����ڳ���������
		// System.setProperty("webdriver.firefox.bin",
		// "D:\\Program Files\\Mozilla Firefox\\firefox.exe");
		// ����һ�� FireFox �������ʵ��
		
		// ����������� Baidu
		driver.get(url);
		// ���������Ҳ����ʵ��
		// driver.navigate().to("http://www.baidu.com");
		closeLayer(driver);
		// ��ȡ ��ҳ�� title
		System.out.println("1 Page title is: " + driver.getTitle());

		// ͨ�� id �ҵ� input �� DOM
		// WebElement elVehicleType = driver.findElement(By.id("vehicleType"));

		// ����ؼ���
		// elVehicleType.sendKeys(values.get("vehicleType"));

		WebElement elName = driver.findElement(By.id("name"));
		elName.sendKeys(sellerInfo.get("name"));

		WebElement elPhone = driver.findElement(By.id("cel_num"));
		elPhone.sendKeys(sellerInfo.get("cel_num"));

		WebElement elSelCity = driver.findElement(By.id("selectCity"));
//		driver.findElement(By.id("city")).sendKeys(values.get("city"));
		driver.findElement(By.id("city")).click();

		JavascriptExecutor js = (JavascriptExecutor) driver;

		List<WebElement> elCityList = elSelCity.findElements(By.tagName("a"));
		
		for (WebElement elCity : elCityList) {
//			System.out.println("id" + elCity.getAttribute("id"));
//			System.out.println("text:" + elCity.getText());
//			System.out.println("text:" + elCity.getAttribute("innerHTML"));
			String city = elCity.getAttribute("innerHTML");
			if (city.equals(sellerInfo.get("city"))) {
//				elCity.click();
				 js.executeScript("document.getElementById('" + elCity.getAttribute("id") +"').click()");
				break;
			}
		}

		WebElement elBrand = driver.findElement(By.id("brand"));
		elBrand.sendKeys(sellerInfo.get("brand"));

		BufferedImage img = getAutoCodeImg(driver);
		if (img != null) {
			try {
				ImageIO.write(img, "png", new File(Config.getInstance().getProp("captcha_snapshot_dir") + File.separator + "auth.png"));
				String captcha = captchaService.solve(img);
		
				WebElement elAuth = driver.findElement(By.id("auth"));
				elAuth.sendKeys(captcha);
			} catch(CaptcharSolveException e) {
				logger.error("solve captcha failed", e);
				throw e;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("", e);
			}

		}

		// �ύ input ���ڵ� form
//		elBrand.submit();
		driver.findElement(By.id("saveOnline")).click();

		// ͨ���ж� title ���ݵȴ�����ҳ�������ϣ����10��
//		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
//			public Boolean apply(WebDriver d) {
//				return d.getTitle().toLowerCase().endsWith("ztree");
//			}
//		});
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		handleModelDialog(driver);
		// ��ʾ�������ҳ��� title
		//System.out.println("2 Page title is: " + driver.getTitle());
		boolean result = driver.getCurrentUrl().endsWith("sellcar/express.html");
		logger.info(sellerInfo + ", result=" + result);
		return result;
		// �ر������
		// driver.quit();
	}
	
	private void handleModelDialog(WebDriver driver) {
		try {
			driver.switchTo().alert().accept();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * close the layer which will overlap the captcha
	 */
	private void closeLayer(WebDriver driver) throws Exception {
		driver.findElement(By.id("close1")).findElement(By.tagName("area")).click();
	}
}
