package com.mossle.client.space;

import java.util.List;

public interface SpaceClient {
    List<SpaceDTO> findBuildings();

    List<SpaceDTO> findLocationsByBuildingCode(String buildingCode);
}
