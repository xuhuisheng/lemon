package com.mossle.security.region;

import java.util.List;

public interface RegionCache {
    List<RegionDTO> getByType(String type);

    List<RegionDTO> getByType(String parentPath, String type);

    void refresh();
}
