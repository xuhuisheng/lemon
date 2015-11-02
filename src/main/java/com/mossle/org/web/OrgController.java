package com.mossle.org.web;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantConnector;
import com.mossle.api.tenant.TenantDTO;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;

import com.mossle.party.persistence.domain.PartyEntity;
import com.mossle.party.persistence.domain.PartyStruct;
import com.mossle.party.persistence.domain.PartyStructType;
import com.mossle.party.persistence.domain.PartyType;
import com.mossle.party.persistence.manager.PartyEntityManager;
import com.mossle.party.persistence.manager.PartyStructManager;
import com.mossle.party.persistence.manager.PartyStructTypeManager;
import com.mossle.party.persistence.manager.PartyTypeManager;
import com.mossle.party.service.PartyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.util.Assert;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("com.mossle.org.web.OrgController")
@RequestMapping("org")
public class OrgController {
    private static Logger logger = LoggerFactory.getLogger(OrgController.class);
    private PartyEntityManager partyEntityManager;
    private PartyTypeManager partyTypeManager;
    private PartyStructManager partyStructManager;
    private PartyStructTypeManager partyStructTypeManager;
    private UserConnector userConnector;
    private PartyService partyService;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantHolder tenantHolder;
    private TenantConnector tenantConnector;

    public void init(Model model, Long partyStructTypeId, Long partyEntityId) {
        String tenantId = tenantHolder.getTenantId();
        List<PartyStructType> partyStructTypes = partyStructTypeManager.find(
                "from PartyStructType where tenantId=? order by priority",
                tenantId);
        PartyStructType partyStructType = null;

        if (partyStructTypeId == null) {
            partyStructType = partyStructTypes.get(0);
            partyStructTypeId = partyStructType.getId();
        } else {
            partyStructType = partyStructTypeManager.get(partyStructTypeId);
        }

        if (partyEntityId == null) {
            partyEntityId = partyService.getTopPartyEntities(partyStructTypeId)
                    .get(0).getId();
        }

        model.addAttribute("partyStructType", partyStructType);
        model.addAttribute("partyStructTypeId", partyStructTypeId);
        model.addAttribute("partyEntityId", partyEntityId);
        model.addAttribute("partyStructTypes", partyStructTypes);
    }

    @RequestMapping("org-users")
    public String users(
            Model model,
            @RequestParam(value = "partyStructTypeId", required = false) Long partyStructTypeId,
            @RequestParam(value = "partyEntityId", required = false) Long partyEntityId,
            @ModelAttribute Page page) {
        init(model, partyStructTypeId, partyEntityId);

        String tenantId = tenantHolder.getTenantId();

        String hql = "from PartyStruct where childEntity.partyType.type=1 and parentEntity.id=? and tenantId=?";
        page = partyStructTypeManager.pagedQuery(hql, page.getPageNo(),
                page.getPageSize(), partyEntityId, tenantId);
        model.addAttribute("page", page);

        return "org/org-users";
    }

    @RequestMapping("org-inputUser")
    public String inputUser(
            Model model,
            @RequestParam(value = "partyStructTypeId", required = false) Long partyStructTypeId,
            @RequestParam(value = "partyEntityId", required = false) Long partyEntityId)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        init(model, partyStructTypeId, partyEntityId);

        List<PartyStructType> partyStructTypes = partyStructTypeManager.findBy(
                "tenantId", tenantId);

        model.addAttribute("partyStructTypes", partyStructTypes);

        return "org/org-inputUser";
    }

    @RequestMapping("org-saveUser")
    public String saveUser(@ModelAttribute PartyStruct partyStruct,
            @RequestParam("name") String name,
            @RequestParam("partyEntityId") Long partyEntityId,
            @RequestParam("partyStructTypeId") Long partyStructTypeId)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        TenantDTO tenantDto = tenantConnector.findById(tenantId);
        UserDTO userDto = userConnector.findByUsername(name,
                tenantDto.getUserRepoRef());
        logger.debug("user id : {}", userDto.getId());

        PartyEntity child = partyEntityManager
                .findUnique(
                        "from PartyEntity where partyType.type=1 and ref=? and tenantId=?",
                        userDto.getId(), tenantId);
        logger.debug("child : {}", child);

        PartyEntity parent = partyEntityManager.get(partyEntityId);

        PartyStruct dest = new PartyStruct();
        beanMapper.copy(partyStruct, dest);
        dest.setPartyStructType(partyStructTypeManager.get(partyStructTypeId));
        dest.setParentEntity(parent);
        dest.setChildEntity(child);
        partyStructManager.save(dest);

        return "redirect:/org/org-users.do?partyStructTypeId="
                + partyStructTypeId + "&partyEntityId=" + partyEntityId;
    }

    @RequestMapping("org-removeUser")
    public String removeUser(
            @RequestParam("selectedItem") List<Long> selectedItem,
            @RequestParam("partyEntityId") Long partyEntityId,
            @RequestParam("partyStructTypeId") Long partyStructTypeId) {
        for (Long childId : selectedItem) {
            PartyStruct partyStruct = partyStructManager.get(childId);
            partyStructManager.remove(partyStruct);
        }

        // addActionMessage(messages.getMessage("core.success.delete", "删除成功"));
        return "redirect:/org/org-users.do?partyStructTypeId="
                + partyStructTypeId + "&partyEntityId=" + partyEntityId;
    }

    // ~ ==================================================
    @RequestMapping("org-children")
    public String children(
            Model model,
            @RequestParam(value = "partyStructTypeId", required = false) Long partyStructTypeId,
            @RequestParam(value = "partyEntityId", required = false) Long partyEntityId,
            @ModelAttribute Page page) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        init(model, partyStructTypeId, partyEntityId);

        String hql = "select ps from PartyEntity child join child.parentStructs ps join ps.parentEntity parent"
                + " where child.partyType.type=0 and parent.id=? and child.tenantId=?";
        page = partyEntityManager.pagedQuery(hql, page.getPageNo(),
                page.getPageSize(), partyEntityId, tenantId);
        model.addAttribute("page", page);

        return "org/org-children";
    }

    @RequestMapping("org-inputChild")
    public String inputChild(
            Model model,
            @RequestParam(value = "partyStructTypeId", required = false) Long partyStructTypeId,
            @RequestParam(value = "partyEntityId", required = false) Long partyEntityId)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        init(model, partyStructTypeId, partyEntityId);

        List<PartyType> partyTypes = partyTypeManager.find(
                "from PartyType where type=0 and tenantId=?", tenantId);

        model.addAttribute("partyTypes", partyTypes);

        return "org/org-inputChild";
    }

    @RequestMapping("org-saveChild")
    public String saveChild(@RequestParam("name") String name,
            @RequestParam("partyTypeId") Long partyTypeId,
            @RequestParam("partyEntityId") Long partyEntityId,
            @RequestParam("partyStructTypeId") Long partyStructTypeId) {
        String tenantId = tenantHolder.getTenantId();
        PartyEntity child = partyEntityManager
                .findUnique(
                        "from PartyEntity where name=? and partyType.id=? and tenantId=?",
                        name, partyTypeId, tenantId);
        PartyEntity parent = partyEntityManager.get(partyEntityId);
        Assert.notNull(child, name + "(" + partyTypeId + ") is null");
        Assert.notNull(parent, partyEntityId + " is null");

        PartyStruct partyStruct = new PartyStruct();
        partyStruct.setPartyStructType(partyStructTypeManager
                .get(partyStructTypeId));
        partyStruct.setParentEntity(partyEntityManager.get(parent.getId()));
        partyStruct.setChildEntity(partyEntityManager.get(child.getId()));
        partyStructManager.save(partyStruct);

        return "redirect:/org/org-children.do?partyStructTypeId="
                + partyStructTypeId + "&partyEntityId=" + partyEntityId;
    }

    @RequestMapping("org-removeChild")
    public String removeChild(
            @RequestParam("selectedItem") List<Long> selectedItem,
            @RequestParam("partyEntityId") Long partyEntityId,
            @RequestParam("partyStructTypeId") Long partyStructTypeId) {
        for (Long childId : selectedItem) {
            PartyStruct partyStruct = partyStructManager.get(childId);
            partyStructManager.remove(partyStruct);
        }

        return "redirect:/org/org-children.do?partyStructTypeId="
                + partyStructTypeId + "&partyEntityId=" + partyEntityId;
    }

    // ~ ==================================================
    @Resource
    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }

    @Resource
    public void setPartyStructManager(PartyStructManager partyStructManager) {
        this.partyStructManager = partyStructManager;
    }

    @Resource
    public void setPartyTypeManager(PartyTypeManager partyTypeManager) {
        this.partyTypeManager = partyTypeManager;
    }

    @Resource
    public void setPartyStructTypeManager(
            PartyStructTypeManager partyStructTypeManager) {
        this.partyStructTypeManager = partyStructTypeManager;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setTenantConnector(TenantConnector tenantConnector) {
        this.tenantConnector = tenantConnector;
    }
}
