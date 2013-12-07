package com.mossle.acl.service;

import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.acl.domain.AclEntry;
import com.mossle.acl.domain.AclObjectIdentity;
import com.mossle.acl.domain.AclObjectType;
import com.mossle.acl.manager.AclEntryManager;
import com.mossle.acl.manager.AclObjectIdentityManager;
import com.mossle.acl.manager.AclObjectTypeManager;

import com.mossle.api.UserConnector;
import com.mossle.api.UserDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Component;

@Component
public class AclService {
    private Logger logger = LoggerFactory.getLogger(AclService.class);
    private UserConnector userConnector;
    private AclEntryManager aclEntryManager;
    private AclObjectIdentityManager aclObjectIdentityManager;
    private AclObjectTypeManager aclObjectTypeManager;
    private JdbcTemplate jdbcTemplate;

    public UserDTO getUserDTO(String reference) {
        return userConnector.findById(reference);
    }

    public AclObjectIdentity createOrGetAclObjectIdentity(String reference,
            String objectTypeCode) {
        AclObjectIdentity aclObjectIdentity = findAclObjectIdentity(reference,
                objectTypeCode);

        if (aclObjectIdentity == null) {
            aclObjectIdentity = new AclObjectIdentity();
            aclObjectIdentity.setReference(reference);
            aclObjectIdentity
                    .setAclObjectType(createOrGetAclObjectType(objectTypeCode));
            aclObjectIdentityManager.save(aclObjectIdentity);
        }

        return aclObjectIdentity;
    }

    public AclObjectIdentity findAclObjectIdentity(String reference,
            String objectTypeCode) {
        return aclObjectIdentityManager
                .findUnique(
                        "from AclObjectIdentity where reference=? and aclObjectType.code=?",
                        reference, objectTypeCode);
    }

    public AclObjectType createOrGetAclObjectType(String code) {
        AclObjectType aclObjectType = aclObjectTypeManager.findUniqueBy("code",
                code);

        if (aclObjectType == null) {
            aclObjectType = new AclObjectType();
            aclObjectType.setCode(code);
            aclObjectTypeManager.save(aclObjectType);
        }

        return aclObjectType;
    }

    public AclEntry findAclEntry(String userReference,
            long aclObjectIdentityId, int mask) {
        String hql = "select ace "
                + " from AclEntry ace,AclSid sid,AclObjectIdentity oid,PartyEntity u,PartyStruct ps,PartyEntity entity "
                + " where u.reference=? " + " and ps.childEntity=u "
                + " and ps.parentEntity=entity " + " and entity.id=ace.sidId "
                + " and ace.aclObjectIdentity.id=? " + " and ace.mask=?";

        return aclEntryManager.findUnique(hql, userReference,
                aclObjectIdentityId, mask);
    }

    public AclObjectIdentity createOrFindAclObjectIdentity(String reference,
            String objectTypeCode) {
        AclObjectIdentity aclObjectIdentity = findAclObjectIdentity(reference,
                objectTypeCode);

        if (aclObjectIdentity == null) {
            AclObjectType aclObjectType = aclObjectTypeManager.findUniqueBy(
                    "code", objectTypeCode);

            if (aclObjectType == null) {
                aclObjectType = new AclObjectType();
                aclObjectType.setCode(objectTypeCode);
                aclObjectType.setName(objectTypeCode);
                aclObjectTypeManager.save(aclObjectType);
            }

            aclObjectIdentity = new AclObjectIdentity();
            aclObjectIdentity.setReference(reference);
            aclObjectIdentity.setAclObjectType(aclObjectType);
            aclObjectIdentityManager.save(aclObjectIdentity);
        }

        return aclObjectIdentity;
    }

    public AclEntry createOrFindAclEntry(Long sidId,
            AclObjectIdentity aclObjectIdentity, int mask) {
        return this.createOrFindAclEntry(sidId, aclObjectIdentity, mask, null,
                null);
    }

    public AclEntry createOrFindAclEntry(Long sidId,
            AclObjectIdentity aclObjectIdentity, int mask, String startTime,
            String endTime) {
        AclEntry aclEntry = findAclEntry(sidId, aclObjectIdentity, mask);

        if (aclEntry == null) {
            aclEntry = new AclEntry();
            aclEntry.setSidId(sidId);
            aclEntry.setAclObjectIdentity(aclObjectIdentity);
            aclEntry.setMask(mask);

            try {
                if (startTime != null) {
                    aclEntry.setStartTime(new SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss").parse(startTime));
                }
            } catch (Exception ex) {
                logger.warn(ex.getMessage(), ex);
            }

            try {
                if (endTime != null) {
                    aclEntry.setEndTime(new SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss").parse(endTime));
                }
            } catch (Exception ex) {
                logger.warn(ex.getMessage(), ex);
            }

            aclEntryManager.save(aclEntry);
        }

        return aclEntry;
    }

    public AclEntry findAclEntry(Long sidId,
            AclObjectIdentity aclObjectIdentity, int mask) {
        return aclEntryManager
                .findUnique(
                        "from AclEntry where sidId=? and aclObjectIdentity=? and mask=?",
                        sidId, aclObjectIdentity, mask);
    }

    public void saveAclEntry(AclEntry aclEntry) {
        aclEntryManager.save(aclEntry);
    }

    public void removeAclEntry(AclEntry aclEntry) {
        aclEntryManager.remove(aclEntry);
    }

    public void removeAclEntry(Long aclEntryId) {
        aclEntryManager.removeById(aclEntryId);
    }

    public Long getSidId(String entityId, String entityType) {
        String sql = "select pe.id from party_entity pe where pe.type_id=? and pe.reference=?";

        return jdbcTemplate.queryForObject(sql, Long.class, entityType,
                entityId);
    }

    public Map getSidInfo(Long sidId) {
        String sql = "select id,name,type_id from party_entity where id=?";
        Map<String, Object> map = jdbcTemplate.queryForMap(sql, sidId);
        Map sidInfo = new HashMap();
        sidInfo.put("typeId",
                (map.get("type_id") == null) ? null : map.get("type_id")
                        .toString());
        sidInfo.put("id", map.get("id"));
        sidInfo.put("name", map.get("name"));

        return sidInfo;
    }

    public Long findUserIdByUsername(String username, long userType) {
        UserDTO userDto = userConnector.findByUsername(username, userType);

        return getSidId(userDto.getId(), "1");
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setAclEntryManager(AclEntryManager aclEntryManager) {
        this.aclEntryManager = aclEntryManager;
    }

    @Resource
    public void setAclObjectIdentityManager(
            AclObjectIdentityManager aclObjectIdentityManager) {
        this.aclObjectIdentityManager = aclObjectIdentityManager;
    }

    @Resource
    public void setAclObjectTypeManager(
            AclObjectTypeManager aclObjectTypeManager) {
        this.aclObjectTypeManager = aclObjectTypeManager;
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
