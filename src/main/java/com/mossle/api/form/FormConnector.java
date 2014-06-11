package com.mossle.api.form;

import java.util.List;

public interface FormConnector {
    List<FormDTO> getAll(String scopeId);
}
