package com.mossle.msg.rs;

import javax.annotation.Resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;

import com.mossle.msg.persistence.manager.MsgInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component
@Path("msg")
public class MsgResource {
    private static Logger logger = LoggerFactory.getLogger(MsgResource.class);
    private MsgInfoManager msgInfoManager;
    private JsonMapper jsonMapper = new JsonMapper();
    private CurrentUserHolder currentUserHolder;
    private boolean enable = true;

    @GET
    @Path("unreadCount")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO unreadCount() {
        if (!enable) {
            return new BaseDTO();
        }

        String userId = currentUserHolder.getUserId();
        Integer count = msgInfoManager.getCount(
                "select count(*) from MsgInfo where receiverId=? and status=0",
                userId);
        BaseDTO result = new BaseDTO();
        result.setData(count);

        return result;
    }

    // ~ ======================================================================
    @Resource
    public void setMsgInfoManager(MsgInfoManager msgInfoManager) {
        this.msgInfoManager = msgInfoManager;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Value("${msg.enable}")
    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
