package com.mossle.security.region;

public interface RegionUpdater {
    void updateRegion(Object instance);

    void removeRegion(Object instance);
}
