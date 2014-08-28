package com.mossle.core.util;

import java.security.MessageDigest;

public class Md5Utils {
    public static String getMd5(String text) {
        try {
            char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                    '9', 'a', 'b', 'c', 'd', 'e', 'f' };
            MessageDigest md = MessageDigest.getInstance("md5");
            md.update(text.getBytes("UTF-8"));

            byte[] bytes = md.digest();
            int j = bytes.length;
            char[] c = new char[j * 2];
            int k = 0;

            for (int i = 0; i < j; i++) {
                byte b = bytes[i];
                c[k++] = hexDigits[b >>> 4 & 0xf];
                c[k++] = hexDigits[b & 0xf];
            }

            return new String(c);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
