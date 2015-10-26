package com.mossle.api.org;

import java.util.List;

/**
 * 试验中的组织机构相关的connector.
 */
public interface OrgConnector {
    /**
     * 根据userId获得职位级别.
     */
    int getJobLevelByUserId(String userId);

    /**
     * 获得userId最近的positionName对应的职位级别.
     */
    int getJobLevelByInitiatorAndPosition(String userId, String positionName);

    /**
     * 获得上级id.
     */
    String getSuperiorId(String userId);

    /**
     * 获得userId最近的positionName下的所有人员.
     */
    List<String> getPositionUserIds(String userId, String positionName);

    /**
     * 根据userId获得所有最近的部门或公司.
     */
    List<OrgDTO> getOrgsByUserId(String userId);
}
