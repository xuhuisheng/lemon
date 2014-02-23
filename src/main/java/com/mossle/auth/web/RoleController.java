package com.mossle.auth.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.scope.ScopeConnector;
import com.mossle.api.scope.ScopeDTO;
import com.mossle.api.scope.ScopeHolder;

import com.mossle.auth.component.RoleChecker;
import com.mossle.auth.domain.Role;
import com.mossle.auth.domain.RoleDef;
import com.mossle.auth.manager.RoleDefManager;
import com.mossle.auth.manager.RoleManager;
import com.mossle.auth.support.CheckRoleException;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;

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
    private ScopeConnector scopeConnector;

    @RequestMapping("role-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_scopeId", ScopeHolder
                .getScopeId()));
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
                "from RoleDef where scopeId=?", ScopeHolder.getScopeId());

        List<ScopeDTO> scopeDtos = scopeConnector.findSharedScopes();

        for (ScopeDTO scopeDto : scopeDtos) {
            roleDefs.addAll(roleDefManager.find(
                    "from RoleDef where scopeInfo=?", scopeDto.getId()));
        }

        List<Role> roles = roleManager.findBy("scopeId",
                ScopeHolder.getScopeId());
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
                dest.setScopeId(ScopeHolder.getScopeId());
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
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = roleManager.pagedQuery(page, propertyFilters);

        List<Role> roles = (List<Role>) page.getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("role");
        tableModel.addHeaders("id", "name", "descn");
        tableModel.setData(roles);
        exportor.export(response, tableModel);
    }

    @RequestMapping("role-checkName")
    @ResponseBody
    public boolean checkName(@RequestParam("name") String name,
            @RequestParam(value = "id", required = false) Long id)
            throws Exception {
        String hql = "from Role where scopeId=" + ScopeHolder.getScopeId()
                + " and name=?";
        Object[] params = { name };

        if (id != 0L) {
            hql = "from Role where scopeId=" + ScopeHolder.getScopeId()
                    + " and name=? and id<>?";
            params = new Object[] { name, id };
        }

        boolean result = roleManager.findUnique(hql, params) == null;

        return result;
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
    public void setScopeConnector(ScopeConnector scopeConnector) {
        this.scopeConnector = scopeConnector;
    }
}
