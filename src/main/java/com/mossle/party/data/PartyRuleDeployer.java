package com.mossle.party.data;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.party.PartyConstants;
import com.mossle.party.persistence.domain.PartyStructRule;
import com.mossle.party.persistence.domain.PartyStructType;
import com.mossle.party.persistence.domain.PartyType;
import com.mossle.party.persistence.manager.PartyStructRuleManager;
import com.mossle.party.persistence.manager.PartyStructTypeManager;
import com.mossle.party.persistence.manager.PartyTypeManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartyRuleDeployer {
    private static Logger logger = LoggerFactory
            .getLogger(PartyRuleDeployer.class);
    private PartyStructTypeManager partyStructTypeManager;
    private PartyTypeManager partyTypeManager;
    private PartyStructRuleManager partyStructRuleManager;
    private String defaultTenantId = "1";

    public void init() {
        this.initPartyStructType();
        this.initPartyType();
        this.initPartyStructRule();
    }

    public void initPartyStructType() {
        this.createPartyStructType("struct", "行政组织", 1, "true");
        this.createPartyStructType("manage", "部门负责人", 2, "false");
        this.createPartyStructType("user-position", "人员岗位", 3, "false");
        this.createPartyStructType("department-position", "部门岗位", 4, "false");
        this.createPartyStructType("report", "汇报线", 5, "false");
    }

    public void initPartyType() {
        // org 0
        this.createPartyType("company", "公司", PartyConstants.TYPE_ORG);
        this.createPartyType("department", "部门", PartyConstants.TYPE_ORG);
        this.createPartyType("group", "群组", PartyConstants.TYPE_ORG);
        // user 1
        this.createPartyType("user", "用户", PartyConstants.TYPE_USER);
        // position 2
        this.createPartyType("position", "岗位", PartyConstants.TYPE_POSITION);
    }

    public void initPartyStructRule() {
        this.createPartyStructRule("struct", "company", "company");
        this.createPartyStructRule("struct", "company", "department");
        this.createPartyStructRule("struct", "company", "group");
        this.createPartyStructRule("struct", "company", "user");
        this.createPartyStructRule("struct", "department", "department");
        this.createPartyStructRule("struct", "department", "group");
        this.createPartyStructRule("struct", "department", "user");
        this.createPartyStructRule("struct", "group", "group");
        this.createPartyStructRule("struct", "group", "user");
    }

    //
    public void createPartyType(String ref, String name, int type) {
        PartyType partyType = this.partyTypeManager.findUniqueBy("ref", ref);

        if (partyType != null) {
            logger.info("partyType exists. skip. {}", ref);

            return;
        }

        partyType = new PartyType();
        partyType.setName(name);
        partyType.setRef(ref);
        partyType.setType(type);
        partyType.setTenantId(defaultTenantId);
        partyTypeManager.save(partyType);
    }

    public void createPartyStructRule(String structType, String parentRef,
            String childRef) {
        String hql = "from PartyStructRule where partyStructType.type=? and parentType.ref=? and childType.ref=?";
        PartyStructRule partyStructRule = this.partyStructRuleManager
                .findUnique(hql, structType, parentRef, childRef);

        if (partyStructRule != null) {
            logger.info("partyStructRule exists. skip. {} {} {}", structType,
                    parentRef, childRef);

            return;
        }

        PartyStructType partyStructType = this.partyStructTypeManager
                .findUniqueBy("type", structType);

        if (partyStructType == null) {
            logger.info("partyStructType not exists. skip. {}", structType);

            return;
        }

        PartyType parentType = this.partyTypeManager.findUniqueBy("ref",
                parentRef);

        if (parentType == null) {
            logger.info("parentType not exists. skip. {}", parentRef);

            return;
        }

        PartyType childType = this.partyTypeManager.findUniqueBy("ref",
                childRef);

        if (parentType == null) {
            logger.info("childType not exists. skip. {}", childRef);

            return;
        }

        partyStructRule = new PartyStructRule();
        partyStructRule.setPartyStructType(partyStructType);
        partyStructRule.setParentType(parentType);
        partyStructRule.setChildType(childType);
        partyStructRule.setTenantId(defaultTenantId);
        partyStructRuleManager.save(partyStructRule);
    }

    public void createPartyStructType(String type, String name, int priority,
            String display) {
        PartyStructType partyStructType = this.partyStructTypeManager
                .findUniqueBy("type", type);

        if (partyStructType != null) {
            logger.info("partyStructType exists. skip. {}", type);

            return;
        }

        partyStructType = new PartyStructType();
        partyStructType.setType(type);
        partyStructType.setName(name);
        partyStructType.setPriority(priority);
        partyStructType.setDisplay(display);
        partyStructType.setTenantId(defaultTenantId);
        this.partyStructTypeManager.save(partyStructType);
    }

    @Resource
    public void setPartystructTypeManager(
            PartyStructTypeManager partyStructTypeManager) {
        this.partyStructTypeManager = partyStructTypeManager;
    }

    @Resource
    public void setPartyTypeManager(PartyTypeManager partyTypeManager) {
        this.partyTypeManager = partyTypeManager;
    }

    @Resource
    public void setPartyStructRuleManager(
            PartyStructRuleManager partyStructRuleManager) {
        this.partyStructRuleManager = partyStructRuleManager;
    }
}
