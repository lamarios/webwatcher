package com.ftpix.webwatcher.server;


import com.ftpix.sparknnotation.annotations.SparkController;
import com.ftpix.sparknnotation.annotations.SparkGet;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SparkController()
public class TestWebController {

    private String simpleContent = "hello world";

    private List<String> multipleContent = new ArrayList<>();


    @SparkGet(value = "/simple", templateEngine = VelocityTemplateEngine.class)
    public ModelAndView simpleContent() {
        Map<String, String> values = new HashMap<>();
        values.put("content", simpleContent);

        return new ModelAndView(values, "simple.vm");
    }


    @SparkGet(value = "/multiple", templateEngine = VelocityTemplateEngine.class)
    public ModelAndView multipleContent() {
        Map<String, Object> values = new HashMap<>();
        values.put("content", multipleContent);

        return new ModelAndView(values, "multiple.vm");
    }


    public String getSimpleContent() {
        return simpleContent;
    }

    public List<String> getMultipleContent() {
        return multipleContent;
    }

    public void setMultipleContent(String... content) {
        this.multipleContent = Stream.of(content).collect(Collectors.toList());
    }

    public void setSimpleContent(String simpleContent) {
        this.simpleContent = simpleContent;
    }
}
