package com.mossle.api.template;

import java.util.List;

public class MockTemplateConnector implements TemplateConnector {
    public TemplateDTO findByCode(String code, String tenantId) {
        return null;
    }

    public List<TemplateDTO> findAll(String tenantId) {
        return null;
    }
}
