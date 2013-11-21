package com.mossle.auth.rs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mossle.api.UserConnector;

import com.mossle.auth.domain.Access;
import com.mossle.auth.domain.UserStatus;
import com.mossle.auth.manager.AccessManager;
import com.mossle.auth.manager.UserStatusManager;

import com.mossle.core.jdbc.DataSourceService;
import com.mossle.core.mail.MailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Component;

@Component
@Path("authority")
public class AuthorityResource {
    private static Logger logger = LoggerFactory
            .getLogger(AuthorityResource.class);
    public static final String HQL_AUTHORITY = "select p.name from Perm p join p.roleDefs rd join rd.roles r join r.userStatuses u"
            + " where u.id=? and r.localId=?";
    public static final String HQL_ATTRIBUTE = "select r.name from Role r join r.userStatuses u"
            + " where u.id=? and r.localId=?";
    public static final String HQL_ACCESS = "from Access where localId=? order by priority";
    private UserStatusManager userStatusManager;
    private AccessManager accessManager;
    private UserConnector userConnector;
    private DataSourceService dataSourceService;
    private JdbcTemplate jdbcTemplate;
    private MailService mailService;
    private Map<Long, String> appMap = new HashMap<Long, String>();
    private Map<Long, String> userRepoMap = new HashMap<Long, String>();
    private Map<Long, String> globalMap = new HashMap<Long, String>();

    public AuthorityResource() {
        appMap.put(1L, "taoli");
        appMap.put(2L, "plan a");
        appMap.put(3L, "sem");
        appMap.put(4L, "platform");
        appMap.put(5L, "hrlms");
        appMap.put(6L, "sandbox");
        userRepoMap.put(2L, "uc");
        userRepoMap.put(3L, "passport");
        userRepoMap.put(4L, "uuap");
        globalMap.put(1L, "uc");
        globalMap.put(2L, "passport");
        globalMap.put(3L, "uuap");
    }

    @GET
    @Path("userid")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO getUserById(@QueryParam("userId") String userId,
            @QueryParam("appId") Long appId) {
        // appId repoCode
        // auth 0 1
        // taoli 1 3
        // plana 2 2
        // sem 3 3
        // platform 4 *
        // hrlms 5 4
        // sandbox 6 ?
        if (userId == null) {
            logger.error("userId cannot be null");

            return null;
        }

        logger.debug("userId : {}", userId);
        logger.debug("appId : {}({})", appId, appMap.get(appId));

        Long localId = appId;
        Long globalId = jdbcTemplate.queryForObject(
                "select global_id from scope_local where id=?", Long.class,
                localId);

        logger.debug("localId : {}({})", localId, appMap.get(localId));
        logger.debug("globalId : {}({})", globalId, globalMap.get(globalId));

        try {
            com.mossle.api.UserDTO apiUserDto = userConnector.findById(userId);

            UserDTO userDto = new UserDTO();

            if (apiUserDto == null) {
                logger.error("user is not exists : [{}]", userId);

                userDto.setUsername(userId);
                userDto.setPassword("NO_PASSWORD");
                userDto.setAuthorities(Collections.EMPTY_LIST);
                userDto.setAttributes(Collections.EMPTY_LIST);

                return userDto;
            }

            String hql = "from UserStatus where reference=?";
            UserStatus userStatus = userStatusManager.findUnique(hql, userId);

            if (userStatus == null) {
                logger.debug("user has no authorities : [{}]", userId);

                logger.debug("find user : [{}]", apiUserDto.getUsername());
                userDto.setUsername(apiUserDto.getUsername());

                if ((userDto.getUsername() == null)
                        || "".equals(userDto.getUsername())) {
                    // userDto.setUsername((String) remoteMap.get("email"));
                    userDto.setUsername(apiUserDto.getId());
                }

                userDto.setPassword("NO_PASSWORD");
                userDto.setAuthorities(Collections.EMPTY_LIST);
                userDto.setAttributes(Collections.EMPTY_LIST);
                logger.debug("username : [{}]", userDto.getUsername());
                logger.debug("password : [{}]", userDto.getPassword());
            } else {
                userDto.setUsername(userStatus.getUsername());
                userDto.setPassword(userStatus.getPassword());

                List<String> authorities = userStatusManager
                        .find("select p.name from Perm p join p.roleDefs rd join rd.roles r join r.userStatuses u"
                                + " where u.id=?", userStatus.getId());
                userDto.setAuthorities(authorities);

                List<String> roles = userStatusManager.find(
                        "select r.name from Role r join r.userStatuses u"
                                + " where u.id=?", userStatus.getId());
                List<String> attributes = new ArrayList<String>();

                for (String role : roles) {
                    attributes.add("ROLE_" + role);
                }

                userDto.setAttributes(attributes);

                // FIXME: send mail
                if (authorities.isEmpty()) {
                    mailService.send(
                            "xuhuisheng@baidu.com",
                            "[userId]",
                            "userId:" + userId + "<br>" + "appId:" + appId
                                    + "<br>" + "username:"
                                    + userStatus.getUsername() + "<br>");
                    userDto.setAuthorities(Collections.singletonList("*"));
                    userDto.setAttributes(Arrays.asList(new String[] {
                            "ROLE_admin", "ROLE_PALAN_ROLE_SUPERADMIN",
                            "ROLE_PALAN_ROLE_SUBCOM_ADMIN" }));
                }
            }

            return userDto;
        } catch (Exception ex) {
            logger.error("", ex);

            UserDTO userDto = new UserDTO();
            userDto.setUsername(userId);

            return userDto;
        }
    }

    @GET
    @Path("user")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO getUser(@QueryParam("username") String username,
            @QueryParam("appId") Long appId,
            @QueryParam("repoCode") Long repoCode) {
        // appId repoCode
        // auth 0 1
        // taoli 1 3
        // plana 2 2
        // sem 3 3
        // platform 4 *
        // hrlms 5 4
        // sandbox 6 ?
        if (username == null) {
            logger.error("username cannot be null");

            return null;
        }

        if (appId == 6L) {
            logger.debug("change 6 to 5");
            appId = 5L;
        }

        logger.debug("username : {}", username);
        logger.debug("appId : {}({})", appId, appMap.get(appId));
        logger.debug("repoCode : {}({})", repoCode, userRepoMap.get(repoCode));

        Long localId = appId;
        Long globalId = repoCode - 1;
        logger.debug("localId : {}({})", localId, appMap.get(localId));
        logger.debug("globalId : {}({})", globalId, globalMap.get(globalId));

        try {
            com.mossle.api.UserDTO apiUserDto = userConnector.findByUsername(
                    username, globalId);

            UserDTO userDto = new UserDTO();

            if (apiUserDto == null) {
                logger.error("user is not exists : [{}]", username);

                userDto.setUsername(username);
                userDto.setPassword("NO_PASSWORD");
                userDto.setAuthorities(Collections.EMPTY_LIST);
                userDto.setAttributes(Collections.EMPTY_LIST);

                return userDto;
            }

            String hql = "from UserStatus where username=? and globalId=?";
            UserStatus userStatus = userStatusManager.findUnique(hql,
                    apiUserDto.getUsername(), globalId);

            if (userStatus == null) {
                logger.debug("user has no authorities : [{}]", username);
                userDto.setUsername(username);
                userDto.setAuthorities(Collections.EMPTY_LIST);
                userDto.setAttributes(Collections.EMPTY_LIST);
            } else {
                userDto.setUsername(userStatus.getUsername());
                userDto.setPassword(userStatus.getPassword());
                userDto.setAppId("0");

                List<String> authorities = userStatusManager
                        .find("select p.name from Perm p join p.roleDefs rd join rd.roles r join r.userStatuses u"
                                + " where u.id=?", userStatus.getId());
                logger.debug("authorties : {}", authorities);
                userDto.setAuthorities(authorities);

                List<String> roles = userStatusManager.find(
                        "select r.name from Role r join r.userStatuses u"
                                + " where u.id=?", userStatus.getId());
                logger.debug("roles : {}", roles);

                List<String> attributes = new ArrayList<String>();

                for (String role : roles) {
                    attributes.add("ROLE_" + role);
                }

                userDto.setAttributes(attributes);

                // FIXME: send mail
                if (authorities.isEmpty()) {
                    mailService.send("xuhuisheng@baidu.com", "[username]",
                            "username:" + username + "<br>" + "repoCode:"
                                    + repoCode + "<br>" + "appId:" + appId
                                    + "<br>" + "username:" + username + "<br>");
                    userDto.setAuthorities(Collections.singletonList("*"));
                    userDto.setAttributes(Arrays.asList(new String[] {
                            "ROLE_admin", "ROLE_PALAN_ROLE_SUPERADMIN",
                            "ROLE_PALAN_ROLE_SUBCOM_ADMIN" }));
                }
            }

            return userDto;
        } catch (Exception ex) {
            logger.error("", ex);

            UserDTO userDto = new UserDTO();
            userDto.setUsername(username);

            return userDto;
        }
    }

    @GET
    @Path("resource")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AccessDTO> getResource(@QueryParam("appId") Long appId) {
        Long localId = appId;
        List<Access> accesses = accessManager.find(HQL_ACCESS, localId);
        List<AccessDTO> accessDtos = new ArrayList<AccessDTO>();

        for (Access access : accesses) {
            AccessDTO dto = new AccessDTO();
            dto.setAccess(access.getValue());
            dto.setPermission(access.getPerm().getName());
            accessDtos.add(dto);
        }

        // FIXME: send mail
        if (accessDtos.isEmpty()) {
            mailService.send("xuhuisheng@baidu.com", "[resource]", "appId:"
                    + appId + "<br>");
        }

        return accessDtos;
    }

    @Resource
    public void setUserStatusManager(UserStatusManager userStatusManager) {
        this.userStatusManager = userStatusManager;
    }

    @Resource
    public void setAccessManager(AccessManager accessManager) {
        this.accessManager = accessManager;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setDataSourceService(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Resource
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }
}
