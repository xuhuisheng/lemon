package com.mossle.api.org;

import java.util.List;

/**
 * 试验中的组织机构相关的connector.
 */
public interface OrgConnector {
    int getJobLevelByUserId(String userId);

    int getJobLevelByInitiatorAndPosition(String userId, String positionName);
}
