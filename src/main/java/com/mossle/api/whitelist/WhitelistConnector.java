package com.mossle.api.whitelist;

import java.util.List;

public interface WhitelistConnector {
    WhitelistDTO getWhitelist(String code, String tenantId);

    List<WhitelistDTO> getWhitelists(String code, String tenantId);
}
