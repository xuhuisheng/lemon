package com.mossle.api;

public interface OrgConnector {
    OrgDTO findByType(String reference, String type);
}
