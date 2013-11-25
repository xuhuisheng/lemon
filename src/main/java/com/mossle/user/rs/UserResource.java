package com.mossle.user.rs;

import java.util.List;

import javax.annotation.Resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mossle.user.persistence.manager.UserBaseManager;

import org.springframework.stereotype.Component;

@Component
@Path("user")
public class UserResource {
    private UserBaseManager userBaseManager;

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> search(@QueryParam("username") String username) {
        List<String> usernames = userBaseManager.find(
                "select username from UserBase where username like ?", "%"
                        + username + "%");

        return usernames;
    }

    @Resource
    public void setUserBaseManager(UserBaseManager userBaseManager) {
        this.userBaseManager = userBaseManager;
    }
}
