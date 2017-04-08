package com.mossle.auth.web.dev;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.auth.persistence.domain.Menu;
import com.mossle.auth.persistence.domain.Perm;
import com.mossle.auth.persistence.manager.MenuManager;
import com.mossle.auth.persistence.manager.PermManager;
import com.mossle.auth.support.MenuCache;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("com.mossle.auth.web.dev.MenuController")
@RequestMapping("auth/dev")
public class MenuController {
    private MenuManager menuManager;
    private PermManager permManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MenuCache menuCache;
    private JsonMapper jsonMapper = new JsonMapper();

    @RequestMapping("menu-export")
    public String export(Model model) throws Exception {
        String hql = "from Menu where menu=null order by priority";
        List<Menu> menus = this.menuManager.find(hql);
        List<Map<String, Object>> list = this.convertMenus(menus);
        String json = jsonMapper.toJson(list);
        model.addAttribute("json", json);

        return "auth/dev/menu-export";
    }

    public List<Map<String, Object>> convertMenus(List<Menu> menus) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (Menu menu : menus) {
            list.add(this.convertMenu(menu));
        }

        return list;
    }

    public Map<String, Object> convertMenu(Menu menu) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("code", menu.getCode());
        map.put("name", menu.getTitle());
        map.put("url", menu.getUrl());
        map.put("type", menu.getType());
        map.put("display", menu.getDisplay());
        map.put("perm", menu.getPerm().getCode());
        map.put("priority", menu.getPriority());

        List<Menu> menus = menuManager.find(
                "from Menu where menu=? order by priority", menu);

        if (!menus.isEmpty()) {
            map.put("children", this.convertMenus(menus));
        }

        return map;
    }

    // ~ ======================================================================
    @Resource
    public void setMenuManager(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    @Resource
    public void setPermManager(PermManager permManager) {
        this.permManager = permManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setMenuCache(MenuCache menuCache) {
        this.menuCache = menuCache;
    }
}
