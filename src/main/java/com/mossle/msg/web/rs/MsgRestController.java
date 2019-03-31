package com.mossle.msg.web.rs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.client.user.UserClient;

import com.mossle.core.page.Page;
import com.mossle.core.util.BaseDTO;

import com.mossle.msg.persistence.domain.MsgInfo;
import com.mossle.msg.persistence.manager.MsgInfoManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("msg/rs")
public class MsgRestController {
    private static Logger logger = LoggerFactory
            .getLogger(MsgRestController.class);
    private MsgInfoManager msgInfoManager;
    private TenantHolder tenantHolder;
    private CurrentUserHolder currentUserHolder;
    private UserClient userClient;

    @RequestMapping("unreadCount")
    public BaseDTO unreadCount() {
        String userId = currentUserHolder.getUserId();
        String hql = "from MsgInfo where receiverId=? and status=0";
        Page page = msgInfoManager.pagedQuery(hql, 1, 5, userId);

        BaseDTO result = new BaseDTO();
        result.setData(page);

        return result;
    }

    // ~
    @Resource
    public void setMsgInfoManager(MsgInfoManager msgInfoManager) {
        this.msgInfoManager = msgInfoManager;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }
}
