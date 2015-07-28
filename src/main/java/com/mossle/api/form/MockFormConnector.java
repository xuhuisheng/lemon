package com.mossle.api.form;

import java.util.List;

public class MockFormConnector implements FormConnector {
    public List<FormDTO> getAll(String scopeId) {
        return null;
    }

    public FormDTO findForm(String code) {
        return null;
    }
}
