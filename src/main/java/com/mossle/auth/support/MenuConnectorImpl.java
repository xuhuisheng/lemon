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
    private MenuCache menuCache;

    /**
     * systemCode算子系统的标识，比如个人事务子系统，账号子系统.
     */
    public List<MenuDTO> findMenus(String systemCode, String userId) {
        String tenantId = tenantHolder.getTenantId();
        List<MenuDTO> menuDtos = this.menuCache.findByCode(systemCode);

        if (menuDtos == null) {
            String hql = "from Menu where menu.display='true' and menu.type='system' and menu.code=? order by priority";
            List<Menu> menus = menuManager.find(hql, systemCode);
            menuDtos = this.convertMenuDtos(menus, false);
            this.menuCache.updateByCode(systemCode, menuDtos);
        }

        List<String> permissions = userAuthConnector.findById(userId, tenantId)
                .getPermissions();

        return this.filterMenuDtos(menuDtos, permissions, false);
    }

    /**
     * 获得所有子系统的入口，比如账号子系统.
     */
    public List<MenuDTO> findSystemMenus(String userId) {
        String tenantId = tenantHolder.getTenantId();
        List<MenuDTO> menuDtos = this.menuCache.findEntries();

        if (menuDtos == null) {
            List<Menu> menus = menuManager
                    .find("from Menu where type='entry' and display='true'");
            menuDtos = this.convertMenuDtos(menus, true);
            this.menuCache.updateEntries(menuDtos);
        }

        List<String> permissions = userAuthConnector.findById(userId, tenantId)
                .getPermissions();

        return this.filterMenuDtos(menuDtos, permissions, true);
    }

    /**
     * 将menu转换为menuDto.
     */
    public List<MenuDTO> convertMenuDtos(List<Menu> menus, boolean excludeModule) {
        List<MenuDTO> menuDtos = new ArrayList<MenuDTO>();

        for (Menu menu : menus) {
            if (!("true".equals(menu.getDisplay()))) {
                continue;
            }

            if (excludeModule && "module".equals(menu.getType())) {
                continue;
            }

            MenuDTO menuDto = this.convertMenuDto(menu, excludeModule);
            menuDtos.add(menuDto);
        }

        return menuDtos;
    }

    /**
     * 把menu数据复制给dto.
     */
    public MenuDTO convertMenuDto(Menu menu, boolean excludeModule) {
        MenuDTO menuDto = new MenuDTO();
        menuDto.setCode(menu.getCode());
        menuDto.setTitle(menu.getTitle());
        // 为了jsp里使用方便，要去掉url前面的/
        menuDto.setUrl(this.processUrl(menu.getUrl()));
        menuDto.setPermission(menu.getPerm().getCode());
        menuDto.setType(menu.getType());

        List<Menu> menus = menuManager.find(
                "from Menu where display='true' and menu=? order by priority",
                menu);
        List<MenuDTO> menuDtos = this.convertMenuDtos(menus, excludeModule);
        menuDto.setChildren(menuDtos);

        return menuDto;
    }

    /**
     * 按个人权限过滤菜单.
     */
    public List<MenuDTO> filterMenuDtos(List<MenuDTO> menuDtos,
            List<String> permissions, boolean excludeModule) {
        List<MenuDTO> result = new ArrayList<MenuDTO>();

        for (MenuDTO menuDto : menuDtos) {
            if (excludeModule && "module".equals(menuDto.getType())) {
                continue;
            }

            if ((!permissions.contains("*"))
                    && (!permissions.contains(menuDto.getPermission()))) {
                logger.debug("permissions : {}", permissions);
                logger.debug("skip : {}", menuDto.getPermission());

                continue;
            }

            MenuDTO item = this.filterMenuDto(menuDto, permissions,
                    excludeModule);
            result.add(item);
        }

        return result;
    }

    /**
     * 把menu数据复制给dto.
     */
    public MenuDTO filterMenuDto(MenuDTO menuDto, List<String> permissions,
            boolean excludeModule) {
        MenuDTO item = new MenuDTO();
        item.setCode(menuDto.getCode());
        item.setTitle(menuDto.getTitle());
        // 为了jsp里使用方便，要去掉url前面的/
        item.setUrl(this.processUrl(menuDto.getUrl()));

        List<MenuDTO> children = this.filterMenuDtos(menuDto.getChildren(),
                permissions, excludeModule);
        item.setChildren(children);

        return item;
    }

    /**
     * 如果url以/开头，要去掉/，这样前端jsp渲染的时候就方便多了.
     */
    public String processUrl(String url) {
        if (url == null) {
            return "";
        }

        if (url.length() == 0) {
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

    @Resource
    public void setMenuCache(MenuCache menuCache) {
        this.menuCache = menuCache;
    }
}
