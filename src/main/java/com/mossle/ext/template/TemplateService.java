package com.mossle.ext.template;

import java.io.File;

import java.util.Map;

public interface TemplateService {
    String render(String templatePath, Map<String, Object> data);

    void renderTo(String templatePath, Map<String, Object> data, File targetFile);
}
