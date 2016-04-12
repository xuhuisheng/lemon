package com.mossle.spi.device;

public interface DeviceConnector {
    DeviceDTO findDevice(String code);

    void saveDevice(DeviceDTO deviceDto);
}
