package com.mossle.core.util;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.ldap.InitialLdapContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapHandler {
    private static Logger logger = LoggerFactory.getLogger(LdapHandler.class);
    private String ip;
    private int port = 389;
    private String domainName = "mossle.com";

    public boolean check(String username, String password) {
        DirContext dirContext = null;

        String bindDn = username + "@" + domainName;
        logger.info("ad start : {}", bindDn);

        Hashtable<String, String> env = new Hashtable<String, String>();
        String url = "ldap://" + ip + ":" + port;
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, bindDn);
        env.put(Context.SECURITY_CREDENTIALS, password);
        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, url);

        try {
            dirContext = new InitialLdapContext(env, null);
            logger.info("dirContext : {}", dirContext);

            return true;
        } catch (final Exception e) {
            logger.info("Failed to authenticate user {} with error {}",
                    username, e.getMessage());

            return false;
        } finally {
            if (dirContext != null) {
                try {
                    dirContext.close();
                } catch (NamingException ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        }
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }
}
