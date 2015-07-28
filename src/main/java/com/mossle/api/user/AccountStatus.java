package com.mossle.api.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountStatus {
    private static Logger logger = LoggerFactory.getLogger(AccountStatus.class);

    // status
    public static final String OTP_PASSWORD_TOO_SHORT = "otpPasswordTooShort";
    public static final String OTP_NOT_EXISTS = "otpNotExists";
    public static final String OTP_STATUS_INVALID = "otpStatusInvalid";
    public static final String OTP_CODE_FORMAT_INVALID = "otpCodeFormatInvalid";
    public static final String OTP_CODE_EXPIRED = "otpCodeExpired";
    public static final String OTP_CODE_INVALID = "otpCodeInvalid";
    public static final String BAD_CREDENTIALS = "badCredentials";
    public static final String BAD_HOURS = "badHours";
    public static final String BAD_WORKSTATION = "badWorkstation";
    public static final String ACCOUNT_NOT_EXISTS = "accountNotExists";
    public static final String PASSWORD_NOT_EXISTS = "passwordNotExists";
    public static final String ACCOUNT_LOCKED = "accountLocked";
    public static final String ACCOUNT_EXPIRED = "accountExpired";
    public static final String ACCOUNT_DISABLED = "accountDisabled";
    public static final String PASSWORD_EXPIRED = "passwordExpired";
    public static final String PASSWORD_MUST_CHANGE = "passwordMustChange";
    public static final String ENABLED = "enabled";
    public static final String LOCKED = "locked";

    // result
    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";

    // success reason
    public static final String OTP = "otp";
    public static final String NORMAL = "normal";
    public static final String SPECIAL = "special";

    public static String convertLdapException(String exceptionMessage) {
        if (exceptionMessage.indexOf("data 525") != -1) {
            return AccountStatus.ACCOUNT_NOT_EXISTS;
        } else if (exceptionMessage.indexOf("data 52e") != -1) {
            logger.info(exceptionMessage);

            return AccountStatus.BAD_CREDENTIALS;
        } else if (exceptionMessage.indexOf("data 530") != -1) {
            return AccountStatus.BAD_HOURS;
        } else if (exceptionMessage.indexOf("data 531") != -1) {
            return AccountStatus.BAD_WORKSTATION;
        } else if (exceptionMessage.indexOf("data 532") != -1) {
            return AccountStatus.PASSWORD_EXPIRED;
        } else if (exceptionMessage.indexOf("data 533") != -1) {
            return AccountStatus.ACCOUNT_DISABLED;
        } else if (exceptionMessage.indexOf("data 701") != -1) {
            return AccountStatus.ACCOUNT_EXPIRED;
        } else if (exceptionMessage.indexOf("data 733") != -1) {
            return AccountStatus.PASSWORD_MUST_CHANGE;
        } else if (exceptionMessage.indexOf("data 775") != -1) {
            return AccountStatus.ACCOUNT_LOCKED;
        } else {
            return exceptionMessage;
        }
    }
}
