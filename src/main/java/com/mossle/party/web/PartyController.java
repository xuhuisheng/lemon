package com.mossle.party.web;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.org.OrgConnector;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserDTO;

import com.mossle.client.user.UserClient;

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
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * party.
 */
@Controller
@RequestMapping("party")
public class PartyController {
    private static Logger logger = LoggerFactory
            .getLogger(PartyController.class);
    private OrgConnector orgConnector;
    private PartyEntityManager partyEntityManager;
    private PartyTypeManager partyTypeManager;
    private PartyStructManager partyStructManager;
    private PartyStructTypeManager partyStructTypeManager;
    private PartyService partyService;
    private UserClient userClient;

    /**
     * 管理首页，组织人员.
     *
     * @param partyEntityId Long
     * @param page Page
     * @param model Model
     * @return String
     * @throws Exception ex
     */
    @RequestMapping("index")
    public String index(
            @RequestParam(value = "partyEntityId", required = false) Long partyEntityId,
            Page page, Model model) throws Exception {
        if (partyEntityId == null) {
            partyEntityId = partyService.getDefaultRootPartyEntityId();
        }

        // party entity
        PartyEntity partyEntity = partyEntityManager.get(partyEntityId);
        model.addAttribute("partyEntity", partyEntity);

        // child
        String hql = "select c from PartyStruct c where c.parentEntity.id=? and c.childEntity.partyType.type=1 and c.partyStructType.type='struct'";
        page = partyEntityManager.pagedQuery(hql, page.getPageNo(),
                page.getPageSize(), partyEntityId);
        model.addAttribute("page", page);

        return "party/index";
    }

    /**
     * 管理首页，组织结构.
     *
     * @param partyEntityId Long
     * @param model Model
     * @return String
     * @throws Exception ex
     */
    @RequestMapping("index-org")
    public String indexOrg(
            @RequestParam(value = "partyEntityId", required = false) Long partyEntityId,
            Model model) throws Exception {
        if (partyEntityId == null) {
            partyEntityId = partyService.getDefaultRootPartyEntityId();
        }

        // party entity
        PartyEntity partyEntity = partyEntityManager.get(partyEntityId);
        model.addAttribute("partyEntity", partyEntity);

        // child
        String hql = "select c from PartyStruct c where c.parentEntity.id=? and c.childEntity.partyType.type=0";
        List<PartyStruct> children = partyEntityManager
                .find(hql, partyEntityId);
        Page page = new Page(children, children.size());
        model.addAttribute("page", page);

        return "party/index-org";
    }

    /**
     * 新增下级部门.
     *
     * @param parentId Long
     * @param model Model
     * @return String
     * @throws Exception ex
     */
    @RequestMapping("index-org-add")
    public String indexOrgAdd(@RequestParam("parentId") Long parentId,
            Model model) throws Exception {
        PartyEntity parent = partyEntityManager.get(parentId);
        model.addAttribute("parent", parent);

        return "party/index-org-add";
    }

    /**
     * 新增下级部门.
     *
     * @param parentId Long
     * @param code String
     * @param name String
     * @return String
     */
    @RequestMapping("index-org-save")
    public String indexOrgSave(@RequestParam("parentId") Long parentId,
            @RequestParam("code") String code, @RequestParam("name") String name) {
        // child
        PartyEntity partyEntity = new PartyEntity();
        partyEntity.setCode(code);
        partyEntity.setName(name);
        partyEntity.setPartyType(partyTypeManager.findUniqueBy("ref",
                "department"));
        partyEntityManager.save(partyEntity);

        // parent
        PartyEntity parent = partyEntityManager.get(parentId);

        // struct
        PartyStruct partyStruct = new PartyStruct();
        partyStruct.setParentEntity(parent);
        partyStruct.setChildEntity(partyEntity);
        partyStruct.setPartyStructType(partyStructTypeManager.findUniqueBy(
                "type", "struct"));
        partyStructManager.save(partyStruct);

        return "redirect:/party/index-org.do?partyEntityId=" + parentId;
    }

    /**
     * 编辑部门.
     *
     * @param id Long
     * @param model Model
     * @return String
     * @throws Exception ex
     */
    @RequestMapping("index-org-edit")
    public String indexOrgEdit(@RequestParam("id") Long id, Model model)
            throws Exception {
        PartyStruct partyStruct = partyStructManager.get(id);
        PartyEntity partyEntity = partyStruct.getChildEntity();
        model.addAttribute("partyEntity", partyEntity);

        // parent
        PartyEntity parent = partyStruct.getParentEntity();
        model.addAttribute("parent", parent);

        return "party/index-org-edit";
    }

    /**
     * 编辑部门.
     *
     * @param id Long
     * @param code String
     * @param name String
     * @return String
     */
    @RequestMapping("index-org-update")
    public String indexOrgUpdate(@RequestParam("id") Long id,
            @RequestParam("code") String code, @RequestParam("name") String name) {
        PartyStruct partyStruct = partyStructManager.get(id);
        PartyEntity partyEntity = partyStruct.getChildEntity();
        partyEntity.setCode(code);
        partyEntity.setName(name);
        partyEntityManager.save(partyEntity);

        // parent
        PartyEntity parent = partyStruct.getParentEntity();

        return "redirect:/party/index-org.do?partyEntityId=" + parent.getId();
    }

    /**
     * 删除部门.
     *
     * @param id Long
     * @return String
     */
    @RequestMapping("index-org-remove")
    public String indexOrgRemove(@RequestParam("id") Long id) {
        PartyStruct partyStruct = partyStructManager.get(id);
        partyStructManager.remove(partyStruct);

        // child
        PartyEntity partyEntity = partyStruct.getChildEntity();
        partyEntityManager.remove(partyEntity);

        // parent
        PartyEntity parent = partyStruct.getParentEntity();

        return "redirect:/party/index-org.do?partyEntityId=" + parent.getId();
    }

    /**
     * 新增人员.
     *
     * @param parentId Long
     * @param model Model
     * @return String
     * @throws Exception ex
     */
    @RequestMapping("index-add")
    public String indexAdd(@RequestParam("parentId") Long parentId, Model model)
            throws Exception {
        if (parentId == null) {
            parentId = partyService.getDefaultRootPartyEntityId();
        }

        PartyEntity parent = partyEntityManager.get(parentId);
        model.addAttribute("parent", parent);

        return "party/index-add";
    }

    /**
     * 新增人员.
     *
     *
     * @param parentId Long
     * @param userId String
     * @param model Model
     * @return String
     * @throws Exception ex
     */
    @RequestMapping("index-save")
    public String indexSave(
            @RequestParam(value = "parentId", required = false) Long parentId,
            @RequestParam("userId") String userId, Model model)
            throws Exception {
        if (parentId == null) {
            parentId = partyService.getDefaultRootPartyEntityId();
        }

        // parent
        PartyEntity parent = partyEntityManager.get(parentId);
        model.addAttribute("parent", parent);

        // child
        PartyEntity child = partyEntityManager.findUnique(
                "from PartyEntity where ref=? and partyType.type=1", userId);

        if (child == null) {
            // user
            UserDTO userDto = userClient.findById(userId, "1");
            child = new PartyEntity();
            child.setCode(userDto.getUsername());
            child.setName(userDto.getDisplayName());
            child.setRef(userId);
            partyEntityManager.save(child);
        }

        // struct
        PartyStruct partyStruct = new PartyStruct();
        partyStruct.setParentEntity(parent);
        partyStruct.setChildEntity(child);
        partyStruct.setPartyStructType(partyStructTypeManager.findUniqueBy(
                "type", "struct"));
        partyStructManager.save(partyStruct);

        return "redirect:/party/index.do?partyEntityId=" + parentId;
    }

    /**
     * 删除人员.
     *
     * @param id Long
     * @return String
     */
    @RequestMapping("index-remove")
    public String indexRemove(@RequestParam("id") Long id) {
        PartyStruct partyStruct = partyStructManager.get(id);
        partyStructManager.remove(partyStruct);

        // parent
        PartyEntity parent = partyStruct.getParentEntity();

        return "redirect:/party/index.do?partyEntityId=" + parent.getId();
    }

    /**
     * 汇报线.
     *
     * @param id Long
     * @param model Model
     * @return String
     * @throws Exception ex
     */
    @RequestMapping("index-line")
    public String indexLine(@RequestParam("id") Long id, Model model)
            throws Exception {
        PartyStruct partyStruct = partyStructManager.get(id);
        model.addAttribute("partyStruct", partyStruct);

        List<PartyEntity> reportLines = partyService
                .findReportLines(partyStruct.getChildEntity().getId());
        model.addAttribute("reportLines", reportLines);

        if (reportLines.size() > 1) {
            model.addAttribute("superiour", reportLines.get(1));
        }

        return "party/index-line";
    }

    @RequestMapping("index-line-save")
    public String indexLineSave(@RequestParam("id") Long id, String userId)
            throws Exception {
        PartyStruct partyStruct = partyStructManager.get(id);

        // child
        PartyEntity child = partyStruct.getChildEntity();

        // parent
        PartyEntity parent = partyEntityManager.findUnique(
                "from PartyEntity where ref=? and partyType.type=1", userId);

        if (parent == null) {
            // user
            UserDTO userDto = userClient.findById(userId, "1");
            parent = new PartyEntity();
            parent.setCode(userDto.getUsername());
            parent.setName(userDto.getDisplayName());
            parent.setRef(userId);
            partyEntityManager.save(parent);
        }

        // struct
        PartyStruct targetPartyStruct = partyStructManager
                .findUnique(
                        "from PartyStruct where childEntity=? and partyStructType.type='report'",
                        child);

        if (targetPartyStruct == null) {
            targetPartyStruct = new PartyStruct();
        }

        targetPartyStruct.setParentEntity(parent);
        targetPartyStruct.setChildEntity(child);
        targetPartyStruct.setPartyStructType(partyStructTypeManager
                .findUniqueBy("type", "report"));
        partyStructManager.save(targetPartyStruct);

        return "redirect:/party/index-line.do?id=" + id;
    }

    /**
     * 测试.
     *
     * @param model Model
     * @return String
     * @throws Exception ex
     */
    @RequestMapping("test")
    public String test(Model model) throws Exception {
        return "party/test";
    }

    /**
     * 根据userId获取上级领导.
     *
     * @param userId String
     * @return String 
     */
    @RequestMapping("getSuperiorId")
    @ResponseBody
    public String getSuperiorId(@RequestParam("userId") String userId) {
        String superiourId = orgConnector.getSuperiorId(userId);

        return superiourId;
    }

    /**
     * 根据userId和positionName获取userIds.
     *
     * @param userId String
     * @param positionName String
     * @return String
     */
    @RequestMapping("getPositionUserIds")
    @ResponseBody
    public String getPositionUserIds(@RequestParam("userId") String userId,
            @RequestParam("positionName") String positionName) {
        List<String> userIds = orgConnector.getPositionUserIds(userId,
                positionName);

        return userIds.toString();
    }

    // ~ ==================================================
    @Resource
    public void setOrgConnector(OrgConnector orgConnector) {
        this.orgConnector = orgConnector;
    }

    @Resource
    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }

    @Resource
    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }

    @Resource
    public void setPartyTypeManager(PartyTypeManager partyTypeManager) {
        this.partyTypeManager = partyTypeManager;
    }

    @Resource
    public void setPartyStructManager(PartyStructManager partyStructManager) {
        this.partyStructManager = partyStructManager;
    }

    @Resource
    public void setPartyStructTypeManager(
            PartyStructTypeManager partyStructTypeManager) {
        this.partyStructTypeManager = partyStructTypeManager;
    }

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }
}
