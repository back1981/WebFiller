package com.yj.webtool.webdriver;

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
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.yj.webtool.IAutoSeller;

public class AutoSeller implements IAutoSeller{
	Logger logger = Logger.getLogger(AutoSeller.class);
	private CaptchaService captchaService = new CaptchaServiceRuoKuaiImpl();
	
	WebDriver driver = null;

	// WebDriver driver = new HtmlUnitDriver();

	public AutoSeller() {
		long start = System.currentTimeMillis();
		driver = new FirefoxDriver();
//		driver = new InternetExplorerDriver();
		logger.info("open firefox cost time:" + (System.currentTimeMillis() - start));
	}

	public CaptchaService getCaptchaService() {
		return captchaService;
	}

	public void setCaptchaService(CaptchaService captchaService) {
		this.captchaService = captchaService;
	}

	public BufferedImage getAutoCodeImg(WebDriver driver) {
		try {
			if (!driver.findElement(By.id("checkCodeUl")).isDisplayed()) {
				return null;
			}

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
			// e.printStackTrace();
			logger.error("get captcha image failed", e);

		}
		return null;

	}

	public void autoSell(String url, List<Map<String, String>> sellerList)
			throws Exception {
		try {
			if (sellerList == null || sellerList.size() == 0) {
				return;
			}
			
			List<Map<String, String>> retryList = new LinkedList<Map<String, String>>();
			for (Map<String, String> sellerInfo : sellerList) {
				try {
					autoSell(url, sellerInfo);
				} catch (CaptcharSolveException e) {
					retryList.add(sellerInfo);
				}
			}
			autoSell(url, retryList);
		} finally {
			// 关闭浏览器
			driver.quit();
		}
	}

	public boolean autoSell(String url, Map<String, String> sellerInfo)
			throws Exception {

		// 如果你的 FireFox 没有安装在默认目录，那么必须在程序中设置
		// System.setProperty("webdriver.firefox.bin",
		// "D:\\Program Files\\Mozilla Firefox\\firefox.exe");
		// 创建一个 FireFox 的浏览器实例

		// 让浏览器访问 Baidu

		long start = System.currentTimeMillis();
		driver.get(url);
		logger.info("open url cost time:" + (System.currentTimeMillis() - start));

//		JavascriptExecutor js = (JavascriptExecutor) driver;
//		js.executeScript("return window.stop");
//		logger.info("stop window cost time:" + (System.currentTimeMillis() - start));
//		waitSubmitButton();
		
		// 用下面代码也可以实现
		// driver.navigate().to("http://www.baidu.com");
		closeLayer(driver);
		// 获取 网页的 title
		// System.out.println("1 Page title is: " + driver.getTitle());

		// 通过 id 找到 input 的 DOM
		// WebElement elVehicleType = driver.findElement(By.id("vehicleType"));

		// 输入关键字
		// elVehicleType.sendKeys(values.get("vehicleType"));

		WebElement elName = driver.findElement(By.id("name"));
		elName.clear();
		elName.sendKeys(sellerInfo.get("name"));

		WebElement elPhone = driver.findElement(By.id("cel_num"));
		elPhone.clear();
		elPhone.sendKeys(sellerInfo.get("cel_num"));

		selectCity(sellerInfo);

		WebElement elBrand = driver.findElement(By.id("brand"));
		elBrand.clear();
		elBrand.sendKeys(sellerInfo.get("brand"));

		selectTime();
		BufferedImage img = getAutoCodeImg(driver);
		if (img != null) {
			try {
				ImageIO.write(img, "png", new File(Config.getInstance()
						.getProp("captcha_snapshot_dir")
						+ File.separator
						+ "auth.png"));
				String captcha = captchaService.solve(img);

				WebElement elAuth = driver.findElement(By.id("auth"));
				elAuth.clear();
				elAuth.sendKeys(captcha);
			} catch (CaptcharSolveException e) {
				logger.error("solve captcha failed", e);
				throw e;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("", e);
			}

		}

		// 提交 input 所在的 form
		// elBrand.submit();
		driver.findElement(By.id("saveOnline")).click();


//		try {
//			Thread.sleep(Integer.parseInt(Config.getInstance().getProp(
//					"waittime")));
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

		
//		String alertMsg = handleModelDialog(driver);
//		boolean result = driver.getCurrentUrl()
//				.endsWith("sellcar/express.html");
		String strResult = checkResult();
		boolean result = false;
		String alertMsg = null;
		if("true".equalsIgnoreCase(strResult)) {
			result = true;
			driver.navigate().back();
		} else {
			alertMsg = strResult;
		}
		logger.info(sellerInfo + ", result=" + result + ", msg=" + alertMsg);
		SubmitStatus status = null;
		if (result) {
			status = SubmitStatus.SUCC;
		} else {
			status = SubmitStatus.FAILED;
			if (alertMsg.contains("验证码错误")) {
				status = SubmitStatus.CHECK_CODE_ERR;
			}
		}
		new DBReader().updateStatus(status, sellerInfo.get("cel_num"));
		long end = System.currentTimeMillis();
		logger.info("submit a request coste cost time:" + (end - start));
		return result;

	}

	private String checkResult() {
		String alertMsg = null;
		try {
			Thread.sleep(500);
			alertMsg = driver.switchTo().alert().getText();
			driver.switchTo().alert().accept();
		} catch (Exception e) {
			boolean result = driver.getCurrentUrl()
					.endsWith("sellcar/express.html");
			if(result) {
				return String.valueOf(result);
			} else {
				checkResult();
			}
		}
		return alertMsg;
	}
	
	
	private void selectCity(Map<String, String> sellerInfo) {
		WebElement elSelCity = driver.findElement(By.id("selectCity"));
		// driver.findElement(By.id("city")).sendKeys(values.get("city"));
		WebElement elCity2 = driver.findElement(By.id("city"));
		try {
			elCity2.clear();
		} catch(Exception e) {
			
		}
		elCity2.click();
		
		JavascriptExecutor js = (JavascriptExecutor) driver;

		List<WebElement> elCityList = elSelCity.findElements(By.tagName("a"));
		boolean cityMatched = false;
		for (WebElement elCity : elCityList) {
			String city = elCity.getAttribute("innerHTML");
			if (city.equals(sellerInfo.get("city"))) {
				logger.debug("city=" + city + ",clickid="
						+ elCity.getAttribute("id"));
				cityMatched = true;
				js.executeScript("document.getElementById('"
						+ elCity.getAttribute("id") + "').click()");
				break;
			}
		}
		if (!cityMatched) {
			driver.findElement(By.id("city")).click();
		}
	}

	private void waitSubmitButton() {
		WebElement myDynamicElement = (new WebDriverWait(driver, 10))
				.until(new ExpectedCondition<WebElement>() {
					@Override
					public WebElement apply(WebDriver d) {
						logger.info("start find svaeOnline");
						long start = System.currentTimeMillis();
						WebElement el = d.findElement(By.id("saveOnline"));
						logger.info("end find svaeOnline "
								+ (System.currentTimeMillis() - start));
						return el;
					}
				});
	}

	private void selectTime() {
		driver.findElement(By.id("time")).click();
		List<WebElement> elSelectTime = driver.findElement(By.id("selectTime"))
				.findElement(By.tagName("ul")).findElements(By.tagName("li"));
		for (WebElement el : elSelectTime) {
			String time = el.findElement(By.tagName("a")).getText();

			if ("一周内".equals(time)) {
				el.click();
				break;
			}
		}
	}

	private String handleModelDialog(WebDriver driver) {
		String alertMsg = null;
		try {
			alertMsg = driver.switchTo().alert().getText();
			driver.switchTo().alert().accept();
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return alertMsg;

	}

	/**
	 * close the layer which will overlap the captcha
	 */
	private void closeLayer(WebDriver driver) throws Exception {
		try {
//			Thread.sleep(20000);
			long start = System.currentTimeMillis();
//			WebElement el = driver.findElement(By.cssSelector("#close1"));
			WebElement el = driver.findElement(By.id("close1"));
//			driver.findElements(By.id("close1"));
			long time1 = System.currentTimeMillis();
			logger.info("close layer time cost:" + (time1 - start));
//			WebElement el = driver.findElement(By.id("close1"));
			


			el = el.findElement(By.tagName("area"));
//			long time2 = System.currentTimeMillis();
//			logger.info("close layer time cost:" + (time2 - time1));
			el.click();
//			logger.info("close layer time cost:"
//					+ (System.currentTimeMillis() - time2));
		} catch (Exception e) {

		}
	}
}
