package com.covenant.tribe.service;

import org.springframework.stereotype.Service;

@Service
public interface VerificationCodeService {
    int getVerificationCode(int minValue, int maxValue);
}
