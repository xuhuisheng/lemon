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

/**
 * party.
 */
@Controller
@RequestMapping("party")
public class OrgController {
    private static Logger logger = LoggerFactory.getLogger(OrgController.class);
    public static final int TYPE_ORG = 0;
    public static final int TYPE_USER = 1;
    public static final int TYPE_POSITION = 2;
    private PartyEntityManager partyEntityManager;
    private PartyTypeManager partyTypeManager;
    private PartyStructManager partyStructManager;
    private PartyStructTypeManager partyStructTypeManager;
    private UserConnector userConnector;
    private PartyService partyService;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantHolder tenantHolder;

    /**
     * 初始化组织机构的维度，包括对应维度下的根节点.
     */
    public PartyEntity init(Model model, Long partyStructTypeId,
            Long partyEntityId) {
        String tenantId = tenantHolder.getTenantId();

        // 维度，比如行政组织
        String hqlPartyStructType = "from PartyStructType where tenantId=? and display='true' order by priority";
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

    /**
     * 显示下级列表.
     */
    @RequestMapping("org-list")
    public String list(
            Model model,
            @RequestParam(value = "partyStructTypeId", required = false) Long partyStructTypeId,
            @RequestParam(value = "partyEntityId", required = false) Long partyEntityId,
            @RequestParam(value = "name", required = false) String name,
            @ModelAttribute Page page) {
        PartyEntity partyEntity = this.init(model, partyStructTypeId,
                partyEntityId);

        if (partyEntity != null) {
            // 返回所有下级，包含组织，岗位，人员
            String hql = "from PartyStruct where parentEntity=? and partyStructType.id=?";

            if (name != null) {
                hql += (" and childEntity.name like '%" + name + "%'");
            }

            // 如果没有选中partyEntityId，就啥也不显示
            page = partyStructTypeManager.pagedQuery(hql, page.getPageNo(),
                    page.getPageSize(), partyEntity, partyStructTypeId);
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

    /**
     * 编辑下级.
     */
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

    /**
     * 添加下级.
     */
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

        if (partyType.getType() == TYPE_USER) {
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
        } else if (partyType.getType() == TYPE_POSITION) {
            // 岗位
            PartyEntity child = null;

            if (childEntityId == null) {
                child = new PartyEntity();
                child.setName(childEntityName);
                child.setPartyType(partyType);
                partyEntityManager.save(child);
            } else {
                child = new PartyEntity();
                child.setName(childEntityName);
                child.setPartyType(partyType);
                partyEntityManager.save(child);
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

    /**
     * 删除下级.
     */
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

    /**
     * 维护负责人.
     */
    @RequestMapping("org-admin-list")
    public String orgAdminList(
            Model model,
            @RequestParam(value = "partyStructTypeId", required = false) Long partyStructTypeId,
            @RequestParam(value = "partyEntityId", required = false) Long partyEntityId)
            throws Exception {
        PartyEntity partyEntity = init(model, partyStructTypeId, partyEntityId);

        model.addAttribute("partyEntity", partyEntity);

        // TODO: 先写死id=2是负责关系
        // PartyStructType partyStructType = partyStructTypeManager.get(2L);
        if (partyEntity != null) {
            // 组织的负责人可能是岗位，可能是人
            String hql = "from PartyStruct where parentEntity=? and partyStructType=2";

            // 如果没有选中partyEntityId，就啥也不显示
            Page page = partyStructTypeManager.pagedQuery(hql, 1, 10,
                    partyEntity);
            model.addAttribute("page", page);
        }

        return "party/org-admin-list";
    }

    /**
     * 添加管理人或管理岗位.
     */
    @RequestMapping("org-admin-input")
    public String orgAdminInput(
            Model model,
            @RequestParam(value = "partyStructTypeId", required = false) Long partyStructTypeId,
            @RequestParam(value = "partyTypeId", required = false) Long partyTypeId,
            @RequestParam(value = "partyEntityId", required = false) Long partyEntityId)
            throws Exception {
        partyStructTypeId = 1L;

        PartyEntity partyEntity = init(model, partyStructTypeId, partyEntityId);
        PartyType partyType = partyTypeManager.get(partyTypeId);

        model.addAttribute("partyEntity", partyEntity);
        model.addAttribute("partyType", partyType);

        return "party/org-admin-input";
    }

    /**
     * 保存管理.
     */
    @RequestMapping("org-admin-save")
    public String orgAdminSave(
            @ModelAttribute PartyStruct partyStruct,
            @RequestParam(value = "childEntityRef", required = false) String childEntityRef,
            @RequestParam(value = "childEntityId", required = false) Long childEntityId,
            @RequestParam(value = "childEntityName", required = false) String childEntityName,
            @RequestParam("partyEntityId") Long partyEntityId,
            @RequestParam("partyTypeId") Long partyTypeId,
            @RequestParam("partyStructTypeId") Long partyStructTypeId)
            throws Exception {
        PartyType partyType = partyTypeManager.get(partyTypeId);

        // 管理关系，暂定2，以后看来还是改成code较好
        PartyStructType partyStructType = partyStructTypeManager.get(2L);

        if (partyType.getType() == TYPE_USER) {
            // 人员
            PartyEntity child = partyEntityManager.findUnique(
                    "from PartyEntity where partyType=? and ref=?", partyType,
                    childEntityRef);
            logger.debug("child : {}", child);

            PartyEntity parent = partyEntityManager.get(partyEntityId);

            PartyStruct dest = new PartyStruct();
            beanMapper.copy(partyStruct, dest);
            dest.setPartyStructType(partyStructType);
            dest.setParentEntity(parent);
            dest.setChildEntity(child);
            partyStructManager.save(dest);
        } else if (partyType.getType() == TYPE_POSITION) {
            // 岗位
            PartyEntity child = null;

            if (childEntityId == null) {
                child = new PartyEntity();
                child.setName(childEntityName);
                child.setPartyType(partyType);
                partyEntityManager.save(child);
            } else {
                child = new PartyEntity();
                child.setName(childEntityName);
                child.setPartyType(partyType);
                partyEntityManager.save(child);
            }

            logger.debug("child : {}", child);

            PartyEntity parent = partyEntityManager.get(partyEntityId);

            PartyStruct dest = new PartyStruct();
            beanMapper.copy(partyStruct, dest);
            dest.setPartyStructType(partyStructType);
            dest.setParentEntity(parent);
            dest.setChildEntity(child);
            dest.setTenantId("1");
            partyStructManager.save(dest);
        } else {
            logger.info("unsupport : {}", partyType.getType());
        }

        partyStructTypeId = 1L;

        return "redirect:/party/org-admin-list.do?partyStructTypeId="
                + partyStructTypeId + "&partyEntityId=" + partyEntityId;
    }

    /**
     * 添加职位.
     */
    @RequestMapping("org-position-input")
    public String orgPositionInput(
            Model model,
            @RequestParam(value = "partyStructTypeId", required = false) Long partyStructTypeId,
            @RequestParam(value = "partyTypeId", required = false) Long partyTypeId,
            @RequestParam(value = "partyEntityId", required = false) Long partyEntityId)
            throws Exception {
        partyStructTypeId = 1L;

        PartyEntity partyEntity = init(model, partyStructTypeId, partyEntityId);
        PartyType partyType = partyTypeManager.get(partyTypeId);

        model.addAttribute("partyEntity", partyEntity);
        model.addAttribute("partyType", partyType);

        return "party/org-position-input";
    }

    /**
     * 保存职位.
     */
    @RequestMapping("org-position-save")
    public String orgPositionSave(
            @ModelAttribute PartyStruct partyStruct,
            @RequestParam(value = "childEntityRef", required = false) String childEntityRef,
            @RequestParam(value = "childEntityId", required = false) Long childEntityId,
            @RequestParam(value = "childEntityName", required = false) String childEntityName,
            @RequestParam("partyEntityId") Long partyEntityId,
            @RequestParam("partyTypeId") Long partyTypeId,
            @RequestParam("partyStructTypeId") Long partyStructTypeId)
            throws Exception {
        PartyType partyType = partyTypeManager.get(partyTypeId);

        // 岗位人员是5
        PartyStructType partyStructType = partyStructTypeManager.get(5L);

        if (partyType.getType() == TYPE_POSITION) {
            // 岗位
            PartyEntity child = null;

            if (childEntityId == null) {
                child = new PartyEntity();
                child.setName(childEntityName);
                child.setPartyType(partyType);
                partyEntityManager.save(child);
            } else {
                child = new PartyEntity();
                child.setName(childEntityName);
                child.setPartyType(partyType);
                partyEntityManager.save(child);
            }

            logger.debug("child : {}", child);

            PartyEntity parent = partyEntityManager.get(partyEntityId);

            PartyStruct dest = new PartyStruct();
            beanMapper.copy(partyStruct, dest);
            dest.setPartyStructType(partyStructType);
            dest.setParentEntity(parent);
            dest.setChildEntity(child);
            dest.setTenantId("1");
            partyStructManager.save(dest);
        } else {
            logger.info("unsupport : {}", partyType.getType());
        }

        partyStructTypeId = 1L;

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
