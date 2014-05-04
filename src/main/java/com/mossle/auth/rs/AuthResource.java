package com.mossle.auth.rs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mossle.api.scope.ScopeHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.auth.domain.Access;
import com.mossle.auth.domain.Role;
import com.mossle.auth.domain.UserStatus;
import com.mossle.auth.manager.AccessManager;
import com.mossle.auth.manager.RoleManager;
import com.mossle.auth.manager.UserStatusManager;
import com.mossle.auth.service.AuthService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Component;

@Component
@Path("auth")
public class AuthResource {
    private static Logger logger = LoggerFactory.getLogger(AuthResource.class);
    public static final String HQL_AUTHORITY = "select p.code from Perm p join p.roleDefs rd join rd.roles r join r.userStatuses u"
            + " where u.id=? and r.localId=?";
    public static final String HQL_ATTRIBUTE = "select r.name from Role r join r.userStatuses u"
            + " where u.id=? and r.localId=?";
    public static final String HQL_ACCESS = "from Access where scopeId=? order by priority";
    private UserStatusManager userStatusManager;
    private AccessManager accessManager;
    private UserConnector userConnector;
    private JdbcTemplate jdbcTemplate;
    private RoleManager roleManager;
    private AuthService authService;

    @GET
    @Path("userid")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO getUserById(@QueryParam("userId") String userId) {
        if (userId == null) {
            logger.error("userId cannot be null");

            return null;
        }

        try {
            com.mossle.api.user.UserDTO apiUserDto = userConnector
                    .findById(userId);

            UserDTO userDto = new UserDTO();

            if (apiUserDto == null) {
                logger.error("user is not exists : [{}]", userId);

                userDto.setUsername(userId);
                userDto.setPassword("NO_PASSWORD");
                userDto.setAuthorities(Collections.EMPTY_LIST);
                userDto.setAttributes(Collections.EMPTY_LIST);

                return userDto;
            }

            String hql = "from UserStatus where username=? and userRepoRef=?";
            UserStatus userStatus = userStatusManager.findUnique(hql,
                    apiUserDto.getUsername(), ScopeHolder.getUserRepoRef());

            if (userStatus == null) {
                logger.debug("user has no authorities : [{}]", userId);

                logger.debug("find user : [{}]", apiUserDto.getUsername());
                userDto.setUsername(apiUserDto.getUsername());

                if ((userDto.getUsername() == null)
                        || "".equals(userDto.getUsername())) {
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

                List<String> authorties = userStatusManager.find(HQL_AUTHORITY,
                        userStatus.getId(), ScopeHolder.getScopeId());
                userDto.setAuthorities(authorties);

                List<String> roles = userStatusManager.find(HQL_ATTRIBUTE,
                        userStatus.getId(), ScopeHolder.getScopeId());
                List<String> attributes = new ArrayList<String>();

                for (String role : roles) {
                    attributes.add("ROLE_" + role);
                }

                userDto.setAttributes(attributes);
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
    public UserDTO getUser(@QueryParam("username") String username) {
        if (username == null) {
            logger.error("username cannot be null");

            return null;
        }

        logger.debug("username : {}", username);

        try {
            com.mossle.api.user.UserDTO apiUserDto = userConnector
                    .findByUsername(username, ScopeHolder.getUserRepoRef());

            UserDTO userDto = new UserDTO();

            if (apiUserDto == null) {
                logger.error("user is not exists : [{}]", username);

                userDto.setUsername(username);
                userDto.setPassword("NO_PASSWORD");
                userDto.setAuthorities(Collections.EMPTY_LIST);
                userDto.setAttributes(Collections.EMPTY_LIST);

                return userDto;
            }

            String hql = "from UserStatus where username=? and userRepoRef=?";
            UserStatus userStatus = userStatusManager.findUnique(hql,
                    apiUserDto.getUsername(), ScopeHolder.getUserRepoRef());

            if (userStatus == null) {
                logger.debug("user has no authorities : [{}]", username);
                userDto.setUsername(username);
                userDto.setAuthorities(Collections.EMPTY_LIST);
                userDto.setAttributes(Collections.EMPTY_LIST);
            } else {
                userDto.setUsername(userStatus.getUsername());
                userDto.setPassword(userStatus.getPassword());
                userDto.setAppId("0");

                List<String> authorties = userStatusManager.find(HQL_AUTHORITY,
                        userStatus.getId(), ScopeHolder.getScopeId());
                logger.debug("authorties : {}", authorties);
                userDto.setAuthorities(authorties);

                List<String> roles = userStatusManager.find(HQL_ATTRIBUTE,
                        userStatus.getId(), ScopeHolder.getScopeId());
                logger.debug("roles : {}", roles);

                List<String> attributes = new ArrayList<String>();

                for (String role : roles) {
                    attributes.add("ROLE_" + role);
                }

                userDto.setAttributes(attributes);
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
    public List<AccessDTO> getResource() {
        List<Access> accesses = accessManager.find(HQL_ACCESS,
                ScopeHolder.getScopeId());
        List<AccessDTO> accessDtos = new ArrayList<AccessDTO>();

        for (Access access : accesses) {
            AccessDTO dto = new AccessDTO();
            dto.setAccess(access.getValue());
            dto.setPermission(access.getPerm().getCode());
            accessDtos.add(dto);
        }

        return accessDtos;
    }

    // ~ ======================================================================
    @GET
    @Path("findUsers")
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserDTO> findUsers(@QueryParam("appId") Long appId) {
        Long localId = appId;
        Long globalId = jdbcTemplate.queryForObject(
                "select global_id from scope_local where id=?", Long.class,
                localId);
        logger.debug("globalId : {}", globalId);
        logger.debug("localId : {}", localId);

        List<UserStatus> userStatuses = userStatusManager.find(
                "from UserStatus where localId=?", localId);
        List<UserDTO> userDtos = new ArrayList<UserDTO>();

        for (UserStatus userStatus : userStatuses) {
            UserDTO userDto = new UserDTO();
            userDto.setUserId(userStatus.getId());
            userDto.setUsername(userStatus.getUsername());

            List<String> roles = userStatusManager.find(HQL_ATTRIBUTE,
                    userStatus.getId());
            logger.debug("roles : {}", roles);

            userDto.setAuthorities(roles);
            userDtos.add(userDto);
        }

        return userDtos;
    }

    @GET
    @Path("findRoles")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RoleDTO> findRoles(@QueryParam("appId") Long appId) {
        Long localId = appId;
        Long globalId = jdbcTemplate.queryForObject(
                "select global_id from scope_local where id=?", Long.class,
                localId);
        logger.debug("globalId : {}", globalId);
        logger.debug("localId : {}", localId);

        List<Role> roles = roleManager.find("from Role where localId=?",
                localId);
        List<RoleDTO> roleDtos = new ArrayList<RoleDTO>();

        for (Role role : roles) {
            RoleDTO roleDto = new RoleDTO();
            roleDto.setId(role.getId());
            roleDto.setName(role.getName());

            roleDtos.add(roleDto);
        }

        return roleDtos;
    }

    @GET
    @Path("getUserByUsername")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO getUserByUsername(@QueryParam("username") String username,
            @QueryParam("appId") Long appId) {
        logger.debug("username : {}", username);

        Long localId = appId;
        Long globalId = jdbcTemplate.queryForObject(
                "select global_id from scope_local where id=?", Long.class,
                localId);
        logger.debug("globalId : {}", globalId);
        logger.debug("localId : {}", localId);

        com.mossle.api.user.UserDTO apiUserDto = userConnector.findByUsername(
                username, Long.toString(globalId));

        if (apiUserDto == null) {
            return null;
        }

        String userId = apiUserDto.getId();

        UserStatus userStatus = userStatusManager.findUnique(
                "from UserStatus where ref=? and localId=?", userId, localId);

        if (userStatus == null) {
            userStatus = new UserStatus();
            userStatus.setRef(userId);
            userStatus.setUsername(username);
            userStatus.setStatus(1);
            userStatus.setUserRepoRef(ScopeHolder.getUserRepoRef());
            userStatus.setScopeId(ScopeHolder.getScopeId());
            userStatusManager.save(userStatus);
        }

        UserDTO userDto = new UserDTO();

        userDto.setUserId(userStatus.getId());

        userDto.setUsername(apiUserDto.getUsername());

        List<String> roles = userStatusManager.find(HQL_ATTRIBUTE,
                userStatus.getId());
        logger.debug("roles : {}", roles);

        userDto.setAuthorities(roles);

        return userDto;
    }

    @GET
    @Path("configUserRole")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean configUserRole(@QueryParam("userId") Long userId,
            @QueryParam("roleIds") List<Long> roleIds) {
        logger.info("userId : {}", userId);
        logger.info("roleIds : {}", roleIds);

        authService.configUserRole(userId, roleIds,
                ScopeHolder.getUserRepoRef(), ScopeHolder.getScopeId(), true);

        return true;
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
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Resource
    public void setRoleManager(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    @Resource
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }
}
