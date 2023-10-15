package com.covenant.tribe.client.kudago.decoder;

import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class  KudagoDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        FeignException exception = feign.FeignException.errorStatus(methodKey, response);
        if(exception instanceof RetryableException){
            return exception;
        }
        if(response.status() >= 500 || response.status() == 403){
            log.error("We have exception in response with status: {} and message: {}", response.status(), response.body());
            return new RetryableException(response.status(),
                    exception.getMessage(),
                    response.request().httpMethod(),
                    exception,
                    null,
                    response.request());
        }
        return exception;
    }

}
