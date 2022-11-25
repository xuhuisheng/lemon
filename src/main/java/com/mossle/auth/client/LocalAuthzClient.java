package com.mossle.auth.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantConnector;
import com.mossle.api.tenant.TenantDTO;
import com.mossle.api.user.UserDTO;
import com.mossle.api.userauth.UserAuthDTO;

import com.mossle.auth.persistence.domain.Perm;
import com.mossle.auth.persistence.domain.Role;
import com.mossle.auth.persistence.manager.PermManager;
import com.mossle.auth.persistence.manager.RoleManager;

import com.mossle.client.authz.AuthzClient;
import com.mossle.client.user.AccountStatusClient;
import com.mossle.client.user.UserClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.jdbc.core.JdbcTemplate;

public class LocalAuthzClient implements AuthzClient {
    private static Logger logger = LoggerFactory
            .getLogger(LocalAuthzClient.class);
    private PermManager permManager;
    private RoleManager roleManager;
    private AccountStatusClient accountStatusClient;
    private TenantConnector tenantConnector;
    private UserClient userClient;
    private boolean checkAccountStatus = false;
    private String sysCode;

    public UserAuthDTO findByUsername(String username, String tenantId) {
        TenantDTO tenantDto = tenantConnector.findById(tenantId);
        UserDTO userDto = userClient.findByUsername(username,
                tenantDto.getUserRepoRef());

        if (userDto == null) {
            logger.info("cannot find user by (" + username + ","
                    + tenantDto.getUserRepoRef() + ")");

            return null;
        }

        return this.process(userDto, tenantDto);
    }

    public UserAuthDTO findByRef(String ref, String tenantId) {
        TenantDTO tenantDto = tenantConnector.findById(tenantId);

        // TODO: there is only findById, no findByRef
        UserDTO userDto = userClient.findById(ref, tenantDto.getUserRepoRef());

        return process(userDto, tenantDto);
    }

    public UserAuthDTO findById(String id, String tenantId) {
        TenantDTO tenantDto = tenantConnector.findById(tenantId);
        UserDTO userDto = userClient.findById(id, tenantId);

        return process(userDto, tenantDto);
    }

    public UserAuthDTO process(UserDTO userDto, TenantDTO tenantDto) {
        UserAuthDTO userAuthDto = new UserAuthDTO();
        userAuthDto.setId(userDto.getId());
        userAuthDto.setTenantId(tenantDto.getId());
        userAuthDto.setUsername(userDto.getUsername());
        userAuthDto.setRef(userDto.getRef());
        userAuthDto.setDisplayName(userDto.getDisplayName());
        userAuthDto.setStatus(Integer.toString(userDto.getStatus()));
        // enable
        userAuthDto.setEnabled("1".equals(userAuthDto.getStatus()));
        userAuthDto.setCredentialsExpired(false);
        userAuthDto.setAccountLocked(false);
        userAuthDto.setAccountExpired(false);

        this.doCheckAccountStatus(userAuthDto, userDto);

        // permissions
        List<String> permissions = this.findPermissions(userDto.getId(),
                tenantDto.getId());
        userAuthDto.setPermissions(permissions);

        // roles
        List<String> roles = this.findRoles(userDto.getId(), tenantDto.getId());
        userAuthDto.setRoles(roles);

        return userAuthDto;
    }

    public void doCheckAccountStatus(UserAuthDTO userAuthDto, UserDTO userDto) {
        if (!checkAccountStatus) {
            logger.debug("skip check account status");

            return;
        }

        userAuthDto.setAccountLocked(this.accountStatusClient
                .findAccountLocked(userDto.getId()));
        userAuthDto.setAccountExpired(this.accountStatusClient
                .findAccountExpired(userDto.getId()));
        userAuthDto.setCredentialsExpired(this.accountStatusClient
                .findCredentialsExpired(userDto.getId()));
    }

    public List<String> findPermissions(String userId, String sysCode) {
        String hql = "from Perm perm join perm.roleDefs roleDef join roleDef.roles role join role.userStatuses user "
                + " where user.ref=? and user.tenantId=?";
        List<Perm> perms = permManager.find(hql, userId, sysCode);

        logger.debug("userDto.getId() : {}", userId);
        logger.debug("tenantDto.getId() : {}", sysCode);
        logger.debug("permissions : {}", perms);

        List<String> list = new ArrayList<String>();

        for (Perm perm : perms) {
            list.add(perm.getCode());
        }

        return list;
    }

    public List<String> findRoles(String userId, String sysCode) {
        String hql = "from Role role join role.userStatuses user "
                + " where user.ref=? and user.tenantId=?";
        List<Role> roles = roleManager.find(hql, userId, sysCode);
        List<String> list = new ArrayList<String>();

        for (Role role : roles) {
            list.add(role.getName());
        }

        return list;
    }

    public List<String> convertMapListToStringList(
            List<Map<String, Object>> mapList, String name) {
        List<String> stringList = new ArrayList<String>();

        for (Map<String, Object> map : mapList) {
            Object value = map.get(name);

            if (value != null) {
                stringList.add(value.toString());
            }
        }

        return stringList;
    }

    // ~
    @Resource
    public void setPermManager(PermManager permManager) {
        this.permManager = permManager;
    }

    @Resource
    public void setRoleManager(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    @Resource
    public void setAccountStatusClient(AccountStatusClient accountStatusClient) {
        this.accountStatusClient = accountStatusClient;
    }

    @Resource
    public void setTenantConnector(TenantConnector tenantConnector) {
        this.tenantConnector = tenantConnector;
    }

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }

    public void setCheckAccountStatus(boolean checkAccountStatus) {
        this.checkAccountStatus = checkAccountStatus;
    }

    @Value("${authz.client.code}")
    public void setSysCode(String sysCode) {
        this.sysCode = sysCode;
    }
}
