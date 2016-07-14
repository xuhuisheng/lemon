package com.mossle.internal.sendsms.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockSmsConnector implements SmsConnector {
    private static Logger logger = LoggerFactory
            .getLogger(MockSmsConnector.class);

    public SmsDTO send(SmsDTO smsDto, SmsServerInfo smsServerInfo) {
        logger.info("mobile : {}", smsDto.getMobile());
        logger.info("message : {}", smsDto.getMessage());

        return smsDto;
    }
}
