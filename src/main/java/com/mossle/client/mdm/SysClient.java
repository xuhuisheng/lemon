package com.mossle.client.mdm;

import java.util.List;

import com.mossle.api.sys.SysCategoryDTO;
import com.mossle.api.sys.SysInfoDTO;

public interface SysClient {
    List<SysCategoryDTO> findAll();

    List<SysInfoDTO> findFavorites();
}
