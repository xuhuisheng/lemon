package com.mossle.acl.rs;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mossle.acl.domain.AclEntry;
import com.mossle.acl.domain.AclObjectIdentity;
import com.mossle.acl.domain.AclObjectType;
import com.mossle.acl.domain.AclSid;
import com.mossle.acl.service.AclService;

import com.mossle.api.UserDTO;

import com.mossle.core.util.BaseDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("acl")
public class AclResource {
    private static Logger logger = LoggerFactory.getLogger(AclResource.class);
    private AclService aclService;

    /**
     * 判断某个用户对某个资源是否拥有权限
     */
    @Path("check")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO checkAccess(@QueryParam("userId") long userId,
            @QueryParam("resourceId") long resourceId,
            @QueryParam("resourceType") String resourceType,
            @QueryParam("mask") int mask) {
        UserDTO userDto = aclService.getUserDTO("" + userId);

        if (userDto == null) {
            logger.info("user [{}] is null", userId);

            BaseDTO baseDto = new BaseDTO();
            baseDto.setCode(200);
            baseDto.setData(false);

            return baseDto;
        }

        AclObjectIdentity aclObjectIdentity = aclService.findAclObjectIdentity(
                "" + resourceId, resourceType);

        if (aclObjectIdentity == null) {
            logger.info("object identity [{},{}] is null", resourceId,
                    resourceType);

            BaseDTO baseDto = new BaseDTO();
            baseDto.setCode(200);
            baseDto.setData(false);

            return baseDto;
        }

        AclEntry aclEntry = aclService.findAclEntry(userDto.getId(),
                aclObjectIdentity.getId(), mask);
        logger.debug("aclEntry : {}", aclEntry);

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);

        boolean canAccess = false;

        if ((aclEntry != null)
                && this.isTimeValid(aclEntry.getStartTime(), aclEntry
                        .getEndTime())) {
            canAccess = Integer.valueOf(1).equals(aclEntry.getGranting());
        }

        baseDto.setData(canAccess);

        return baseDto;
    }

    /**
     * 给一个资源赋予某个sid实体的权限
     */
    @Path("grant")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO grantAccess(@FormParam("entityId") String entityId,
            @FormParam("entityType") String entityType,
            @FormParam("resourceId") long resourceId,
            @FormParam("resourceType") String resourceType,
            @FormParam("mask") int mask) {
        Long sidId = aclService.getSidId(entityId, entityType);
        AclObjectIdentity aclObjectIdentity = aclService
                .createOrFindAclObjectIdentity("" + resourceId, resourceType);

        AclEntry aclEntry = aclService.createOrFindAclEntry(sidId,
                aclObjectIdentity, mask);

        aclEntry.setGranting(1);
        aclService.saveAclEntry(aclEntry);

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);

        return baseDto;
    }

    /**
     * 给一个资源收回某个sid实体的权限
     */
    @Path("revoke")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO revokeAccess(@FormParam("entityId") String entityId,
            @FormParam("entityType") String entityType,
            @FormParam("resourceId") long resourceId,
            @FormParam("resourceType") String resourceType,
            @FormParam("mask") int mask) {
        Long sidId = aclService.getSidId(entityId, entityType);
        AclObjectIdentity aclObjectIdentity = aclService.findAclObjectIdentity(
                "" + resourceId, resourceType);

        if (aclObjectIdentity == null) {
            logger.info("object identity [{},{}] is null", resourceId,
                    resourceType);

            BaseDTO baseDto = new BaseDTO();
            baseDto.setCode(200);

            return baseDto;
        }

        AclEntry aclEntry = aclService.findAclEntry(sidId, aclObjectIdentity,
                mask);

        if (aclEntry != null) {
            aclService.removeAclEntry(aclEntry);
        }

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);

        return baseDto;
    }

    /**
     * 批量为某个资源批量设置多个sid的权限
     */
    @Path("updateAccess")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO updateAccess(@FormParam("resourceId") String resourceId,
            @FormParam("resourceType") String resourceType,
            @FormParam("mask") int mask,
            @FormParam("entityIds") String entityIds) {
        AclObjectIdentity aclObjectIdentity = aclService
                .createOrGetAclObjectIdentity(resourceId, resourceType);

        List<Long> aceIds = new ArrayList<Long>();

        for (AclEntry aclEntry : aclObjectIdentity.getAclEntries()) {
            if ((aclEntry.getMask() == null) || (aclEntry.getMask() == mask)) {
                aceIds.add(aclEntry.getId());
            }
        }

        List<Long> entityIdList = new ArrayList<Long>();

        if (entityIds != null) {
            for (String entityId : entityIds.split(",")) {
                if (entityId.equals("")) {
                    continue;
                }

                try {
                    entityIdList.add(Long.parseLong(entityId));
                } catch (Exception ex) {
                    logger.info(ex.toString());
                }
            }
        }

        for (Long entityId : entityIdList) {
            AclEntry aclEntry = aclService.createOrFindAclEntry(entityId,
                    aclObjectIdentity, mask);

            aclEntry.setGranting(1);
            aclService.saveAclEntry(aclEntry);
            aceIds.remove(aclEntry.getId());
        }

        for (Long aceId : aceIds) {
            aclService.removeAclEntry(aceId);
        }

        // aclObjectIdentity = aclService.findAclObjectIdentity(resourceId,
        // resourceType);

        // 操作完成后，返回当前设置的资源是否设置了数据权限
        // 有则返回true
        // 无则返回false
        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(!entityIdList.isEmpty());

        return baseDto;
    }

    /**
     * 根据给定的userId设置权限
     */
    @Path("updateUserAccess")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO updateUserAccess(@FormParam("resourceId") String resourceId,
            @FormParam("resourceType") String resourceType,
            @FormParam("mask") int mask,
            @FormParam("entityIds") String entityIds) {
        AclObjectIdentity aclObjectIdentity = aclService
                .createOrGetAclObjectIdentity(resourceId, resourceType);

        List<Long> aceIds = new ArrayList<Long>();

        for (AclEntry aclEntry : aclObjectIdentity.getAclEntries()) {
            if ((aclEntry.getMask() == null) || (aclEntry.getMask() == mask)) {
                aceIds.add(aclEntry.getId());
            }
        }

        List<Long> entityIdList = new ArrayList<Long>();

        if (entityIds != null) {
            for (String entityId : entityIds.split(",")) {
                if (entityId.equals("")) {
                    continue;
                }

                try {
                    Long userId = aclService.getSidId(entityId, "1");
                    entityIdList.add(userId);
                } catch (Exception ex) {
                    logger.info(ex.toString());
                }
            }
        }

        for (Long entityId : entityIdList) {
            AclEntry aclEntry = aclService.createOrFindAclEntry(entityId,
                    aclObjectIdentity, mask);

            aclEntry.setGranting(1);
            aclService.saveAclEntry(aclEntry);
            aceIds.remove(aclEntry.getId());
        }

        for (Long aceId : aceIds) {
            aclService.removeAclEntry(aceId);
        }

        // aclObjectIdentity = aclService.findAclObjectIdentity(resourceId,
        // resourceType);

        // 操作完成后，返回当前设置的资源是否设置了数据权限
        // 有则返回true
        // 无则返回false
        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(!entityIdList.isEmpty());

        return baseDto;
    }

    /**
     * 根据给定的userId添加权限
     */
    @Path("addUserAccess")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO addUserAccess(@FormParam("resourceId") String resourceId,
            @FormParam("resourceType") String resourceType,
            @FormParam("mask") int mask,
            @FormParam("entityIds") String entityIds,
            @FormParam("startTime") String startTime,
            @FormParam("endTime") String endTime) {
        logger.info("resourceId : {}", resourceId);
        logger.info("resourceType : {}", resourceType);
        logger.info("mask : {}", mask);
        logger.info("entityIds : {}", entityIds);
        logger.info("startTime : {}", startTime);
        logger.info("endTime : {}", endTime);

        AclObjectIdentity aclObjectIdentity = aclService
                .createOrGetAclObjectIdentity(resourceId, resourceType);
        logger.info("aclObjectIdentity : {}", aclObjectIdentity.getId());

        List<Long> entityIdList = new ArrayList<Long>();

        if (entityIds != null) {
            for (String entityId : entityIds.split(",")) {
                if (entityId.equals("")) {
                    continue;
                }

                try {
                    Long userId = aclService.getSidId(entityId, "1");
                    entityIdList.add(userId);
                } catch (Exception ex) {
                    logger.info(ex.toString());
                }
            }
        }

        logger.info("entityIdList : {}", entityIdList);

        for (Long entityId : entityIdList) {
            logger.info("entityId : {}", entityId);

            AclEntry aclEntry = aclService.createOrFindAclEntry(entityId,
                    aclObjectIdentity, mask, startTime, endTime);

            aclEntry.setGranting(1);

            if ("".equals(startTime)) {
                startTime = null;
            }

            if ("".equals(endTime)) {
                endTime = null;
            }

            aclEntry.setStartTime(null);
            aclEntry.setEndTime(null);

            try {
                if (startTime != null) {
                    aclEntry.setStartTime(new SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss").parse(startTime));
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            logger.info("startTime : {}", aclEntry.getStartTime());

            try {
                if (endTime != null) {
                    aclEntry.setEndTime(new SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss").parse(endTime));
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            logger.info("endTime : {}", aclEntry.getEndTime());

            aclService.saveAclEntry(aclEntry);
            logger.info("aclEntry : {}", aclEntry.getId());
        }

        // 操作完成后，返回当前设置的资源是否设置了数据权限
        // 有则返回true
        // 无则返回false
        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(!entityIdList.isEmpty());

        return baseDto;
    }

    /**
     * 根据给定的userId删除权限
     */
    @Path("removeUserAccess")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO removeUserAccess(@FormParam("resourceId") String resourceId,
            @FormParam("resourceType") String resourceType,
            @FormParam("mask") int mask,
            @FormParam("entityIds") String entityIds) {
        AclObjectIdentity aclObjectIdentity = aclService
                .createOrGetAclObjectIdentity(resourceId, resourceType);

        List<Long> aceIds = new ArrayList<Long>();

        for (AclEntry aclEntry : aclObjectIdentity.getAclEntries()) {
            aceIds.add(aclEntry.getId());
        }

        List<Long> entityIdList = new ArrayList<Long>();

        if (entityIds != null) {
            for (String entityId : entityIds.split(",")) {
                if (entityId.equals("")) {
                    continue;
                }

                try {
                    Long userId = aclService.getSidId(entityId, "1");
                    entityIdList.add(userId);
                } catch (Exception ex) {
                    logger.info(ex.toString());
                }
            }
        }

        // for (Long entityId : entityIdList) {
        // AclEntry aclEntry = aclService.createOrFindAclEntry(entityId,
        // aclObjectIdentity, mask);
        // aclEntry.setGranting(1);
        // aclService.saveAclEntry(aclEntry);
        // aceIds.remove(aclEntry.getId());
        // }

        // for (Long aceId : aceIds) {
        // aclService.removeAclEntry(aceId);
        // }
        for (Long entityId : entityIdList) {
            AclEntry aclEntry = aclService.createOrFindAclEntry(entityId,
                    aclObjectIdentity, mask);
            aclService.removeAclEntry(aclEntry.getId());
        }

        // aclObjectIdentity = aclService.findAclObjectIdentity(resourceId,
        // resourceType);

        // 操作完成后，返回当前设置的资源是否设置了数据权限
        // 有则返回true
        // 无则返回false
        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(!entityIdList.isEmpty());

        return baseDto;
    }

    /**
     * 获得对某个资源拥有权限的sid列表
     */
    @Path("getAccess")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO getAccess(@QueryParam("resourceId") String resourceId,
            @QueryParam("resourceType") String resourceType,
            @QueryParam("mask") int mask) {
        AclObjectIdentity aclObjectIdentity = aclService.findAclObjectIdentity(
                resourceId, resourceType);

        if (aclObjectIdentity == null) {
            logger.info("object identity [{},{}] is null", resourceId,
                    resourceType);

            BaseDTO baseDto = new BaseDTO();
            baseDto.setCode(200);

            return baseDto;
        }

        List departments = new ArrayList();
        List emailgroups = new ArrayList();
        List users = new ArrayList();

        for (AclEntry aclEntry : aclObjectIdentity.getAclEntries()) {
            Map sidInfo = aclService.getSidInfo(aclEntry.getSidId());

            if ("1".equals(sidInfo.get("typeId"))) {
                sidInfo.remove("typeId");
                users.add(sidInfo);
            } else if ("3".equals(sidInfo.get("typeId"))) {
                sidInfo.remove("typeId");
                departments.add(sidInfo);
            } else if ("5".equals(sidInfo.get("typeId"))) {
                sidInfo.remove("typeId");
                emailgroups.add(sidInfo);
            }
        }

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);

        Map data = new HashMap();
        baseDto.setData(data);
        data.put("department", departments);
        data.put("emailgroup", emailgroups);
        data.put("user", users);

        return baseDto;
    }

    protected boolean isTimeValid(Date startTime, Date endTime) {
        logger.debug("startTime : {}, endTime : {}", startTime, endTime);

        if ((startTime == null) && (endTime == null)) {
            return true;
        }

        long now = System.currentTimeMillis();

        if (startTime == null) {
            return now < endTime.getTime();
        }

        if (endTime == null) {
            return now > startTime.getTime();
        }

        return (now < endTime.getTime()) && (now > startTime.getTime());
    }

    @Resource
    public void setAclService(AclService aclService) {
        this.aclService = aclService;
    }
}
