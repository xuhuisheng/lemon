package com.mossle.party.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.core.spring.MessageHelper;

import com.mossle.party.persistence.domain.PartyEntity;
import com.mossle.party.persistence.domain.PartyStruct;
import com.mossle.party.persistence.manager.PartyEntityManager;
import com.mossle.party.persistence.manager.PartyStructManager;
import com.mossle.party.persistence.manager.PartyStructTypeManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("party")
public class AdminBatchController {
    private MessageHelper messageHelper;
    private UserConnector userConnector;
    private PartyEntityManager partyEntityManager;
    private PartyStructManager partyStructManager;
    private PartyStructTypeManager partyStructTypeManager;
    private TenantHolder tenantHolder;

    @RequestMapping("user-repo-list")
    public String list(@RequestParam("id") Long id, Model model) {
        String hql = "select c from PartyEntity c join c.parentStructs p"
                + " where p.parentEntity.reference=? and p.parentEntity.partyType=2 and c.partyType.id=1";
        List<PartyEntity> partyEntities = partyEntityManager.find(hql,
                Long.toString(id));
        model.addAttribute("partyEntities", partyEntities);

        return "party/admin-batch-list";
    }

    @RequestMapping("admin-batch-list")
    public String input(
            @RequestParam(value = "text", required = false) String text,
            Model model) {
        if (text != null) {
            List<UserDTO> userDtos = new ArrayList<UserDTO>();

            for (String str : text.split("\n")) {
                str = str.trim();

                if (str.length() == 0) {
                    continue;
                }

                UserDTO userDto = userConnector.findByUsername(str,
                        tenantHolder.getUserRepoRef());

                if (userDto.getStatus() != 1) {
                    continue;
                }

                userDtos.add(userDto);
            }

            model.addAttribute("userDtos", userDtos);
        }

        return "party/admin-batch-input";
    }

    @RequestMapping("admin-batch-save")
    public String save(@RequestParam("id") Long id,
            @RequestParam("userIds") List<Long> userIds,
            RedirectAttributes redirectAttributes) {
        PartyEntity group = partyEntityManager.findUnique(
                "from PartyEntity where partyType.id=2 and reference=?",
                Long.toString(id));

        for (Long userId : userIds) {
            PartyEntity user = partyEntityManager.findUnique(
                    "from PartyEntity where partyType.id=1 and reference=?",
                    Long.toString(userId));

            PartyStruct partyStruct = partyStructManager
                    .findUnique(
                            "from PartyStruct where partyStructType.id=2 and parentEntity=? and childEntity=?",
                            group, user);

            if (partyStruct == null) {
                partyStruct = new PartyStruct();
                partyStruct.setPartyStructType(partyStructTypeManager.get(2L));
                partyStruct.setChildEntity(group);
                partyStruct.setParentEntity(user);
                partyStructManager.save(partyStruct);
            }
        }

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/party/admin-batch-list.do";
    }

    @RequestMapping("admin-batch-remove")
    public String remove(@RequestParam("id") Long id,
            @RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        PartyEntity group = partyEntityManager.findUnique(
                "from PartyEntity where partyType.id=2 and reference=?",
                Long.toString(id));

        for (Long userId : selectedItem) {
            PartyEntity user = partyEntityManager.findUnique(
                    "from PartyEntity where partyType.id=1 and reference=?",
                    Long.toString(userId));

            PartyStruct partyStruct = partyStructManager
                    .findUnique(
                            "from PartyStruct where partyStructType.id=2 and parentEntity=? and childEntity=?",
                            group, user);

            if (partyStruct != null) {
                partyStructManager.remove(partyStruct);
            }
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/party/admin-batch-list.do";
    }

    // ~ ======================================================================
    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
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
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
