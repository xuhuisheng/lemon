package com.mossle.party.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManagerPool {
    // department
    private Long id;
    private List<ManagerInfo> managerInfos = new ArrayList<ManagerInfo>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ManagerInfo> getManagerInfos() {
        return managerInfos;
    }

    public void setManagerInfos(List<ManagerInfo> managerInfos) {
        this.managerInfos = managerInfos;
    }

    //
    public boolean isExists() {
        return (!managerInfos.isEmpty());
    }

    public void addUserId(String userId, Integer priority) {
        if (priority == null) {
            priority = 99;
        }

        for (ManagerInfo managerInfo : managerInfos) {
            if (managerInfo.getPriority() == priority) {
                managerInfo.addUserId(userId);

                return;
            }
        }

        ManagerInfo managerInfo = new ManagerInfo();
        managerInfo.setPriority(priority);
        managerInfo.addUserId(userId);
        managerInfos.add(managerInfo);
    }

    public boolean containsUserId(String userId) {
        for (ManagerInfo managerInfo : managerInfos) {
            if (managerInfo.containsUserId(userId)) {
                return true;
            }
        }

        return false;
    }

    public List<String> findNearestManagers(String userId) {
        if (!this.isExists()) {
            return Collections.emptyList();
        }

        Collections.sort(managerInfos, new ManagerInfoComparator());

        ManagerInfo managerInfo = null;

        if (userId == null) {
            // 员工
            managerInfo = managerInfos.get(managerInfos.size() - 1);
        } else {
            int index = 0;

            for (ManagerInfo item : managerInfos) {
                if (item.containsUserId(userId)) {
                    break;
                }

                index++;
            }

            if (index == 0) {
                // 本人已经是最高级领导
                return Collections.emptyList();
            } else {
                managerInfo = managerInfos.get(index - 1);
            }
        }

        List<String> userIds = new ArrayList<String>();
        userIds.addAll(managerInfo.getUserIds());

        return userIds;
    }
}
