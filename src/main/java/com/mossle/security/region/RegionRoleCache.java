package com.mossle.security.region;

import java.util.Collection;

public interface RegionRoleCache {
    Collection<RoleDTO> getByRegion(String regionName);

    Collection<RoleDTO> getByRegionPath(String regionPath);

    void refresh();
}
