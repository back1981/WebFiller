package com.yj.webtool.webdriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Config {
	private static final Config  instance = new Config();
	
	
	Properties props = new Properties();
	
	public static Config getInstance() {
		return instance;
	}
	
	private Config() {
		
	}
	
	public void init(String filePath) {
		try {
			props.load(new FileInputStream(new File(filePath)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getProp(String key) {
		return props.getProperty(key);
	}
}
