package com.mossle.msg.rs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.List;

import javax.annotation.Resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.core.auth.CurrentUserHolder;

import com.mossle.msg.persistence.domain.MsgInfo;
import com.mossle.msg.persistence.manager.MsgInfoManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("msg/widget")
public class MsgWidgetResource {
    private static Logger logger = LoggerFactory
            .getLogger(MsgWidgetResource.class);
    private MsgInfoManager msgInfoManager;
    private TenantHolder tenantHolder;
    private CurrentUserHolder currentUserHolder;
    private UserConnector userConnector;

    @GET
    @Path("msg")
    @Produces(MediaType.TEXT_HTML)
    public String msg() throws Exception {
        String userId = currentUserHolder.getUserId();
        String hql = "from MsgInfo where receiverId=? and status=0 order by createTime desc";
        List<MsgInfo> msgInfos = (List<MsgInfo>) msgInfoManager.pagedQuery(hql,
                1, 10, userId).getResult();

        StringBuilder buff = new StringBuilder();
        buff.append("<table class='table table-hover'>");
        buff.append("  <thead>");
        buff.append("    <tr>");
        buff.append("      <th>发送人</th>");
        buff.append("      <th>时间</th>");
        buff.append("      <th>消息</th>");
        buff.append("    </tr>");
        buff.append("  </thead>");
        buff.append("  <tbody>");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (MsgInfo msgInfo : msgInfos) {
            buff.append("    <tr>");
            buff.append("      <td>"
                    + this.findDisplayName(msgInfo.getSenderId()) + "</td>");
            buff.append("      <td>"
                    + dateFormat.format(msgInfo.getCreateTime()) + "</td>");
            buff.append("      <td>");
            buff.append("        <a href='" + ".."
                    + "/msg/msg-info-view.do?id=" + msgInfo.getId() + "'>"
                    + this.substr(msgInfo.getContent()) + "</a>");
            buff.append("      </td>");
            buff.append("    </tr>");
        }

        buff.append("  </tbody>");
        buff.append("</table>");

        return buff.toString();
    }

    public String findDisplayName(String userId) {
        if (StringUtils.isBlank(userId)) {
            return "";
        }

        return userConnector.findById(userId.trim()).getDisplayName();
    }

    public String substr(String text) {
        if (StringUtils.isBlank(text)) {
            return "";
        }

        if (text.trim().length() < 10) {
            return text.trim();
        }

        return text.trim().substring(0, 10) + "...";
    }

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
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }
}
