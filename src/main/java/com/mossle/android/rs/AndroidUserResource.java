package com.mossle.android.rs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.*;
import java.util.List;

import javax.annotation.Resource;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mossle.api.process.ProcessConnector;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.manager.BpmProcessManager;

import com.mossle.cms.persistence.domain.CmsArticle;
import com.mossle.cms.persistence.manager.CmsArticleManager;

import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.util.BaseDTO;

import com.mossle.msg.persistence.domain.MsgInfo;
import com.mossle.msg.persistence.manager.MsgInfoManager;

import com.mossle.pim.persistence.domain.PimDevice;
import com.mossle.pim.persistence.manager.PimDeviceManager;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricProcessInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("android/user")
public class AndroidUserResource {
    private static Logger logger = LoggerFactory
            .getLogger(AndroidUserResource.class);
    private JsonMapper jsonMapper = new JsonMapper();
    private TenantHolder tenantHolder;
    private MsgInfoManager msgInfoManager;
    private UserConnector userConnector;
    private PimDeviceManager pimDeviceManager;

    @POST
    @Path("profile")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO profile(@HeaderParam("sessionId") String sessionId)
            throws Exception {
        logger.info("start");

        PimDevice pimDevice = pimDeviceManager.findUniqueBy("sessionId",
                sessionId);

        if (pimDevice == null) {
            BaseDTO result = new BaseDTO();
            result.setCode(401);
            result.setMessage("auth fail");

            return result;
        }

        String userId = pimDevice.getUserId();
        UserDTO userDto = userConnector.findById(userId);

        Map<String, String> map = new HashMap<String, String>();
        map.put("displayName", userDto.getDisplayName());
        map.put("email", userDto.getEmail());
        map.put("mobile", userDto.getMobile());

        String json = jsonMapper.toJson(map);
        BaseDTO result = new BaseDTO();
        result.setCode(200);
        result.setData(json);
        logger.info("end");

        return result;
    }

    // ~ ======================================================================
    @Resource
    public void setMsgInfoManager(MsgInfoManager msgInfoManager) {
        this.msgInfoManager = msgInfoManager;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setPimDeviceManager(PimDeviceManager pimDeviceManager) {
        this.pimDeviceManager = pimDeviceManager;
    }
}
