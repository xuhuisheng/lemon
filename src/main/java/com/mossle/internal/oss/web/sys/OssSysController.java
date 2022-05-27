package com.mossle.internal.oss.web.sys;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.client.open.OpenClient;
import com.mossle.client.open.SysDTO;

import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;

import com.mossle.internal.oss.persistence.domain.OssBucket;
import com.mossle.internal.oss.persistence.manager.OssBucketManager;
import com.mossle.internal.oss.persistence.manager.OssObjectManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("oss/sys")
public class OssSysController {
    private static Logger logger = LoggerFactory
            .getLogger(OssSysController.class);
    private OpenClient openClient;
    private OssBucketManager ossBucketManager;
    private OssObjectManager ossObjectManager;

    @RequestMapping("{sysCode}/index")
    public String index(@PathVariable("sysCode") String sysCode,
            @ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        logger.debug("index : {}", sysCode);

        // open sys
        SysDTO sysDto = openClient.findSys(sysCode);
        model.addAttribute("sysCode", sysCode);
        model.addAttribute("sysDto", sysDto);

        String tenantId = sysCode;
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = ossBucketManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "oss/sys/index";
    }

    @RequestMapping("{sysCode}/objects")
    public String objects(@PathVariable("sysCode") String sysCode,
            @ModelAttribute Page page, @RequestParam("bucket") String bucket,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        // open sys
        SysDTO sysDto = openClient.findSys(sysCode);
        model.addAttribute("sysCode", sysCode);
        model.addAttribute("sysDto", sysDto);

        OssBucket ossBucket = ossBucketManager.findUniqueBy("name", bucket);
        String tenantId = sysCode;
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQL_ossBucket.id", Long
                .toString(ossBucket.getId())));
        page = ossObjectManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "oss/sys/objects";
    }

    // ~
    @Resource
    public void setOpenClient(OpenClient openClient) {
        this.openClient = openClient;
    }

    @Resource
    public void setOssBucketManager(OssBucketManager ossBucketManager) {
        this.ossBucketManager = ossBucketManager;
    }

    @Resource
    public void setOssObjectManager(OssObjectManager ossObjectManager) {
        this.ossObjectManager = ossObjectManager;
    }
}
