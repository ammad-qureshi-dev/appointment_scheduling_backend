/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.configs;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailSenderConfig {

	@Value("${EMAIL_USERNAME}")
	private String emailUsername;

	@Value("${EMAIL_PWD}")
	private String emailPwd;

	@Bean
	public SimpleMailMessage simpleMailMessage() {
		var message = new SimpleMailMessage();
		message.setFrom(emailUsername);
		return message;
	}

	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

		mailSender.setHost("smtp.gmail.com");
		mailSender.setPort(587);
		mailSender.setUsername(emailUsername);
		mailSender.setPassword(emailPwd);

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "false");

		return mailSender;
	}
}
