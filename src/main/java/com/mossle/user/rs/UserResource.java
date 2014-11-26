package com.mossle.user.rs;

import java.util.ArrayList;
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

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;
import com.mossle.core.util.StringUtils;

import com.mossle.user.persistence.domain.UserBase;
import com.mossle.user.persistence.manager.UserBaseManager;
import com.mossle.user.service.UserService;
import com.mossle.user.support.UserBaseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("user")
public class UserResource {
    private static Logger logger = LoggerFactory.getLogger(UserResource.class);
    private UserBaseManager userBaseManager;
    private UserService userService;
    private Long defaultUserRepoId = 1L;
    private JsonMapper jsonMapper = new JsonMapper();

    @GET
    @Path("exists")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean exists(@QueryParam("username") String username) {
        UserBase userBase = userBaseManager.findUniqueBy("username", username);

        return userBase != null;
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
            UserBase userBase = userBaseManager.findUniqueBy("username",
                    username);

            BaseDTO result = new BaseDTO();

            if (userBase == null) {
                logger.error("user is not exists : [{}]", username);
                result.setCode(404);
                result.setMessage("user is not exists : [" + username + "]");

                return result;
            }

            UserBaseWrapper userBaseWrapper = new UserBaseWrapper(userBase);

            result.setCode(200);
            result.setData(userBaseWrapper.toMap());

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
        List<UserBase> userBases = userBaseManager.find(
                "from UserBase where username like ?", "%" + username + "%");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (UserBase userBase : userBases) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", userBase.getId());
            map.put("username", userBase.getUsername());
            map.put("displayName", userBase.getNickName());
            list.add(map);
        }

        return list;
    }

    /**
     * 添加用户.
     */
    @POST
    @Path("insert")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO insertUser(@FormParam("data") String data) {
        try {
            Map<String, Object> map = jsonMapper.fromJson(data, Map.class);
            UserBase userBase = jsonMapper.fromJson(data, UserBase.class);
            userService.insertUser(userBase, defaultUserRepoId, map);

            BaseDTO baseDto = new BaseDTO();
            baseDto.setCode(200);

            return baseDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            BaseDTO baseDto = new BaseDTO();
            baseDto.setCode(500);
            baseDto.setMessage(ex.getMessage());

            return baseDto;
        }
    }

    /**
     * 更新用户.
     */
    @POST
    @Path("update")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO updateUser(@FormParam("data") String data) {
        try {
            Map<String, Object> map = jsonMapper.fromJson(data, Map.class);
            UserBase userBase = jsonMapper.fromJson(data, UserBase.class);
            userService.updateUser(userBase, defaultUserRepoId, map);

            BaseDTO baseDto = new BaseDTO();
            baseDto.setCode(200);

            return baseDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            BaseDTO baseDto = new BaseDTO();
            baseDto.setCode(500);
            baseDto.setMessage(ex.getMessage());

            return baseDto;
        }
    }

    // ~ ======================================================================
    @Resource
    public void setUserBaseManager(UserBaseManager userBaseManager) {
        this.userBaseManager = userBaseManager;
    }

    @Resource
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setDefaultUserRepoId(Long defaultUserRepoId) {
        this.defaultUserRepoId = defaultUserRepoId;
    }
}
