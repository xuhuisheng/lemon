package com.mossle.auth.web;

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
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("auth")
public class MenuController {
    private MenuManager menuManager;
    private PermManager permManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MenuCache menuCache;

    @RequestMapping("menu-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = menuManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "auth/menu-list";
    }

    @RequestMapping("menu-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            Menu menu = menuManager.get(id);
            model.addAttribute("model", menu);

            String hql = "from Menu where menu.id!=?";
            List<Menu> menus = this.menuManager.find(hql, id);
            model.addAttribute("menus", menus);
        } else {
            List<Menu> menus = this.menuManager.getAll();
            model.addAttribute("menus", menus);
        }

        List<Perm> perms = this.permManager.getAll();
        model.addAttribute("perms", perms);

        return "auth/menu-input";
    }

    @RequestMapping("menu-save")
    public String save(@ModelAttribute Menu menu,
            @RequestParam(value = "parentId", required = false) Long parentId,
            @RequestParam(value = "permId", required = false) Long permId,
            RedirectAttributes redirectAttributes) {
        Menu dest = null;
        Long id = menu.getId();

        if (id != null) {
            dest = menuManager.get(id);
            beanMapper.copy(menu, dest);
        } else {
            dest = menu;
        }

        if (parentId != null) {
            dest.setMenu(menuManager.get(parentId));
        } else {
            dest.setMenu(null);
        }

        if (permId != null) {
            dest.setPerm(permManager.get(permId));
        } else {
            dest.setPerm(null);
        }

        menuManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");
        this.menuCache.clear();

        return "redirect:/auth/menu-list.do";
    }

    @RequestMapping("menu-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<Menu> menus = menuManager.findByIds(selectedItem);
        menuManager.removeAll(menus);
        messageHelper.addFlashMessage(redirectAttributes, "core.delete.save",
                "删除成功");
        this.menuCache.clear();

        return "redirect:/auth/menu-list.do";
    }

    @RequestMapping("menu-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = menuManager.pagedQuery(page, propertyFilters);

        List<Menu> menus = (List<Menu>) page.getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("menu");
        tableModel.addHeaders("id", "name");
        tableModel.setData(menus);
        exportor.export(request, response, tableModel);
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
