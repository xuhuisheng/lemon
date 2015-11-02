package com.mossle.audit.rs;

import java.text.SimpleDateFormat;

import java.util.Date;

import javax.annotation.Resource;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.mossle.api.audit.AuditDTO;

import com.mossle.audit.component.AuditQueue;

import com.mossle.core.util.BaseDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("audit")
public class AuditResource {
    private static Logger logger = LoggerFactory.getLogger(AuditResource.class);
    private AuditQueue auditQueue;

    @POST
    public BaseDTO log(@FormParam("user") String user,
            @FormParam("resourceType") String resourceType,
            @FormParam("resourceId") String resourceId,
            @FormParam("action") String action,
            @FormParam("result") String result,
            @FormParam("application") String application,
            @FormParam("auditTime") String auditTime,
            @FormParam("client") String client,
            @FormParam("server") String server,
            @FormParam("description") String description) {
        Date date = new Date();

        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S")
                    .parse(auditTime);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        AuditDTO auditDto = new AuditDTO();
        auditDto.setUser(user);
        auditDto.setResourceType(resourceType);
        auditDto.setResourceId(resourceId);
        auditDto.setAction(action);
        auditDto.setResult(result);
        auditDto.setApplication(application);
        auditDto.setAuditTime(date);
        auditDto.setClient(client);
        auditDto.setServer(server);
        auditDto.setDescription(description);

        BaseDTO baseDto = new BaseDTO();

        try {
            auditQueue.add(auditDto);
            baseDto.setCode(200);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            baseDto.setCode(500);
            baseDto.setMessage(ex.getMessage());
        }

        return baseDto;
    }

    @Resource
    public void setAuditQueue(AuditQueue auditQueue) {
        this.auditQueue = auditQueue;
    }
}
