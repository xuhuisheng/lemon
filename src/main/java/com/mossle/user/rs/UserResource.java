package com.mossle.user.rs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mossle.user.persistence.domain.UserBase;
import com.mossle.user.persistence.manager.UserBaseManager;

import org.springframework.stereotype.Component;

@Component
@Path("user")
public class UserResource {
    private UserBaseManager userBaseManager;

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String, Object>> search(
            @QueryParam("username") String username) {
        List<UserBase> userBases = userBaseManager.find(
                "from UserBase where username like ?", "%" + username + "%");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (UserBase userBase : userBases) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", userBase.getId());
            map.put("username", userBase.getUsername());
            map.put("displayName", userBase.getDisplayName());
            list.add(map);
        }

        return list;
    }

    @Resource
    public void setUserBaseManager(UserBaseManager userBaseManager) {
        this.userBaseManager = userBaseManager;
    }
}
