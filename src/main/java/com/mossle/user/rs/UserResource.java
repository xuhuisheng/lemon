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

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.util.BaseDTO;
import com.mossle.core.util.StringUtils;

import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.manager.AccountInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("user")
public class UserResource {
    private static Logger logger = LoggerFactory.getLogger(UserResource.class);
    private AccountInfoManager accountInfoManager;
    private Long defaultUserRepoId = 1L;
    private JsonMapper jsonMapper = new JsonMapper();

    @GET
    @Path("exists")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean exists(@QueryParam("username") String username) {
        AccountInfo accountInfo = accountInfoManager.findUniqueBy("username",
                username);

        return accountInfo != null;
    }

    @GET
    @Path("get")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO getUserByUsername(@QueryParam("username") String username) {
        if (StringUtils.isBlank(username)) {
            logger.error("username cannot be blank");

            return null;
        }

        try {
            AccountInfo accountInfo = accountInfoManager.findUniqueBy(
                    "username", username);

            BaseDTO result = new BaseDTO();

            if (accountInfo == null) {
                logger.error("user is not exists : [{}]", username);
                result.setCode(404);
                result.setMessage("user is not exists : [" + username + "]");

                return result;
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", accountInfo.getId());
            map.put("username", accountInfo.getUsername());
            map.put("nickName", accountInfo.getNickName());
            map.put("displayName", accountInfo.getDisplayName());
            result.setCode(200);
            result.setData(map);

            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            BaseDTO result = new BaseDTO();
            result.setCode(500);
            result.setMessage(ex.getMessage());

            return result;
        }
    }

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String, Object>> search(
            @QueryParam("username") String username) {
        Page page = accountInfoManager.pagedQuery(
                "from AccountInfo where username like ?", 1, 5, "%" + username
                        + "%");
        List<AccountInfo> accountInfos = (List<AccountInfo>) page.getResult();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (AccountInfo accountInfo : accountInfos) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", accountInfo.getId());
            map.put("username", accountInfo.getUsername());
            map.put("displayName", accountInfo.getDisplayName());
            list.add(map);
        }

        return list;
    }

    // ~ ======================================================================
    @Resource
    public void setAccountInfoManager(AccountInfoManager accountInfoManager) {
        this.accountInfoManager = accountInfoManager;
    }

    public void setDefaultUserRepoId(Long defaultUserRepoId) {
        this.defaultUserRepoId = defaultUserRepoId;
    }
}
