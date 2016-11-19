package com.mossle.spi.device;

public class MockDeviceConnector implements DeviceConnector {
    public DeviceDTO findDevice(String code) {
        return null;
    }

    public void saveDevice(DeviceDTO deviceDto) {
    }
}
