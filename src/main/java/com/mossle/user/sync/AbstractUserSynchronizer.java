package com.mossle.user.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractUserSynchronizer {
    public abstract List<UserSyncDTO> getLocalUsers();

    public abstract List<UserSyncDTO> getRemoteUsers();

    public abstract void doSync(List<UserSyncDTO> removes,
            List<UserSyncDTO> inserts, List<UserSyncDTO> updates);

    public void execute() {
        List<UserSyncDTO> localUsers = this.getLocalUsers();
        List<UserSyncDTO> remoteUsers = this.getRemoteUsers();

        Map<String, UserSyncDTO> localUserMap = this
                .convertListToMap(localUsers);
        Map<String, UserSyncDTO> remoteUserMap = this
                .convertListToMap(remoteUsers);

        List<UserSyncDTO> inserts = new ArrayList<UserSyncDTO>();
        List<UserSyncDTO> updates = new ArrayList<UserSyncDTO>();
        List<UserSyncDTO> removes = new ArrayList<UserSyncDTO>();

        for (UserSyncDTO userSyncDto : remoteUsers) {
            if (localUserMap.containsKey(userSyncDto.getId())) {
                UserSyncDTO localUser = localUserMap.get(userSyncDto.getId());

                if (localUser.hasModified(userSyncDto)) {
                    updates.add(userSyncDto);
                }
            } else {
                inserts.add(userSyncDto);
            }
        }

        for (UserSyncDTO userSyncDto : localUsers) {
            if (!remoteUserMap.containsKey(userSyncDto.getId())) {
                removes.add(userSyncDto);
            }
        }

        this.doSync(removes, inserts, updates);
    }

    public Map<String, UserSyncDTO> convertListToMap(
            List<UserSyncDTO> userSyncDtos) {
        Map<String, UserSyncDTO> userMap = new HashMap<String, UserSyncDTO>();

        for (UserSyncDTO userSyncDto : userSyncDtos) {
            userMap.put(userSyncDto.getId(), userSyncDto);
        }

        return userMap;
    }
}
