package com.covenant.tribe.service.impl;

import com.covenant.tribe.service.MailService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class MailServiceImpl implements MailService {
    JavaMailSender mailSender;
    private final String FROM_EMAIL = "tribe@tribual.ru";
    @Override
    public void sendEmail(String subject, String message, String email) {
            SimpleMailMessage mailMsg = new SimpleMailMessage();
            mailMsg.setFrom(FROM_EMAIL);
            mailMsg.setTo(email);
            mailMsg.setSubject(subject);
            mailMsg.setText(message);
            mailSender.send(mailMsg);
    }
}
