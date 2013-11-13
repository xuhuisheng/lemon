package com.mossle.security.region;

public class PermissionDecorator {
    public String decorate(String permission, String region) {
        StringBuffer buff = new StringBuffer();
        String regionText = RegionConstants.SYSTEM_REGION;

        if (region != null) {
            regionText = region;
        }

        for (String want : permission.split(",")) {
            buff.append(regionText).append(":").append(want).append(",");
        }

        String text;

        if (permission.length() != 0) {
            buff.deleteCharAt(buff.length() - 1);
            text = buff.toString();
        } else {
            text = "";
        }

        return text;
    }
}
