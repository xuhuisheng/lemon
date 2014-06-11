package com.mossle.form.engine;

import com.mossle.form.engine.model.FormModel;

public interface FormModelCache {
    FormModel getFormModel(String id);

    void setFormModel(FormModel formModel);
}
