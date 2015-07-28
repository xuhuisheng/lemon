package com.mossle.api.org;

import java.util.List;

/**
 * 试验中的组织机构相关的connector.
 */
public class MockOrgConnector implements OrgConnector {
    public int getJobLevelByUserId(String userId) {
        return 0;
    }

    public int getJobLevelByInitiatorAndPosition(String userId,
            String positionName) {
        return 0;
    }

    public String getSuperiorId(String userId) {
        return null;
    }

    public List<String> getPositionUserIds(String userId, String positionName) {
        return null;
    }

    public List<OrgDTO> getOrgsByUserId(String userId) {
        return null;
    }
}
