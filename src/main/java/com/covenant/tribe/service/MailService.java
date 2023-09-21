package com.covenant.tribe.service;

import org.springframework.stereotype.Service;

@Service
public interface MailService {
    void sendEmail(String subject, String message, String email);
}
