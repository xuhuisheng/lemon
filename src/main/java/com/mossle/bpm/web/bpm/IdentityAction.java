package com.mossle.bpm.web.bpm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mossle.core.struts2.BaseAction;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;

import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 用户和组的管理
 * 
 * @author LuZhao
 * 
 */
@Results({
        @Result(name = IdentityAction.RELOAD_USER, location = "identity!listUsers.do?operationMode=RETRIEVE", type = "redirect"),
        @Result(name = IdentityAction.RELOAD_GROUP, location = "identity!listGroups.do?operationMode=RETRIEVE", type = "redirect") })
public class IdentityAction extends BaseAction {
    public static final String RELOAD_USER = "reload-user";
    public static final String RELOAD_GROUP = "reload-group";
    private ProcessEngine processEngine;
    private JdbcTemplate jdbcTemplate;
    private List<User> users;
    private User user;
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> selectedGroupIds = new ArrayList<String>();
    private List<Group> groups;
    private Group group;
    private String groupId;
    private String name;
    private String type;
    private List<String> selectedUserIds = new ArrayList<String>();

    // ~ =======================用户=============================
    /**
     * 用户列表
     * 
     * @return
     */
    public String listUsers() {
        users = processEngine.getIdentityService().createUserQuery().list();

        return "listUsers";
    }

    /**
     * 添加和修改用户页面
     * 
     * @return
     */
    public String inputUser() {
        if (userId != null) {
            user = processEngine.getIdentityService().createUserQuery()
                    .userId(userId).singleResult();
        }

        return "inputUser";
    }

    /**
     * 添加和修改用户
     * 
     * @return
     */
    public String saveUser() {
        user = processEngine.getIdentityService().createUserQuery()
                .userId(userId).singleResult();

        if (user == null) {
            user = processEngine.getIdentityService().newUser(userId);
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        processEngine.getIdentityService().saveUser(user);

        return RELOAD_USER;
    }

    /**
     * 删除用户
     * 
     * @return
     */
    public String removeUser() {
        processEngine.getIdentityService().deleteUser(userId);

        return RELOAD_USER;
    }

    /**
     * 为用户添加和修改组页面
     * 
     * @return
     */
    public String inputUserMembership() {
        user = processEngine.getIdentityService().createUserQuery()
                .userId(userId).singleResult();
        groups = processEngine.getIdentityService().createGroupQuery().list();

        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select group_id_ from ACT_ID_MEMBERSHIP where user_id_=?",
                userId);

        for (Map<String, Object> map : list) {
            selectedGroupIds.add(map.get("group_id_").toString());
        }

        return "inputUserMembership";
    }

    /**
     * 为用户添加和修改组
     * 
     * @return
     */
    public String saveUserMembership() {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select group_id_ from ACT_ID_MEMBERSHIP where user_id_=?",
                userId);

        for (Map<String, Object> map : list) {
            String groupId = map.get("group_id_").toString();
            processEngine.getIdentityService()
                    .deleteMembership(userId, groupId);
        }

        for (String groupId : selectedGroupIds) {
            processEngine.getIdentityService()
                    .createMembership(userId, groupId);
        }

        return RELOAD_USER;
    }

    // ~ =======================组=============================

    /**
     * 组列表
     * 
     * @return
     */
    public String listGroups() {
        groups = processEngine.getIdentityService().createGroupQuery().list();

        return "listGroups";
    }

    /**
     * 添加和修改组页面
     * 
     * @return
     */
    public String inputGroup() {
        if (groupId != null) {
            group = processEngine.getIdentityService().createGroupQuery()
                    .groupId(groupId).singleResult();
        }

        return "inputGroup";
    }

    /**
     * 添加和修改组
     * 
     * @return
     */
    public String saveGroup() {
        group = processEngine.getIdentityService().createGroupQuery()
                .groupId(groupId).singleResult();

        if (group == null) {
            group = processEngine.getIdentityService().newGroup(groupId);
        }

        group.setName(name);
        group.setType(type);
        processEngine.getIdentityService().saveGroup(group);

        return RELOAD_GROUP;
    }

    /**
     * 删除
     * 
     * @return
     */
    public String removeGroup() {
        processEngine.getIdentityService().deleteGroup(groupId);

        return RELOAD_GROUP;
    }

    /**
     * 添加和修改该组下面的用户页面
     * 
     * @return
     */
    public String inputGroupMembership() {
        group = processEngine.getIdentityService().createGroupQuery()
                .groupId(groupId).singleResult();
        users = processEngine.getIdentityService().createUserQuery().list();

        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select user_id_ from ACT_ID_MEMBERSHIP where group_id_=?",
                groupId);

        for (Map<String, Object> map : list) {
            selectedUserIds.add(map.get("user_id_").toString());
        }

        return "inputGroupMembership";
    }

    /**
     * 添加和修改设置该组下面的用户
     * 
     * @return
     */
    public String saveGroupMembership() {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select user_id_ from ACT_ID_MEMBERSHIP where group_id_=?",
                groupId);

        for (Map<String, Object> map : list) {
            String userId = map.get("user_id_").toString();
            processEngine.getIdentityService()
                    .deleteMembership(userId, groupId);
        }

        for (String userId : selectedUserIds) {
            processEngine.getIdentityService()
                    .createMembership(userId, groupId);
        }

        return RELOAD_GROUP;
    }

    // ~ ==================================================
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> getUsers() {
        return users;
    }

    public User getUser() {
        return user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getSelectedGroupIds() {
        return selectedGroupIds;
    }

    public void setSelectedGroupIds(List<String> selectedGroupIds) {
        this.selectedGroupIds = selectedGroupIds;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public Group getGroup() {
        return group;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getSelectedUserIds() {
        return selectedUserIds;
    }

    public void setSelectedUserIds(List<String> selectedUserIds) {
        this.selectedUserIds = selectedUserIds;
    }
}
