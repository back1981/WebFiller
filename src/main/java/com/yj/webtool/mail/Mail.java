package com.yj.webtool.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.yj.webtool.webdriver.Config;

public class Mail {

	public static void send(String msg) {

		Properties props = new Properties();
		// props.put("mail.smtp.auth", "true");
		// props.put("mail.smtp.starttls.enable", "true");
		// props.put("mail.smtp.host", "smtp.gmail.com");
		// props.put("mail.smtp.port", "587");
		props.put("mail.smtp.host", "smtp.126.com");
		props.put("mail.smtp.port", "25");
		props.put("mail.smtp.auth", "true");

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						String username = Config.getInstance().getProp(
								"mail.from");
						String password = Config.getInstance().getProp(
								"mail.password");
						return new PasswordAuthentication(username, password);
					}
				});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(Config.getInstance().getProp(
					"mail.from")));
			message.setRecipients(
					Message.RecipientType.TO,
					InternetAddress.parse(Config.getInstance().getProp(
							"mail.to")));
			message.setSubject("[AutoSeller]自动提交统计");
			message.setText(msg);

			Transport.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}

	}
	
	public static void main(String[] args) {
		String configFilePath = "D:\\studio\\projects\\WebFiller\\src\\test\\resources\\config.properties";
		Config.getInstance().init(configFilePath);
		Mail.send("aaaaaaaaaaaaas");
	}
}