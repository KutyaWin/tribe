package com.covenant.tribe.service.impl;

import com.covenant.tribe.service.VerificationCodeService;

import java.util.Random;

public class VerificationCodeServiceImpl implements VerificationCodeService {
    @Override
    public int getVerificationCode(int minValue, int maxValue) {
        return new Random().nextInt(maxValue - minValue) + minValue;
    }
}
