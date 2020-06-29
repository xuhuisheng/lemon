package com.mossle.client.mdm;

import java.util.Collections;
import java.util.List;

import com.mossle.api.sys.SysCategoryDTO;
import com.mossle.api.sys.SysInfoDTO;

public class MockSysClient implements SysClient {
    public List<SysCategoryDTO> findAll() {
        return Collections.emptyList();
    }

    public List<SysInfoDTO> findFavorites() {
        return Collections.emptyList();
    }
}
