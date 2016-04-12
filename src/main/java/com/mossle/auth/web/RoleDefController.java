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

import com.mossle.auth.component.RoleDefChecker;
import com.mossle.auth.persistence.domain.Role;
import com.mossle.auth.persistence.domain.RoleDef;
import com.mossle.auth.persistence.manager.RoleDefManager;
import com.mossle.auth.persistence.manager.RoleManager;
import com.mossle.auth.support.CheckRoleException;
import com.mossle.auth.support.RoleDTO;

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
public class RoleDefController {
    private static Logger logger = LoggerFactory
            .getLogger(RoleDefController.class);
    private RoleDefManager roleDefManager;
    private RoleManager roleManager;
    private MessageHelper messageHelper;
    private RoleDefChecker roleDefChecker;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantConnector tenantConnector;
    private TenantHolder tenantHolder;

    @RequestMapping("role-def-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantHolder
                .getTenantId()));
        page = roleDefManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "auth/role-def-list";
    }

    @RequestMapping("role-def-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            RoleDef roleDef = roleDefManager.get(id);
            model.addAttribute("model", roleDef);
        }

        return "auth/role-def-input";
    }

    @RequestMapping("role-def-save")
    public String save(@ModelAttribute RoleDef roleDef,
            RedirectAttributes redirectAttributes) {
        try {
            // before check
            roleDefChecker.check(roleDef);

            // after invoke
            RoleDef dest = null;
            Long id = roleDef.getId();

            if (id != null) {
                dest = roleDefManager.get(id);
                beanMapper.copy(roleDef, dest);
            } else {
                dest = roleDef;
            }

            if (id == null) {
                dest.setTenantId(tenantHolder.getTenantId());
            }

            roleDefManager.save(dest);

            messageHelper.addFlashMessage(redirectAttributes,
                    "core.success.save", "保存成功");
        } catch (CheckRoleException ex) {
            logger.warn(ex.getMessage(), ex);
            redirectAttributes.addFlashAttribute("message", ex.getMessage());

            return "auth/role-def-input";
        }

        return "redirect:/auth/role-def-list.do";
    }

    @RequestMapping("role-def-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        try {
            List<RoleDef> roleDefs = roleDefManager.findByIds(selectedItem);

            for (RoleDef roleDef : roleDefs) {
                roleDefChecker.check(roleDef);
            }

            roleDefManager.removeAll(roleDefs);
            messageHelper.addFlashMessage(redirectAttributes,
                    "core.success.delete", "删除成功");
        } catch (CheckRoleException ex) {
            logger.warn(ex.getMessage(), ex);

            messageHelper.addFlashMessage(redirectAttributes, ex.getMessage());
        }

        return "redirect:/auth/role-def-list.do";
    }

    @RequestMapping("role-def-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = roleDefManager.pagedQuery(page, propertyFilters);

        List<RoleDef> roleDefs = (List<RoleDef>) page.getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("role");
        tableModel.addHeaders("id", "name", "descn");
        tableModel.setData(roleDefs);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("role-def-checkName")
    @ResponseBody
    public boolean checkName(@RequestParam("name") String name,
            @RequestParam(value = "id", required = false) Long id)
            throws Exception {
        String hql = "from RoleDef where tenantId="
                + tenantHolder.getTenantId() + " and name=?";
        Object[] params = { name };

        if (id != null) {
            hql = "from RoleDef where tenantId=" + tenantHolder.getTenantId()
                    + " and name=? and id<>?";
            params = new Object[] { name, id };
        }

        boolean result = roleDefManager.findUnique(hql, params) == null;

        return result;
    }

    @RequestMapping("role-def-manage")
    public String manage(@RequestParam("id") Long id, Model model)
            throws Exception {
        RoleDef roleDef = roleDefManager.get(id);
        List<Role> roles = roleManager.findBy("roleDef.id", id);

        TenantDTO currentTenant = tenantHolder.getTenantDto();
        List<TenantDTO> tenantDtos;

        if (currentTenant.isShared()) {
            tenantDtos = tenantConnector.findAll();
        } else {
            tenantDtos = new ArrayList<TenantDTO>();
            tenantDtos.add(currentTenant);
        }

        List<RoleDTO> roleDtos = new ArrayList<RoleDTO>();

        for (TenantDTO tenantDto : tenantDtos) {
            Role existedRole = null;

            for (Role role : roles) {
                if (role.getTenantId().equals(tenantDto.getId())) {
                    existedRole = role;

                    break;
                }
            }

            if (existedRole == null) {
                RoleDTO roleDto = new RoleDTO();
                roleDto.setName(roleDef.getName());
                roleDto.setTenantId(tenantDto.getId());
                roleDto.setStatus("added");
                roleDtos.add(roleDto);
            } else {
                RoleDTO roleDto = new RoleDTO();
                roleDto.setName(roleDef.getName());
                roleDto.setId(existedRole.getId());
                roleDto.setTenantId(tenantDto.getId());
                roleDto.setStatus("existed");
                roleDtos.add(roleDto);
            }
        }

        for (Role role : roles) {
            boolean existed = false;

            for (TenantDTO tenantDto : tenantDtos) {
                if (role.getTenantId().equals(tenantDto.getId())) {
                    existed = true;

                    break;
                }
            }

            if (!existed) {
                RoleDTO roleDto = new RoleDTO();
                roleDto.setName(roleDef.getName());
                roleDto.setId(role.getId());
                roleDto.setTenantId(role.getTenantId());
                roleDto.setStatus("removed");
                roleDtos.add(roleDto);
            }
        }

        model.addAttribute("roleDts", roleDtos);

        return "auth/role-def-manage";
    }

    @RequestMapping("role-def-sync")
    public String sync(@RequestParam("id") Long id) throws Exception {
        RoleDef roleDef = roleDefManager.get(id);
        List<Role> roles = roleManager.findBy("roleDef.id", id);

        TenantDTO currentTenant = tenantHolder.getTenantDto();
        List<TenantDTO> tenantDtos;

        if (currentTenant.isShared()) {
            tenantDtos = tenantConnector.findAll();
        } else {
            tenantDtos = new ArrayList<TenantDTO>();
            tenantDtos.add(currentTenant);
        }

        for (TenantDTO tenantDto : tenantDtos) {
            Role existedRole = null;

            for (Role role : roles) {
                if (role.getTenantId().equals(tenantDto.getId())) {
                    existedRole = role;

                    break;
                }
            }

            if (existedRole == null) {
                Role role = new Role();
                role.setName(roleDef.getName());
                role.setRoleDef(roleDef);
                role.setTenantId(tenantDto.getId());
                roleManager.save(role);
            }
        }

        for (Role role : roles) {
            boolean existed = false;

            for (TenantDTO tenantDto : tenantDtos) {
                if (role.getTenantId().equals(tenantDto.getId())) {
                    existed = true;

                    break;
                }
            }

            if (!existed) {
                roleManager.remove(role);
            }
        }

        return "redirect:/auth/role-def-manage.do?id=" + id;
    }

    // ~ ======================================================================
    @Resource
    public void setRoleDefManager(RoleDefManager roleDefManager) {
        this.roleDefManager = roleDefManager;
    }

    @Resource
    public void setRoleManager(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    @Resource
    public void setRoleDefChecker(RoleDefChecker roleDefChecker) {
        this.roleDefChecker = roleDefChecker;
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
