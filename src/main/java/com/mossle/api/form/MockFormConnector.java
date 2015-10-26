package com.mossle.api.form;

import java.util.List;

public class MockFormConnector implements FormConnector {
    public List<FormDTO> getAll(String tenantId) {
        return null;
    }

    public FormDTO findForm(String code, String tenantId) {
        return null;
    }
}
