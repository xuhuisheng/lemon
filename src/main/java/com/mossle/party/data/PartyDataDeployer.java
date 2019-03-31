package com.mossle.party.data;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.api.user.UserDTO;

import com.mossle.client.user.UserClient;

import com.mossle.core.csv.CsvProcessor;

import com.mossle.party.PartyConstants;
import com.mossle.party.persistence.domain.PartyEntity;
import com.mossle.party.persistence.domain.PartyStruct;
import com.mossle.party.persistence.domain.PartyStructRule;
import com.mossle.party.persistence.domain.PartyStructType;
import com.mossle.party.persistence.domain.PartyType;
import com.mossle.party.persistence.manager.PartyEntityManager;
import com.mossle.party.persistence.manager.PartyStructManager;
import com.mossle.party.persistence.manager.PartyStructRuleManager;
import com.mossle.party.persistence.manager.PartyStructTypeManager;
import com.mossle.party.persistence.manager.PartyTypeManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartyDataDeployer {
    private static Logger logger = LoggerFactory
            .getLogger(PartyDataDeployer.class);
    private PartyStructTypeManager partyStructTypeManager;
    private PartyTypeManager partyTypeManager;
    private PartyStructRuleManager partyStructRuleManager;
    private PartyEntityManager partyEntityManager;
    private PartyStructManager partyStructManager;
    private String defaultTenantId = "1";
    private String orgDataFilePath = "data/party-org.json";
    private String orgDataEncoding = "UTF-8";
    private String employeeDataFilePath = "data/party-user.csv";
    private String employeeDataEncoding = "GB2312";
    private String reportDataFilePath = "data/party-report.json";
    private String reportDataEncoding = "UTF-8";
    private List<EmployeeDTO> employeeDtos = new ArrayList<EmployeeDTO>();
    private OrgProcessor orgProcessor = new OrgProcessor();
    private OrgProcessor reportProcessor = new OrgProcessor();
    private UserClient userClient;

    public void init() throws Exception {
        // 解析employee.csv
        this.parseEmployee();

        // 解析org.json
        this.parseOrg();

        // 解析report.json
        this.parseReport();

        // 为user初始化partyEntity
        this.initUserPartyEntity();

        // 为org初始化partyEntity
        this.initOrgPartyEntity();

        // 为position初始化partyEntity
        this.initPositionPartyEntity();

        // 设置部门关系
        this.initDepartmentRelation();

        // 设置部门人员关系
        this.initDepartmentUser();

        // 设置部门管理关系
        this.initDepartmentAdmin();

        // 设置用户岗位关系
        this.initPositionUser();

        // 汇报线
        this.initReportLine();
    }

    public void parseEmployee() throws Exception {
        EmployeeCallback employeeCallback = new EmployeeCallback();
        new CsvProcessor().process(employeeDataFilePath, employeeDataEncoding,
                employeeCallback);
        employeeDtos.addAll(employeeCallback.getEmployeeDtos());
    }

    public void parseOrg() throws Exception {
        this.orgProcessor.init(orgDataFilePath, orgDataEncoding);
    }

    public void parseReport() throws Exception {
        this.reportProcessor.init(reportDataFilePath, reportDataEncoding);
    }

    public void initUserPartyEntity() {
        PartyType partyType = partyTypeManager.findUniqueBy("type",
                PartyConstants.TYPE_USER);

        if (partyType == null) {
            logger.info("cannot find partyType : {}", PartyConstants.TYPE_USER);

            return;
        }

        for (EmployeeDTO employeeDto : employeeDtos) {
            String username = employeeDto.getUsername();
            PartyEntity partyEntity = this.findUserPartyEntity(username);

            if (partyEntity != null) {
                logger.info("partyEntity exists. skip. {}", username);

                continue;
            }

            UserDTO userDto = userClient.findByUsername(username,
                    defaultTenantId);

            if (userDto == null) {
                logger.info("cannot find user : {}", username);

                continue;
            }

            partyEntity = new PartyEntity();
            partyEntity.setCode(userDto.getUsername());
            partyEntity.setName(userDto.getDisplayName());
            partyEntity.setRef(userDto.getId());
            partyEntity.setPartyType(partyType);
            partyEntity.setTenantId(defaultTenantId);
            partyEntityManager.save(partyEntity);
        }
    }

    public void initOrgPartyEntity() {
        for (OrgDTO orgDto : orgProcessor.getOrgDtos()) {
            PartyEntity partyEntity = this.findOrgPartyEntity(orgDto.getCode(),
                    orgDto.getType());

            if (partyEntity != null) {
                continue;
            }

            String typeHql = "from PartyType where ref=? and type=?";
            PartyType partyType = partyTypeManager.findUnique(typeHql,
                    orgDto.getType(), PartyConstants.TYPE_ORG);

            if (partyType == null) {
                logger.info("cannot find partyType : {}",
                        PartyConstants.TYPE_ORG);

                continue;
            }

            partyEntity = new PartyEntity();
            partyEntity.setCode(orgDto.getCode());
            partyEntity.setName(orgDto.getName());
            partyEntity.setRef(orgDto.getCode());
            partyEntity.setPartyType(partyType);
            partyEntity.setTenantId(defaultTenantId);
            partyEntityManager.save(partyEntity);
        }
    }

    public void initPositionPartyEntity() {
        PartyType partyType = partyTypeManager.findUniqueBy("type",
                PartyConstants.TYPE_POSITION);

        if (partyType == null) {
            logger.info("cannot find partyType : {}",
                    PartyConstants.TYPE_POSITION);

            return;
        }

        for (EmployeeDTO employeeDto : employeeDtos) {
            String position = employeeDto.getPosition();

            if (StringUtils.isBlank(position)) {
                continue;
            }

            String hql = "from PartyEntity where ref=? and partyType=?";
            PartyEntity partyEntity = this.partyEntityManager.findUnique(hql,
                    position, partyType);

            if (partyEntity != null) {
                logger.debug("partyEntity exists. skip. {}", position);

                continue;
            }

            partyEntity = new PartyEntity();
            partyEntity.setName(position);
            partyEntity.setRef(position);
            partyEntity.setPartyType(partyType);
            partyEntity.setTenantId(defaultTenantId);
            partyEntityManager.save(partyEntity);
        }
    }

    public void initDepartmentRelation() {
        // root
        PartyEntity rootPartyEntity = this.findOrgPartyEntity(orgProcessor
                .getRoot().getCode(), orgProcessor.getRoot().getType());

        if (rootPartyEntity != null) {
            this.createPartyStruct("struct", null, rootPartyEntity);
        } else {
            logger.info("cannot find root partyEntity : {} {}", orgProcessor
                    .getRoot().getCode(), orgProcessor.getRoot().getType());
        }

        // not root
        for (OrgDTO orgDto : orgProcessor.getOrgDtos()) {
            PartyEntity parentPartyEntity = this.findOrgPartyEntity(
                    orgDto.getCode(), orgDto.getType());

            if (parentPartyEntity == null) {
                logger.info("partyEntity not exists. skip. {} {}",
                        orgDto.getCode(), orgDto.getType());

                continue;
            }

            for (OrgDTO child : orgDto.getChildren()) {
                PartyEntity childPartyEntity = this.findOrgPartyEntity(
                        child.getCode(), child.getType());

                if (childPartyEntity == null) {
                    continue;
                }

                this.createPartyStruct("struct", parentPartyEntity,
                        childPartyEntity);
            }
        }
    }

    public void initDepartmentUser() {
        for (EmployeeDTO employeeDto : employeeDtos) {
            String username = employeeDto.getUsername();
            PartyEntity userPartyEntity = this.findUserPartyEntity(username);

            if (userPartyEntity == null) {
                logger.info("partyEntity not exists. skip. {}", username);

                continue;
            }

            String department = employeeDto.getDepartment();
            OrgDTO orgDto = orgProcessor.findByCode(department);

            if (orgDto == null) {
                logger.info("cannot find orgDto : {}", department);

                continue;
            }

            PartyEntity orgPartyEntity = this.findOrgPartyEntity(
                    orgDto.getCode(), orgDto.getType());

            if (orgPartyEntity == null) {
                logger.info("partyEntity not exists. skip. {} {}",
                        orgDto.getCode(), orgDto.getType());

                continue;
            }

            this.createPartyStruct("struct", orgPartyEntity, userPartyEntity);
        }
    }

    public void initDepartmentAdmin() {
        for (OrgDTO orgDto : orgProcessor.getOrgDtos()) {
            if (orgDto.getLeader() == null) {
                continue;
            }

            PartyEntity orgPartyEntity = this.findOrgPartyEntity(
                    orgDto.getCode(), orgDto.getType());

            if (orgPartyEntity == null) {
                logger.info("partyEntity not exists. skip. {} {}",
                        orgDto.getCode(), orgDto.getType());

                continue;
            }

            PartyEntity userPartyEntity = this.findUserPartyEntity(orgDto
                    .getLeader());

            if (userPartyEntity == null) {
                logger.info("partyEntity not exists. skip. {}",
                        orgDto.getLeader());

                continue;
            }

            this.createPartyStructAdmin(orgPartyEntity, userPartyEntity);
        }
    }

    public void initPositionUser() {
        PartyType partyType = partyTypeManager.findUniqueBy("type",
                PartyConstants.TYPE_POSITION);

        if (partyType == null) {
            logger.info("cannot find partyType : {}",
                    PartyConstants.TYPE_POSITION);

            return;
        }

        for (EmployeeDTO employeeDto : employeeDtos) {
            String username = employeeDto.getUsername();
            String department = employeeDto.getDepartment();
            String position = employeeDto.getPosition();

            OrgDTO orgDto = orgProcessor.findByCode(department);

            PartyEntity userPartyEntity = this.findUserPartyEntity(username);

            if (userPartyEntity == null) {
                logger.info("partyEntity not exists. skip. {}", username);

                continue;
            }

            PartyEntity orgPartyEntity = this.findOrgPartyEntity(
                    orgDto.getCode(), orgDto.getType());

            if (orgPartyEntity == null) {
                logger.info("partyEntity not exists. skip. {} {}",
                        orgDto.getCode(), orgDto.getType());

                continue;
            }

            PartyEntity positionPartyEntity = this
                    .findPositionPartyEntity(position);

            if (positionPartyEntity == null) {
                logger.debug("partyEntity not exists. skip. {}", position);

                continue;
            }

            // user position
            this.createPartyStruct("department-position", orgPartyEntity,
                    positionPartyEntity);
            this.createPartyStruct("user-position", userPartyEntity,
                    positionPartyEntity);
        }
    }

    public void initReportLine() {
        PartyType partyType = partyTypeManager.findUniqueBy("type",
                PartyConstants.TYPE_USER);

        if (partyType == null) {
            logger.info("cannot find partyType : {}", PartyConstants.TYPE_USER);

            return;
        }

        for (OrgDTO reportDto : reportProcessor.getOrgDtos()) {
            try {
                PartyEntity child = this.findUserPartyEntity(reportDto
                        .getCode());

                if (child == null) {
                    logger.info("partyEntity not exists. skip. {}",
                            reportDto.getCode());

                    continue;
                }

                PartyEntity parent = this.findUserPartyEntity(reportDto
                        .getParentCode());

                if (parent == null) {
                    logger.info("partyEntity not exists. skip. {}",
                            reportDto.getParentCode());

                    continue;
                }

                this.createPartyStruct("report", parent, child);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    // ~
    public PartyEntity findOrgPartyEntity(String code, String type) {
        String typeHql = "from PartyType where ref=? and type=?";
        PartyType partyType = partyTypeManager.findUnique(typeHql, type,
                PartyConstants.TYPE_ORG);

        if (partyType == null) {
            logger.info("cannot find partyType : {}", PartyConstants.TYPE_ORG);

            return null;
        }

        String hql = "from PartyEntity where ref=? and partyType=?";
        PartyEntity partyEntity = partyEntityManager.findUnique(hql, code,
                partyType);

        if (partyEntity == null) {
            logger.debug("partyEntity not exists. skip. {} {}", code,
                    partyType.getName());

            return null;
        }

        return partyEntity;
    }

    public PartyEntity findUserPartyEntity(String username) {
        if (username == null) {
            logger.info("require username : {}", username);

            return null;
        }

        PartyType partyType = partyTypeManager.findUniqueBy("type",
                PartyConstants.TYPE_USER);

        if (partyType == null) {
            logger.info("cannot find partyType : {}", PartyConstants.TYPE_USER);

            return null;
        }

        UserDTO userDto = userClient.findByUsername(username, defaultTenantId);

        if (userDto == null) {
            logger.info("cannot find user : {}", username);

            return null;
        }

        String hql = "from PartyEntity where ref=? and partyType=?";
        PartyEntity partyEntity = partyEntityManager.findUnique(hql,
                userDto.getId(), partyType);

        if (partyEntity == null) {
            logger.debug("partyEntity not exists. skip. {}", username);

            return null;
        }

        return partyEntity;
    }

    public PartyEntity findPositionPartyEntity(String positionName) {
        if (StringUtils.isBlank(positionName)) {
            logger.debug("position is blank");

            return null;
        }

        PartyType partyType = partyTypeManager.findUniqueBy("type",
                PartyConstants.TYPE_POSITION);

        if (partyType == null) {
            logger.info("cannot find partyType : {}",
                    PartyConstants.TYPE_POSITION);

            return null;
        }

        String hql = "from PartyEntity where ref=? and partyType=?";
        PartyEntity partyEntity = partyEntityManager.findUnique(hql,
                positionName, partyType);

        if (partyEntity == null) {
            logger.debug("partyEntity not exists. skip. {}", positionName);

            return null;
        }

        return partyEntity;
    }

    public void createPartyStruct(String type, PartyEntity parentPartyEntity,
            PartyEntity childPartyEntity) {
        PartyStructType partyStructType = this.partyStructTypeManager
                .findUniqueBy("type", type);

        if (partyStructType == null) {
            logger.info("partyStructType not exists. skip. {}", type);

            return;
        }

        PartyStruct partyStruct = null;

        if (parentPartyEntity == null) {
            String hql = "from PartyStruct where partyStructType=? and parentEntity is null and childEntity=?";
            partyStruct = this.partyStructManager.findUnique(hql,
                    partyStructType, childPartyEntity);
        } else {
            String hql = "from PartyStruct where partyStructType=? and parentEntity=? and childEntity=?";
            partyStruct = this.partyStructManager.findUnique(hql,
                    partyStructType, parentPartyEntity, childPartyEntity);
        }

        if (partyStruct != null) {
            if (parentPartyEntity == null) {
                logger.info("partyStruct not exists. skip. {} {} {}", type,
                        "null", childPartyEntity.getName());
            } else {
                logger.info("partyStruct not exists. skip. {} {} {}", type,
                        parentPartyEntity.getName(), childPartyEntity.getName());
            }

            return;
        }

        partyStruct = new PartyStruct();
        partyStruct.setPartyStructType(partyStructType);
        partyStruct.setParentEntity(parentPartyEntity);
        partyStruct.setChildEntity(childPartyEntity);
        partyStruct.setTenantId(defaultTenantId);
        this.partyStructManager.save(partyStruct);
    }

    public void createPartyStructAdmin(PartyEntity parentPartyEntity,
            PartyEntity childPartyEntity) {
        PartyStructType partyStructType = this.partyStructTypeManager
                .findUniqueBy("type", "manage");

        if (partyStructType == null) {
            logger.info("partyStructType not exists. skip. {}", "manage");

            return;
        }

        String hql = "from PartyStruct where partyStructType=? and parentEntity=? and childEntity=?";
        PartyStruct partyStruct = this.partyStructManager.findUnique(hql,
                partyStructType, parentPartyEntity, childPartyEntity);

        if (partyStruct != null) {
            if (!Integer.valueOf(1).equals(partyStruct.getAdmin())) {
                partyStruct.setAdmin(1);
                this.partyStructManager.save(partyStruct);
            }

            return;
        }

        partyStruct = new PartyStruct();
        partyStruct.setPartyStructType(partyStructType);
        partyStruct.setParentEntity(parentPartyEntity);
        partyStruct.setChildEntity(childPartyEntity);
        partyStruct.setTenantId(defaultTenantId);
        partyStruct.setAdmin(1);
        this.partyStructManager.save(partyStruct);
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

    @Resource
    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }

    @Resource
    public void setPartyStructManager(PartyStructManager partyStructManager) {
        this.partyStructManager = partyStructManager;
    }

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }
}
