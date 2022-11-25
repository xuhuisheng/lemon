package com.mossle.user.authenticate;

import java.util.HashMap;
import java.util.Map;

public class CaptchaCache {
    // 30 seconds
    private long expireTime = 1000 * 30;
    private Map<String, Item> captchaMap = new HashMap<String, Item>();

    public String getCaptcha(String username) {
        Item item = captchaMap.get(username);

        if (item == null) {
            return null;
        }

        if ((System.currentTimeMillis() - item.updateTime) > expireTime) {
            captchaMap.remove(username);

            return null;
        }

        return item.code;
    }

    public void setCaptcha(String username, String code) {
        Item item = captchaMap.get(username);

        if (item == null) {
            item = new Item();
            captchaMap.put(username, item);
        }

        item.code = code;
        item.updateTime = System.currentTimeMillis();
    }

    public void clearCaptcha(String username) {
        captchaMap.remove(username);
    }

    public void checkCaptcha(String username, String captchaCode) {
        String code = this.getCaptcha(username);

        if (code == null) {
            // TODO: captcha invalid exception
            throw new RuntimeException("captcha invalid");
        }

        // clear captcha matches or not
        this.clearCaptcha(username);

        if (!code.equals(captchaCode)) {
            // TODO: captcha invalid exception
            throw new RuntimeException("captcha invalid");
        }
    }

    private class Item {
        String code;
        long updateTime;
    }
}
