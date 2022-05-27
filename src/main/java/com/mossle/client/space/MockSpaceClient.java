package com.mossle.client.space;

import java.util.Collections;
import java.util.List;

public class MockSpaceClient implements SpaceClient {
    public List<SpaceDTO> findBuildings() {
        return Collections.emptyList();
    }

    public List<SpaceDTO> findLocationsByBuildingCode(String buildingCode) {
        return Collections.emptyList();
    }
}
