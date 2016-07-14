package com.mossle.core.mail;

import java.util.HashMap;
import java.util.Map;

public class MemoryMailServerInfoCache implements MailServerInfoCache {
    private MailServerInfo defaultMailServerInfo;
    private Map<String, MailServerInfo> mailServerInfoMap = new HashMap<String, MailServerInfo>();

    public MailServerInfo getDefaultMailServerInfo() {
        return defaultMailServerInfo;
    }

    public void setDefaultMailServerInfo(MailServerInfo defaultMailServerInfo) {
        this.defaultMailServerInfo = defaultMailServerInfo;
        this.setMailServerInfo(defaultMailServerInfo);
    }

    public MailServerInfo getMailServerInfo(String name) {
        return mailServerInfoMap.get(name);
    }

    public void setMailServerInfo(MailServerInfo mailServerInfo) {
        mailServerInfoMap.put(mailServerInfo.getName(), mailServerInfo);
    }
}
