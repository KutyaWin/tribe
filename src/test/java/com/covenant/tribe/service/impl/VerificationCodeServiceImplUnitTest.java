package com.covenant.tribe.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class VerificationCodeServiceImplUnitTest {

    @InjectMocks
    VerificationCodeServiceImpl verificationCodeService;


    @Test
    void getVerificationCode() {
        int minValue = 1000;
        int maxValue = 9999;

        int result = verificationCodeService.getVerificationCode(minValue, maxValue);

        assertTrue( result >= minValue && result < maxValue,
                "Returned verification code is not in the expected range");


    }

}
