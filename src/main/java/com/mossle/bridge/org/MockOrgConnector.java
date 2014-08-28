package com.mossle.bridge.org;

import java.util.Collections;
import java.util.List;

import com.mossle.api.org.OrgConnector;
import com.mossle.api.org.OrgDTO;

public class MockOrgConnector implements OrgConnector {
    public int getJobLevelByUserId(String userId) {
        return -1;
    }

    public int getJobLevelByInitiatorAndPosition(String userId,
            String positionName) {
        return -1;
    }

    public String getSuperiorId(String userId) {
        return null;
    }

    public List<String> getPositionUserIds(String userId, String positionName) {
        return Collections.emptyList();
    }

    public List<OrgDTO> getOrgsByUserId(String userId) {
        return Collections.emptyList();
    }
}
