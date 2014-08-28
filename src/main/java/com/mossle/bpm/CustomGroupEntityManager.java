package com.mossle.bpm;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.org.OrgConnector;
import com.mossle.api.org.OrgDTO;

import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.GroupEntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomGroupEntityManager extends GroupEntityManager {
    private static Logger logger = LoggerFactory
            .getLogger(CustomGroupEntityManager.class);
    private OrgConnector orgConnector;

    @Override
    public List<Group> findGroupsByUser(String userId) {
        logger.debug("findGroupsByUser : {}", userId);

        List<Group> groups = new ArrayList<Group>();

        for (OrgDTO orgDto : orgConnector.getOrgsByUserId(userId)) {
            GroupEntity groupEntity = new GroupEntity(orgDto.getId());
            groupEntity.setName(orgDto.getName());
            groupEntity.setType(orgDto.getTypeName());
            groups.add(groupEntity);
        }

        return groups;
    }

    @Resource
    public void setOrgConnector(OrgConnector orgConnector) {
        this.orgConnector = orgConnector;
    }
}
