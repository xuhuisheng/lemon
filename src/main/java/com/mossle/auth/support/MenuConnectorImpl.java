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

/**
 * 用来通过用户权限来动态生成菜单.
 */
public class MenuConnectorImpl implements MenuConnector {
    private static Logger logger = LoggerFactory
            .getLogger(MenuConnectorImpl.class);
    private MenuManager menuManager;
    private UserAuthConnector userAuthConnector;
    private TenantHolder tenantHolder;

    /**
     * systemCode算子系统的标识，比如个人事务子系统，账号子系统.
     */
    public List<MenuDTO> findMenus(String systemCode, String userId) {
        String tenantId = tenantHolder.getTenantId();
        String hql = "from Menu where menu.type='system' and menu.code=? order by priority";
        List<Menu> menus = menuManager.find(hql, systemCode);
        List<String> permissions = userAuthConnector.findById(userId, tenantId)
                .getPermissions();

        return this.convertMenuDtos(menus, permissions, false);
    }

    /**
     * 获得所有子系统的入口，比如账号子系统.
     */
    public List<MenuDTO> findSystemMenus(String userId) {
        String tenantId = tenantHolder.getTenantId();
        List<Menu> menus = menuManager.find("from Menu where type='entry'");

        List<String> permissions = userAuthConnector.findById(userId, tenantId)
                .getPermissions();

        return this.convertMenuDtos(menus, permissions, true);
    }

    /**
     * 按个人权限过滤菜单.
     */
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

    /**
     * 把menu数据复制给dto.
     */
    public MenuDTO convertMenuDto(Menu menu, List<String> permissions,
            boolean excludeModule) {
        MenuDTO menuDto = new MenuDTO();
        menuDto.setCode(menu.getCode());
        menuDto.setTitle(menu.getTitle());
        // 为了jsp里使用方便，要去掉url前面的/
        menuDto.setUrl(this.processUrl(menu.getUrl()));

        List<Menu> menus = menuManager.find(
                "from Menu where menu=? order by priority", menu);
        List<MenuDTO> menuDtos = this.convertMenuDtos(menus, permissions,
                excludeModule);
        menuDto.setChildren(menuDtos);

        return menuDto;
    }

    /**
     * 如果url以/开头，要去掉/，这样前端jsp渲染的时候就方便多了.
     */
    public String processUrl(String url) {
        if (url == null) {
            return "";
        }

        if (url.charAt(0) != '/') {
            return url;
        }

        for (int i = 0; i < url.length(); i++) {
            if (url.charAt(i) != '/') {
                return url.substring(i);
            }
        }

        return "";
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
