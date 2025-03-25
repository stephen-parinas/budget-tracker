package com.stephen_parinas.budget_tracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Configuration class for setting up email properties using Spring Boot.
 * <p>This class loads SMTP settings from the application properties and
 * configures a {@link JavaMailSender} bean for sending emails.</p>
 */
@Configuration
public class EmailConfiguration {
    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String emailUsername;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.smtp.auth}")
    private boolean smtpAuth;

    @Value("${spring.mail.smtp.starttls.enable}")
    private boolean smtpStarttlsEnable;

    /**
     * Configures and provides a {@link JavaMailSender} bean for sending emails.
     *
     * @return a configured instance of {@link JavaMailSender}
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(emailUsername);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.starttls.enable", smtpStarttlsEnable);
        props.put("mail.debug", true);

        return mailSender;
    }
}
