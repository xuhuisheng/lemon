package com.mossle.client.open;

import java.util.List;

public interface OpenClient {
    OpenAppDTO getApp(String clientId);

    List<OpenAppDTO> getAll();

    List<OpenAppDTO> findUserApps(String userId);

    List<OpenAppDTO> findGroupApps(String groupCode);

    SysDTO findSys(String code);
}
