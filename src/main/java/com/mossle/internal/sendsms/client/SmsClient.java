package com.mossle.internal.sendsms.client;

public interface SmsClient {
    void sendSms(String mobile, String message) throws Exception;
}
