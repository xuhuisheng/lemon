package com.mossle.auth.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantConnector;
import com.mossle.api.tenant.TenantDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.auth.component.RoleChecker;
import com.mossle.auth.persistence.domain.Role;
import com.mossle.auth.persistence.domain.RoleDef;
import com.mossle.auth.persistence.manager.RoleDefManager;
import com.mossle.auth.persistence.manager.RoleManager;
import com.mossle.auth.support.CheckRoleException;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("auth")
public class RoleController {
    private static Logger logger = LoggerFactory
            .getLogger(RoleController.class);
    private RoleManager roleManager;
    private RoleDefManager roleDefManager;
    private MessageHelper messageHelper;
    private RoleChecker roleChecker;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantConnector tenantConnector;
    private TenantHolder tenantHolder;

    @RequestMapping("role-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantHolder
                .getTenantId()));
        page = roleManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "auth/role-list";
    }

    @RequestMapping("role-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            Role role = roleManager.get(id);
            model.addAttribute("role", role);
        }

        List<RoleDef> roleDefs = roleDefManager.find(
                "from RoleDef where tenantId=?", tenantHolder.getTenantId());

        List<TenantDTO> tenantDtos = tenantConnector.findSharedTenants();

        for (TenantDTO tenantDto : tenantDtos) {
            roleDefs.addAll(roleDefManager.find(
                    "from RoleDef where tenantInfo=?", tenantDto.getId()));
        }

        List<Role> roles = roleManager.findBy("tenantId",
                tenantHolder.getTenantId());
        List<RoleDef> removedRoleDefs = new ArrayList<RoleDef>();

        for (Role role : roles) {
            for (RoleDef roleDef : roleDefs) {
                if (roleDef.getId().equals(role.getRoleDef().getId())) {
                    removedRoleDefs.add(roleDef);

                    break;
                }
            }
        }

        roleDefs.removeAll(removedRoleDefs);
        model.addAttribute("roleDefs", roleDefs);

        return "auth/role-input";
    }

    @RequestMapping("role-save")
    public String save(@ModelAttribute Role role,
            @RequestParam("roleDefId") Long roleDefId,
            RedirectAttributes redirectAttributes) {
        try {
            // before check
            roleChecker.check(role);

            // after invoke
            Role dest = null;
            Long id = role.getId();

            if (id != null) {
                dest = roleManager.get(id);
                beanMapper.copy(role, dest);
            } else {
                dest = role;
            }

            if (id == null) {
                dest.setTenantId(tenantHolder.getTenantId());
            }

            dest.setName(roleDefManager.get(roleDefId).getName());
            dest.setRoleDef(roleDefManager.get(roleDefId));

            roleManager.save(dest);
            messageHelper.addFlashMessage(redirectAttributes,
                    "core.success.save", "保存成功");
        } catch (CheckRoleException ex) {
            logger.warn(ex.getMessage(), ex);
            redirectAttributes.addFlashAttribute("message", ex.getMessage());

            return "auth/role-input";
        }

        return "redirect:/auth/role-list.do";
    }

    @RequestMapping("role-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        try {
            List<Role> roles = roleManager.findByIds(selectedItem);

            for (Role role : roles) {
                roleChecker.check(role);
            }

            roleManager.removeAll(roles);
            messageHelper.addFlashMessage(redirectAttributes,
                    "core.success.delete", "删除成功");
        } catch (CheckRoleException ex) {
            logger.warn(ex.getMessage(), ex);
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }

        return "redirect:/auth/role-list.do";
    }

    @RequestMapping("role-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = roleManager.pagedQuery(page, propertyFilters);

        List<Role> roles = (List<Role>) page.getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("role");
        tableModel.addHeaders("id", "name", "descn");
        tableModel.setData(roles);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("role-checkName")
    @ResponseBody
    public boolean checkName(@RequestParam("name") String name,
            @RequestParam(value = "id", required = false) Long id)
            throws Exception {
        String hql = "from Role where tenantId=" + tenantHolder.getTenantId()
                + " and name=?";
        Object[] params = { name };

        if (id != 0L) {
            hql = "from Role where tenantId=" + tenantHolder.getTenantId()
                    + " and name=? and id<>?";
            params = new Object[] { name, id };
        }

        boolean result = roleManager.findUnique(hql, params) == null;

        return result;
    }

    // ~ ======================================================================
    @RequestMapping("role-viewList")
    public String viewList(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantHolder
                .getTenantId()));
        page = roleManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "auth/role-viewList";
    }

    @RequestMapping("role-viewInput")
    public String viewInput(
            @RequestParam(value = "id", required = false) Long id, Model model) {
        if (id != null) {
            Role role = roleManager.get(id);
            model.addAttribute("model", role);
        }

        return "auth/role-viewInput";
    }

    @RequestMapping("role-viewSave")
    public String viewSave(@ModelAttribute Role role,
            RedirectAttributes redirectAttributes) {
        try {
            // before check
            roleChecker.check(role);

            // after invoke
            Role dest = null;
            Long id = role.getId();

            if (id != null) {
                dest = roleManager.get(id);
                beanMapper.copy(role, dest);
            } else {
                dest = role;
            }

            if (id == null) {
                dest.setTenantId(tenantHolder.getTenantId());

                RoleDef roleDef = new RoleDef();
                roleDef.setName(role.getName());
                roleDef.setDescn(role.getDescn());
                roleDef.setTenantId(tenantHolder.getTenantId());
                roleDefManager.save(roleDef);
                dest.setRoleDef(roleDef);
            }

            roleManager.save(dest);
            messageHelper.addFlashMessage(redirectAttributes,
                    "core.success.save", "保存成功");
        } catch (CheckRoleException ex) {
            logger.warn(ex.getMessage(), ex);
            redirectAttributes.addFlashAttribute("message", ex.getMessage());

            return "auth/role-viewInput";
        }

        return "redirect:/auth/role-viewList.do";
    }

    @RequestMapping("role-viewRemove")
    public String viewRemove(
            @RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        try {
            List<Role> roles = roleManager.findByIds(selectedItem);

            for (Role role : roles) {
                roleChecker.check(role);
                roleManager.remove(role);
                roleDefManager.remove(role.getRoleDef());
            }

            messageHelper.addFlashMessage(redirectAttributes,
                    "core.success.delete", "删除成功");
        } catch (CheckRoleException ex) {
            logger.warn(ex.getMessage(), ex);
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }

        return "redirect:/auth/role-viewList.do";
    }

    // ~ ======================================================================
    @Resource
    public void setRoleManager(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    @Resource
    public void setRoleDefManager(RoleDefManager roleDefManager) {
        this.roleDefManager = roleDefManager;
    }

    @Resource
    public void setRoleChecker(RoleChecker roleChecker) {
        this.roleChecker = roleChecker;
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
    public void setTenantConnector(TenantConnector tenantConnector) {
        this.tenantConnector = tenantConnector;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
