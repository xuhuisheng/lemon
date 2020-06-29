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

    OrgDTO findById(String orgId);

    List<String> findUserByPositionName(String positionName, String companyName);

    List<String> findUserByNearestPositionName(String userId,
            String positionName);

    /**
     * TODO(根据用户查询用所主职所在的岗位.
     * 
     * @param userId
     * @return OrgDTO 返回类型
     */
    OrgDTO findPositionByUserId(String userId);

    OrgDTO findCompany(String userCode);
}
