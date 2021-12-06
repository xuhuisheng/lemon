package com.mossle.cms.web;

import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 搜索.
 */
@Controller
@RequestMapping("search")
public class SearchController {
    /**
     * 搜索页.
     */
    @RequestMapping("")
    public String index(
            @RequestParam(value = "q", required = false) String query,
            Model model) {
        if (StringUtils.isBlank(query)) {
            return "search/index";
        }

        return "search/result";
    }
}
