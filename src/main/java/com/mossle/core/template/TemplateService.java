package com.mossle.core.template;

import java.io.File;

import java.util.Map;

public interface TemplateService {
    String renderText(String text, Map<String, Object> data);

    String render(String templatePath, Map<String, Object> data);

    void renderTo(String templatePath, Map<String, Object> data, File targetFile);
}
