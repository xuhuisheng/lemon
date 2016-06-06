package com.mossle.internal.sendsms.support;

public interface SmsConnector {
    SmsDTO send(SmsDTO smsDto, SmsServerInfo smsServerInfo);
}
