package com.mossle.party.web;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

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

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("party")
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

    public PartyEntity init(Model model, Long partyStructTypeId,
            Long partyEntityId) {
        String tenantId = tenantHolder.getTenantId();

        // 维度，比如行政组织
        String hqlPartyStructType = "from PartyStructType where tenantId=? order by priority";
        List<PartyStructType> partyStructTypes = partyStructTypeManager.find(
                hqlPartyStructType, tenantId);
        PartyStructType partyStructType = null;

        if (partyStructTypeId != null) {
            partyStructType = partyStructTypeManager.get(partyStructTypeId);
        } else {
            if (!partyStructTypes.isEmpty()) {
                // 如果没有指定维度，就使用第一个维度当做默认维度
                partyStructType = partyStructTypes.get(0);
                partyStructTypeId = partyStructType.getId();
            }
        }

        if (partyEntityId == null) {
            // 如果没有指定组织，就返回顶级组织
            List<PartyEntity> partyEntities = partyService
                    .getTopPartyEntities(partyStructTypeId);

            if (!partyEntities.isEmpty()) {
                partyEntityId = partyEntities.get(0).getId();
            }
        }

        model.addAttribute("partyStructTypes", partyStructTypes);
        model.addAttribute("partyStructType", partyStructType);
        model.addAttribute("partyStructTypeId", partyStructTypeId);
        model.addAttribute("partyEntityId", partyEntityId);

        if (partyEntityId == null) {
            return null;
        }

        return partyEntityManager.get(partyEntityId);
    }

    @RequestMapping("org-list")
    public String list(
            Model model,
            @RequestParam(value = "partyStructTypeId", required = false) Long partyStructTypeId,
            @RequestParam(value = "partyEntityId", required = false) Long partyEntityId,
            @ModelAttribute Page page) {
        PartyEntity partyEntity = this.init(model, partyStructTypeId,
                partyEntityId);

        if (partyEntity != null) {
            // 返回所有下级，包含组织，岗位，人员
            String hql = "from PartyStruct where parentEntity=?";
            // 如果没有选中partyEntityId，就啥也不显示
            page = partyStructTypeManager.pagedQuery(hql, page.getPageNo(),
                    page.getPageSize(), partyEntity);
            model.addAttribute("page", page);

            // 判断这个组织下可以创建哪些下级
            // TODO: 应该判断维度
            List<PartyType> childTypes = partyTypeManager
                    .find("select childType from PartyType childType join childType.parentStructRules parentStructRule where parentStructRule.parentType=?",
                            partyEntity.getPartyType());
            model.addAttribute("childTypes", childTypes);
        }

        return "party/org-list";
    }

    @RequestMapping("org-input")
    public String input(
            Model model,
            @RequestParam(value = "partyStructTypeId", required = false) Long partyStructTypeId,
            @RequestParam(value = "partyTypeId", required = false) Long partyTypeId,
            @RequestParam(value = "partyEntityId", required = false) Long partyEntityId)
            throws Exception {
        PartyEntity partyEntity = init(model, partyStructTypeId, partyEntityId);
        PartyType partyType = partyTypeManager.get(partyTypeId);

        model.addAttribute("partyEntity", partyEntity);
        model.addAttribute("partyType", partyType);

        return "party/org-input";
    }

    @RequestMapping("org-save")
    public String save(
            @ModelAttribute PartyStruct partyStruct,
            @RequestParam(value = "childEntityRef", required = false) String childEntityRef,
            @RequestParam(value = "childEntityId", required = false) Long childEntityId,
            @RequestParam(value = "childEntityName", required = false) String childEntityName,
            @RequestParam("partyEntityId") Long partyEntityId,
            @RequestParam("partyTypeId") Long partyTypeId,
            @RequestParam("partyStructTypeId") Long partyStructTypeId)
            throws Exception {
        PartyType partyType = partyTypeManager.get(partyTypeId);

        if (partyType.getType() == 1) {
            // 人员
            PartyEntity child = partyEntityManager.findUnique(
                    "from PartyEntity where partyType=? and ref=?", partyType,
                    childEntityRef);
            logger.debug("child : {}", child);

            PartyEntity parent = partyEntityManager.get(partyEntityId);

            PartyStruct dest = new PartyStruct();
            beanMapper.copy(partyStruct, dest);
            dest.setPartyStructType(partyStructTypeManager
                    .get(partyStructTypeId));
            dest.setParentEntity(parent);
            dest.setChildEntity(child);
            partyStructManager.save(dest);
        } else {
            // 组织
            PartyEntity child = null;

            if (childEntityId == null) {
                child = new PartyEntity();
                child.setName(childEntityName);
                child.setPartyType(partyType);
                partyEntityManager.save(child);
            } else {
                child = partyEntityManager.get(childEntityId);
            }

            logger.debug("child : {}", child);

            PartyEntity parent = partyEntityManager.get(partyEntityId);

            PartyStruct dest = new PartyStruct();
            beanMapper.copy(partyStruct, dest);
            dest.setPartyStructType(partyStructTypeManager
                    .get(partyStructTypeId));
            dest.setParentEntity(parent);
            dest.setChildEntity(child);
            partyStructManager.save(dest);
        }

        return "redirect:/party/org-list.do?partyStructTypeId="
                + partyStructTypeId + "&partyEntityId=" + partyEntityId;
    }

    @RequestMapping("org-remove")
    public String removeUser(
            @RequestParam("selectedItem") List<Long> selectedItem,
            @RequestParam("partyEntityId") Long partyEntityId,
            @RequestParam("partyStructTypeId") Long partyStructTypeId) {
        for (Long childId : selectedItem) {
            PartyStruct partyStruct = partyStructManager.get(childId);
            partyStructManager.remove(partyStruct);
        }

        // addActionMessage(messages.getMessage("core.success.delete", "删除成功"));
        return "redirect:/party/org-list.do?partyStructTypeId="
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
}
