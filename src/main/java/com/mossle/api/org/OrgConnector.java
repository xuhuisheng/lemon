package com.mossle.api.org;

import java.util.List;

/**
 * 试验中的组织机构相关的connector.
 */
public interface OrgConnector {
    int getJobLevelByUserId(String userId);

    int getJobLevelByInitiatorAndPosition(String userId, String positionName);

    String getSuperiorId(String userId);

    List<String> getPositionUserIds(String userId, String positionName);

    List<OrgDTO> getOrgsByUserId(String userId);
}
