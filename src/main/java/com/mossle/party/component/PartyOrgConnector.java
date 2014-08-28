package com.mossle.party.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.org.OrgConnector;
import com.mossle.api.org.OrgDTO;

import com.mossle.party.domain.PartyEntity;
import com.mossle.party.domain.PartyStruct;
import com.mossle.party.manager.PartyEntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
public class PartyOrgConnector implements OrgConnector {
    private static Logger logger = LoggerFactory
            .getLogger(PartyOrgConnector.class);
    public static final int TYPE_ORG = 0;
    public static final int TYPE_USER = 1;
    public static final int TYPE_POSITION = 2;
    private PartyEntityManager partyEntityManager;

    public int getJobLevelByUserId(String userId) {
        PartyEntity partyEntity = partyEntityManager.findUnique(
                "from PartyEntity where partyType.type=? and ref=?", TYPE_USER,
                userId);

        for (PartyStruct partyStruct : partyEntity.getParentStructs()) {
            if (partyStruct.getParentEntity().getPartyType().getType() == TYPE_POSITION) {
                return partyStruct.getParentEntity().getLevel();
            }
        }

        return -1;
    }

    public int getJobLevelByInitiatorAndPosition(String userId,
            String positionName) {
        String hql = "from PartyEntity where partyType.type=? and name=?";
        PartyEntity partyEntity = partyEntityManager.findUnique(hql,
                TYPE_POSITION, positionName);

        return partyEntity.getLevel();
    }

    public String getSuperiorId(String userId) {
        logger.debug("user id : {}", userId);

        PartyEntity partyEntity = partyEntityManager.findUnique(
                "from PartyEntity where partyType.type=? and ref=?", TYPE_USER,
                userId);
        logger.debug("party entity : {}, {}", partyEntity.getId(),
                partyEntity.getName());

        PartyEntity superior = this.findSuperior(partyEntity);

        if (superior == null) {
            return null;
        }

        return superior.getRef();
    }

    public List<String> getPositionUserIds(String userId, String positionName) {
        PartyEntity partyEntity = partyEntityManager.findUnique(
                "from PartyEntity where partyType.type=? and ref=?", TYPE_USER,
                userId);

        return this.findPositionUserIds(partyEntity, positionName);
    }

    public List<OrgDTO> getOrgsByUserId(String userId) {
        PartyEntity partyEntity = partyEntityManager.findUnique(
                "from PartyEntity where partyType.type=? and ref=?", TYPE_USER,
                userId);

        if (partyEntity == null) {
            return Collections.emptyList();
        }

        List<OrgDTO> orgDtos = new ArrayList<OrgDTO>();

        for (PartyStruct partyStruct : partyEntity.getParentStructs()) {
            PartyEntity parent = partyStruct.getParentEntity();

            if (parent.getPartyType().getType() == TYPE_ORG) {
                OrgDTO orgDto = new OrgDTO();
                orgDto.setId(Long.toString(parent.getId()));
                orgDto.setName(parent.getName());
                orgDto.setTypeName(parent.getPartyType().getName());
                orgDto.setType(parent.getPartyType().getType());
                orgDto.setRef(parent.getRef());
                orgDtos.add(orgDto);
            }
        }

        return orgDtos;
    }

    // ~ ==================================================
    public PartyEntity findSuperior(PartyEntity child) {
        PartyEntity partyEntity = this.findUpperDepartment(child);

        while (partyEntity != null) {
            logger.debug("partyEntity : {}, {}", partyEntity.getId(),
                    partyEntity.getName());

            for (PartyStruct partyStruct : partyEntity.getChildStructs()) {
                PartyEntity childPartyEntity = partyStruct.getChildEntity();
                logger.debug("child : {}, {}", childPartyEntity.getId(),
                        childPartyEntity.getName());

                PartyEntity superior = null;

                if (childPartyEntity.getPartyType().getType() == TYPE_POSITION) {
                    superior = this.findAdministrator(childPartyEntity);
                } else if ((childPartyEntity.getPartyType().getType() == TYPE_USER)
                        && (partyStruct.getAdmin() == 1)) {
                    superior = childPartyEntity;
                }

                if (superior != null) {
                    return superior;
                }
            }

            partyEntity = this.findUpperDepartment(partyEntity);
        }

        return null;
    }

    public PartyEntity findAdministrator(PartyEntity parent) {
        for (PartyStruct partyStruct : parent.getChildStructs()) {
            PartyEntity partyEntity = null;
            PartyEntity child = partyStruct.getChildEntity();
            logger.debug("child : {}, {}", child.getId(), child.getName());

            if (child.getPartyType().getType() == TYPE_POSITION) {
                partyEntity = this.findAdministrator(parent);
            } else if ((child.getPartyType().getType() == TYPE_USER)
                    && (partyStruct.getAdmin() == 1)) {
                partyEntity = child;
            }

            if (partyEntity != null) {
                return partyEntity;
            }
        }

        return null;
    }

    public List<String> findPositionUserIds(PartyEntity parent,
            String positionName) {
        List<String> userIds = new ArrayList<String>();
        PartyEntity partyEntity = this.findUpperDepartment(parent);

        while (partyEntity != null) {
            if (partyEntity.getPartyType().getType() == TYPE_ORG) {
                for (PartyStruct partyStruct : partyEntity.getChildStructs()) {
                    PartyEntity child = partyStruct.getChildEntity();

                    if ((child.getPartyType().getType() == TYPE_POSITION)
                            && child.getName().equals(positionName)) {
                        for (PartyStruct ps : child.getChildStructs()) {
                            userIds.add(ps.getChildEntity().getRef());
                        }
                    }
                }
            } else if ((parent.getPartyType().getType() == TYPE_POSITION)
                    && parent.getName().equals(positionName)) {
                for (PartyStruct partyStruct : parent.getChildStructs()) {
                    PartyEntity child = partyStruct.getChildEntity();

                    if (child.getPartyType().getType() == TYPE_USER) {
                        userIds.add(child.getRef());
                    }
                }
            }

            if (userIds.isEmpty()) {
                partyEntity = this.findUpperDepartment(partyEntity);
            } else {
                break;
            }
        }

        return userIds;
    }

    public PartyEntity findUpperDepartment(PartyEntity child) {
        for (PartyStruct partyStruct : child.getParentStructs()) {
            PartyEntity parent = partyStruct.getParentEntity();

            if (parent == null) {
                continue;
            }

            if (parent.getPartyType().getType() == TYPE_ORG) {
                return parent;
            } else {
                return this.findUpperDepartment(parent);
            }
        }

        return null;
    }

    // ~ ==================================================
    @Resource
    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }
}
