package com.mossle.group.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.api.GlobalScopeDTO;
import com.mossle.api.ScopeConnector;

import com.mossle.group.domain.GroupType;
import com.mossle.group.manager.GroupTypeManager;

import org.springframework.stereotype.Component;

@Component
public class GroupTypeCache {
    private Map<Long, List<GroupTypeDTO>> groupTypeDtoMap = new HashMap<Long, List<GroupTypeDTO>>();
    private GroupTypeManager groupTypeManager;
    private ScopeConnector scopeConnector;

    public List<GroupTypeDTO> getGroupTypeDtos(Long globalId) {
        return groupTypeDtoMap.get(globalId);
    }

    public List<GroupTypeDTO> getGroupTypeDtos(String globalCode) {
        Long globalId = scopeConnector.findGlobalId(globalCode);

        return getGroupTypeDtos(globalId);
    }

    @PostConstruct
    public void refresh() {
        List<GlobalScopeDTO> globalScopeDtos = scopeConnector
                .findGlobalScopes();

        for (GlobalScopeDTO globalScopeDTO : globalScopeDtos) {
            refreshScope(globalScopeDTO.getId());
        }
    }

    public void refreshScope(Long globalId) {
        List<GroupType> groupTypes = groupTypeManager.find(
                "from GroupType where globalId=?", globalId);
        List<GroupTypeDTO> groupTypeDtos = new ArrayList<GroupTypeDTO>();

        for (GroupType groupType : groupTypes) {
            GroupTypeDTO groupTypeDto = new GroupTypeDTO();
            groupTypeDto.setId(groupType.getId());
            groupTypeDto.setName(groupType.getName());
            groupTypeDtos.add(groupTypeDto);
        }

        groupTypeDtoMap.put(globalId, groupTypeDtos);
    }

    @Resource
    public void setGroupTypeManager(GroupTypeManager groupTypeManager) {
        this.groupTypeManager = groupTypeManager;
    }

    @Resource
    public void setScopeConnector(ScopeConnector scopeConnector) {
        this.scopeConnector = scopeConnector;
    }
}
