package com.mossle.internal.whitelist.rs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mossle.core.util.BaseDTO;

import com.mossle.internal.whitelist.persistence.domain.WhitelistApp;
import com.mossle.internal.whitelist.persistence.domain.WhitelistHost;
import com.mossle.internal.whitelist.persistence.domain.WhitelistIp;
import com.mossle.internal.whitelist.persistence.domain.WhitelistType;
import com.mossle.internal.whitelist.persistence.manager.WhitelistTypeManager;

import org.springframework.stereotype.Component;

@Component
@Path("whitelist")
public class WhitelistResource {
    private WhitelistTypeManager whitelistTypeManager;

    @Path("get")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO getWhitelist(@QueryParam("code") String code,
            @QueryParam("tenantId") String tenantId) {
        BaseDTO baseDto = new BaseDTO();
        WhitelistType whitelistType = whitelistTypeManager.findUnique(
                "from WhitelistType where code=? and tenantId=?", code,
                tenantId);

        if (whitelistType == null) {
            baseDto.setCode(404);
            baseDto.setMessage("cannot find by code : " + code);

            return baseDto;
        }

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (WhitelistApp whitelistApp : whitelistType.getWhitelistApps()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", Long.toString(whitelistApp.getId()));
            map.put("name", whitelistApp.getName());
            map.put("description", whitelistApp.getDescription());
            map.put("host", this.processHost(whitelistApp.getWhitelistHosts()));
            map.put("ip", this.processIp(whitelistApp.getWhitelistIps()));
            map.put("forceRelogin",
                    Integer.valueOf(1).equals(whitelistApp.getForceRelogin()));
            map.put("code", whitelistApp.getCode());
            map.put("username", whitelistApp.getUsername());
            map.put("password", whitelistApp.getPassword());
            map.put("level", whitelistApp.getLevel());
            list.add(map);
        }

        baseDto.setCode(200);
        baseDto.setData(list);

        return baseDto;
    }

    public List<String> processHost(Set<WhitelistHost> whitelistHosts) {
        List<String> list = new ArrayList<String>();

        for (WhitelistHost whitelistHost : whitelistHosts) {
            list.add(whitelistHost.getValue());
        }

        return list;
    }

    public List<String> processIp(Set<WhitelistIp> whitelistIps) {
        List<String> list = new ArrayList<String>();

        for (WhitelistIp whitelistIp : whitelistIps) {
            list.add(whitelistIp.getValue());
        }

        return list;
    }

    @Resource
    public void setWhitelistTypeManager(
            WhitelistTypeManager whitelistTypeManager) {
        this.whitelistTypeManager = whitelistTypeManager;
    }
}
