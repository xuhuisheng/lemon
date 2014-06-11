package com.mossle.form.engine;

import com.mossle.form.engine.model.FormModel;

public class FormEngine {
    private FormModelParser formModelParser = new FormModelParser();
    private FormModelSourceFetcher formModelSourceFetcher;
    private FormModelCache formModelCache = new MemoryFormModelCache();

    private FormModel deploy(String json) {
        FormModel formModel = formModelParser.parse(json);
        formModelCache.setFormModel(formModel);

        return formModel;
    }

    public FormModel getFormModel(String id) {
        FormModel formModel = formModelCache.getFormModel(id);

        if (formModel == null) {
            String json = formModelSourceFetcher.getFormModelSource(id);
            formModel = this.deploy(json);
        }

        return formModel;
    }

    public void setFormModelSourceFetcher(
            FormModelSourceFetcher formModelSourceFetcher) {
        this.formModelSourceFetcher = formModelSourceFetcher;
    }
}
