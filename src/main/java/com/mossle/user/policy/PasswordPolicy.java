package com.mossle.user.policy;

import java.util.ArrayList;
import java.util.List;

public class PasswordPolicy {
    private String username;
    private String oldPassword;
    private List<String> keywords;

    public boolean validate(String password) {
        if ("".equals(username) || "".equals(password)) {
            return false;
        }

        if (!this.validatePassword(password)) {
            return false;
        }

        if (!this.validateUsername(password)) {
            return false;
        }

        if (!this.validateCharacterType(password)) {
            return false;
        }

        if (!this.validateKeywords(password)) {
            return false;
        }

        return true;
    }

    public boolean validatePassword(String password) {
        return !password.equals(oldPassword);
    }

    public boolean validateUsername(String password) {
        if (password.indexOf(username) != -1) {
            return false;
        }

        for (int i = 0, len = username.length() - 1; i < len; i++) {
            String item = username.substring(i, i + 2);

            if (password.indexOf(item) != -1) {
                return false;
            }
        }

        return true;
    }

    public boolean validateCharacterType(String password) {
        List<String> patterns = new ArrayList<String>();

        for (int i = 0, len = password.length(); i < len; i++) {
            char c = password.charAt(i);

            if (this.isNumber(c)) {
                if (patterns.contains("number")) {
                    patterns.add("number");
                }
            } else if (this.isUpperCase(c)) {
                if (patterns.contains("upper")) {
                    patterns.add("upper");
                }
            } else if (isLowerCase(c)) {
                if (patterns.contains("lower")) {
                    patterns.add("lower");
                }
            } else {
                if (patterns.contains("other")) {
                    patterns.add("other");
                }
            }

            if (patterns.size() > 2) {
                return true;
            }
        }

        return false;
    }

    public boolean validateKeywords(String password) {
        for (int i = 0, len = keywords.size(); i < len; i++) {
            String item = keywords.get(i).toLowerCase();

            if (password.indexOf(item) != -1) {
                return false;
            }
        }

        return true;
    }

    public boolean isNumber(char c) {
        return (c >= '0') && (c <= '9');
    }

    public boolean isUpperCase(char c) {
        return (c >= 'A') && (c <= 'Z');
    }

    public boolean isLowerCase(char c) {
        return (c >= 'a') && (c <= 'z');
    }

    public void setUsername(String username) {
        if (username == null) {
            this.username = "";
        } else {
            this.username = username.toLowerCase();
        }
    }

    public void setOldPassword(String oldPassword) {
        if (oldPassword == null) {
            this.oldPassword = "";
        } else {
            this.oldPassword = oldPassword.toLowerCase();
        }
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
