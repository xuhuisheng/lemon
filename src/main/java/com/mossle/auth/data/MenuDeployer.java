package com.mossle.auth.data;

import java.io.IOException;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.auth.persistence.domain.Menu;
import com.mossle.auth.persistence.manager.MenuManager;
import com.mossle.auth.persistence.manager.PermManager;

import com.mossle.core.mapper.JsonMapper;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MenuDeployer {
    private static Logger logger = LoggerFactory.getLogger(MenuDeployer.class);
    private MenuManager menuManager;
    private PermManager permManager;
    private boolean enable;
    private JsonMapper jsonMapper = new JsonMapper();
    private String menuFilePath = "data/auth-menu.json";
    private String menuEncoding = "UTF-8";

    @PostConstruct
    public void init() {
        if (!enable) {
            logger.info("skip");

            return;
        }

        try {
            String json = IOUtils.toString(MenuDeployer.class.getClassLoader()
                    .getResourceAsStream(menuFilePath), menuEncoding);
            List<Map<String, Object>> list = jsonMapper.fromJson(json,
                    List.class);

            this.processMenus(list, null);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void processMenus(List<Map<String, Object>> list, String parentCode) {
        for (Map<String, Object> map : list) {
            this.processMenu(map, parentCode);
        }
    }

    public void processMenu(Map<String, Object> map, String parentCode) {
        String code = (String) map.get("code");
        Menu menu = this.menuManager.findUniqueBy("code", code);

        if (menu == null) {
            menu = new Menu();
            menu.setCode(code);
            menu.setTitle((String) map.get("name"));
            menu.setUrl((String) map.get("url"));
            menu.setType((String) map.get("type"));
            menu.setDisplay((String) map.get("display"));
            menu.setPriority((Integer) map.get("priority"));
            menu.setPerm(this.permManager.findUniqueBy("code",
                    (String) map.get("perm")));
            menu.setMenu(this.menuManager.findUniqueBy("code", parentCode));
            this.menuManager.save(menu);
            logger.debug("menu : {}", code);
        }

        List<Map<String, Object>> children = (List<Map<String, Object>>) map
                .get("children");

        if (children != null) {
            this.processMenus(children, code);
        }
    }

    @Resource
    public void setMenuManager(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    @Resource
    public void setPermManager(PermManager permManager) {
        this.permManager = permManager;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
