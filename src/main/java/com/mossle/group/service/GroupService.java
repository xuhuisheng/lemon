package com.mossle.group.service;

import javax.annotation.Resource;

import com.mossle.api.GroupProcessor;

import com.mossle.group.domain.GroupBase;
import com.mossle.group.manager.GroupBaseManager;
import com.mossle.group.support.EmptyGroupProcessor;

import com.mossle.party.domain.PartyEntity;
import com.mossle.party.manager.PartyEntityManager;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class GroupService {
    private GroupProcessor groupProcessor = new EmptyGroupProcessor();
    private GroupBaseManager groupBaseManager;
    private PartyEntityManager partyEntityManager;

    public void insert(GroupBase groupBase) {
        groupBaseManager.save(groupBase);
        groupProcessor.insertGroup(Long.toString(groupBase.getId()),
                groupBase.getName());
    }

    public void update(GroupBase groupBase) {
        groupBaseManager.save(groupBase);
        groupProcessor.updateGroup(Long.toString(groupBase.getId()),
                groupBase.getName());
    }

    public void remove(GroupBase groupBase) {
        groupBaseManager.remove(groupBase);
        groupProcessor.removeGroup(Long.toString(groupBase.getId()));
    }

    public PartyEntity findGroup(String reference) {
        return partyEntityManager.findUnique(
                "from PartyEntity where partyType.id<>1 and reference=?",
                reference);
    }

    @Resource
    public void setGroupBaseManager(GroupBaseManager groupBaseManager) {
        this.groupBaseManager = groupBaseManager;
    }

    @Resource
    public void setGroupProcessor(GroupProcessor groupProcessor) {
        this.groupProcessor = groupProcessor;
    }

    @Resource
    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }
}
