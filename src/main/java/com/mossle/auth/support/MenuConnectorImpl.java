package com.mossle.auth.support;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.menu.MenuConnector;
import com.mossle.api.menu.MenuDTO;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.userauth.UserAuthConnector;

import com.mossle.auth.persistence.domain.Menu;
import com.mossle.auth.persistence.manager.MenuManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MenuConnectorImpl implements MenuConnector {
    private static Logger logger = LoggerFactory
            .getLogger(MenuConnectorImpl.class);
    private MenuManager menuManager;
    private UserAuthConnector userAuthConnector;
    private TenantHolder tenantHolder;

    public List<MenuDTO> findMenus(String systemCode, String userId) {
        String tenantId = tenantHolder.getTenantId();
        String hql = "from Menu where menu.type='system' and menu.code=? order by priority";
        List<Menu> menus = menuManager.find(hql, systemCode);
        List<String> permissions = userAuthConnector.findById(userId, tenantId)
                .getPermissions();

        return this.convertMenuDtos(menus, permissions, false);
    }

    public List<MenuDTO> findSystemMenus(String userId) {
        String tenantId = tenantHolder.getTenantId();
        List<Menu> menus = menuManager.find("from Menu where type='entry'");

        List<String> permissions = userAuthConnector.findById(userId, tenantId)
                .getPermissions();

        return this.convertMenuDtos(menus, permissions, true);
    }

    public List<MenuDTO> convertMenuDtos(List<Menu> menus,
            List<String> permissions, boolean excludeModule) {
        List<MenuDTO> menuDtos = new ArrayList<MenuDTO>();

        for (Menu menu : menus) {
            if (excludeModule && "module".equals(menu.getType())) {
                continue;
            }

            if ((!permissions.contains("*"))
                    && (!permissions.contains(menu.getPerm().getCode()))) {
                logger.debug("permissions : {}", permissions);
                logger.debug("skip : {}", menu.getPerm().getCode());

                continue;
            }

            MenuDTO menuDto = this.convertMenuDto(menu, permissions,
                    excludeModule);
            menuDtos.add(menuDto);
        }

        return menuDtos;
    }

    public MenuDTO convertMenuDto(Menu menu, List<String> permissions,
            boolean excludeModule) {
        MenuDTO menuDto = new MenuDTO();
        menuDto.setCode(menu.getCode());
        menuDto.setTitle(menu.getTitle());
        menuDto.setUrl(menu.getUrl());

        List<Menu> menus = menuManager.find(
                "from Menu where menu=? order by priority", menu);
        List<MenuDTO> menuDtos = this.convertMenuDtos(menus, permissions,
                excludeModule);
        menuDto.setChildren(menuDtos);

        return menuDto;
    }

    @Resource
    public void setMenuManager(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    @Resource
    public void setUserAuthConnector(UserAuthConnector userAuthConnector) {
        this.userAuthConnector = userAuthConnector;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
