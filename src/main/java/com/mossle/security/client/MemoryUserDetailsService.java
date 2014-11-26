package com.mossle.security.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.mossle.security.impl.SpringSecurityUserAuth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class MemoryUserDetailsService implements UserDetailsService {
    private static Logger logger = LoggerFactory
            .getLogger(MemoryUserDetailsService.class);
    private String text;
    private Map<String, SpringSecurityUserAuth> map = new HashMap<String, SpringSecurityUserAuth>();

    @PostConstruct
    public void init() {
        if (text == null) {
            logger.info("text not exists");

            return;
        }

        for (String line : text.split("\n")) {
            String[] array = line.split(",");
            String username = array[0];
            List<String> permissions = new ArrayList<String>(
                    Arrays.asList(array));
            permissions.remove(0);

            SpringSecurityUserAuth userAuth = new SpringSecurityUserAuth();
            userAuth.setId(username);
            userAuth.setUsername(username);
            userAuth.setDisplayName(username);
            userAuth.setPermissions(permissions);
            map.put(username, userAuth);
        }
    }

    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        if (!map.containsKey(username)) {
            throw new UsernameNotFoundException(username, null);
        }

        return map.get(username);
    }

    public void setText(String text) {
        this.text = text;
    }
}
