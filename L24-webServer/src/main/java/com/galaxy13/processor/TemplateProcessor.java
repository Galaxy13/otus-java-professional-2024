package com.galaxy13.processor;

import java.util.Map;

public interface TemplateProcessor {
    String getPage(String templateName, Map<String, Object> data);
}
