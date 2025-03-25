package com.stephen_parinas.budget_tracker.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * A service class for handling email sending operations.
 */
@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends a verification email to the specified recipient.
     *
     * @param to      The recipient's email address.
     * @param subject The subject of the email.
     * @param body    The body content of the email.
     * @throws MessagingException If there is an issue creating or sending the email.
     */
    public void sendVerificationEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);
    }
}
