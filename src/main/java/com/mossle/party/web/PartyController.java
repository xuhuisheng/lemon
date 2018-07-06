package com.mossle.party.web;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.org.OrgConnector;
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

    /**
     * 测试.
     */
    @RequestMapping("test")
    public String test(Model model) throws Exception {
        return "party/test";
    }

    /**
     * 根据userId获取上级领导.
     */
    @RequestMapping("getSuperiorId")
    @ResponseBody
    public String getSuperiorId(@RequestParam("userId") String userId) {
        String superiourId = orgConnector.getSuperiorId(userId);

        return superiourId;
    }

    /**
     * 根据userId和positionName获取userIds.
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
}
