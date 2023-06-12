package com.covenant.tribe.service.impl;

import com.covenant.tribe.service.VerificationCodeService;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {
    @Override
    public int getVerificationCode(int minValue, int maxValue) {
        return new Random().nextInt(maxValue - minValue) + minValue;
    }
}
