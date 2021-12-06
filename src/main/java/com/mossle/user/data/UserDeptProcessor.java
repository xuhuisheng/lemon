package com.mossle.user.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.user.persistence.domain.AccountDept;
import com.mossle.user.persistence.manager.AccountDeptManager;

public class UserDeptProcessor {
    private AccountDeptManager accountDeptManager;
    private UserDeptDTO root = new UserDeptDTO();
    private Map<String, UserDeptDTO> userDeptMap = new HashMap<String, UserDeptDTO>();

    public void init(String userDeptDataFilePath, String userDeptDataEncoding)
            throws Exception {
        Map<String, Object> map = new JsonParser().parseMap(
                userDeptDataFilePath, userDeptDataEncoding);
        this.processUserDeptDto(map, root);
    }

    public void processUserDeptDto(Map<String, Object> map,
            UserDeptDTO userDeptDto) {
        String code = (String) map.get("code");
        String name = (String) map.get("name");
        String type = (String) map.get("type");
        String leader = (String) map.get("leader");

        if (type == null) {
            type = "department";
        }

        userDeptDto.setCode(code);
        userDeptDto.setName(name);
        userDeptDto.setType(type);
        userDeptDto.setLeader(leader);

        userDeptMap.put(code, userDeptDto);

        List<Map<String, Object>> children = (List<Map<String, Object>>) map
                .get("children");

        if (children == null) {
            return;
        }

        for (Map<String, Object> item : children) {
            UserDeptDTO child = new UserDeptDTO();
            userDeptDto.getChildren().add(child);
            this.processUserDeptDto(item, child);
            child.setParentCode(code);
        }
    }

    public void process() {
        this.processDept(root);
    }

    public void processDept(UserDeptDTO userDeptDto) {
        String code = userDeptDto.getCode();
        AccountDept accountDept = accountDeptManager.findUniqueBy("code", code);

        if (accountDept == null) {
            accountDept = new AccountDept();
            accountDept.setCode(code);
        }

        accountDept.setName(userDeptDto.getName());

        AccountDept parent = accountDeptManager.findUniqueBy("code",
                userDeptDto.getParentCode());
        accountDept.setAccountDept(parent);

        accountDeptManager.save(accountDept);

        this.processDepts(userDeptDto.getChildren());
    }

    public void processDepts(List<UserDeptDTO> userDeptDtos) {
        for (UserDeptDTO userDeptDto : userDeptDtos) {
            this.processDept(userDeptDto);
        }
    }

    // ~
    public Collection<UserDeptDTO> getUserDeptDtos() {
        return userDeptMap.values();
    }

    public UserDeptDTO getRoot() {
        return root;
    }

    public UserDeptDTO findByCode(String code) {
        return userDeptMap.get(code);
    }

    public void setAccountDeptManager(AccountDeptManager accountDeptManager) {
        this.accountDeptManager = accountDeptManager;
    }
}
