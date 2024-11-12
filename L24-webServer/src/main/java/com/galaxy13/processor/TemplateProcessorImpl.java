package com.galaxy13.processor;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class TemplateProcessorImpl implements TemplateProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TemplateProcessorImpl.class);
    private final Configuration configuration;

    public TemplateProcessorImpl(String templatePath) {
        configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setClassForTemplateLoading(TemplateProcessorImpl.class, templatePath);
    }

    @Override
    public String getPage(String templateName, Map<String, Object> data) {
        try (Writer writer = new StringWriter()) {
            Template template = configuration.getTemplate(templateName);
            template.process(data, writer);
            return writer.toString();
        } catch (IOException | TemplateException e) {
            logger.error("Error while processing template {}", templateName, e);
            return "Internal server error";
        }
    }
}
