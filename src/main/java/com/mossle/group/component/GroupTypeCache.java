package com.mossle.group.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.api.UserRepoConnector;
import com.mossle.api.UserRepoDTO;

import com.mossle.group.domain.GroupType;
import com.mossle.group.manager.GroupTypeManager;

import org.springframework.stereotype.Component;

@Component
public class GroupTypeCache {
    private Map<String, List<GroupTypeDTO>> groupTypeDtoMap = new HashMap<String, List<GroupTypeDTO>>();
    private GroupTypeManager groupTypeManager;
    private UserRepoConnector userRepoConnector;

    public List<GroupTypeDTO> getGroupTypeDtos(String userRepoRef) {
        return groupTypeDtoMap.get(userRepoRef);
    }

    @PostConstruct
    public void refresh() {
        List<UserRepoDTO> userRepoDtos = userRepoConnector.findAll();

        for (UserRepoDTO userRepoDto : userRepoDtos) {
            refreshScope(userRepoDto.getId());
        }
    }

    public void refreshScope(String userRepoRef) {
        List<GroupType> groupTypes = groupTypeManager.find(
                "from GroupType where userRepoRef=?", userRepoRef);
        List<GroupTypeDTO> groupTypeDtos = new ArrayList<GroupTypeDTO>();

        for (GroupType groupType : groupTypes) {
            GroupTypeDTO groupTypeDto = new GroupTypeDTO();
            groupTypeDto.setId(groupType.getId());
            groupTypeDto.setName(groupType.getName());
            groupTypeDtos.add(groupTypeDto);
        }

        groupTypeDtoMap.put(userRepoRef, groupTypeDtos);
    }

    @Resource
    public void setGroupTypeManager(GroupTypeManager groupTypeManager) {
        this.groupTypeManager = groupTypeManager;
    }

    @Resource
    public void setUserRepoConnector(UserRepoConnector userRepoConnector) {
        this.userRepoConnector = userRepoConnector;
    }
}
