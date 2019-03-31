package com.mossle.cms.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.api.user.UserConnector;

import com.mossle.cms.persistence.domain.CmsTemplateCatalog;
import com.mossle.cms.persistence.domain.CmsTemplateContent;
import com.mossle.cms.persistence.manager.CmsTemplateCatalogManager;
import com.mossle.cms.persistence.manager.CmsTemplateContentManager;

import com.mossle.core.csv.CsvProcessor;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

// import org.springframework.core.io.Resource;
public class CmsTemplateDeployer implements ApplicationContextAware {
    private static Logger logger = LoggerFactory
            .getLogger(CmsTemplateDeployer.class);
    private CmsTemplateCatalogManager cmsTemplateCatalogManager;
    private CmsTemplateContentManager cmsTemplateContentManager;
    private String dataFilePath = "classpath:/data/cms-template";
    private String dataFileEncoding = "UTF-8";
    private String defaultTenantId = "1";
    private boolean enable = true;
    private ApplicationContext applicationContext;

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip init {}", CmsTemplateDeployer.class);

            return;
        }

        org.springframework.core.io.Resource[] resources = applicationContext
                .getResources(dataFilePath + "/**");

        if (resources == null) {
            logger.info("cannot find default template for cms.");

            return;
        }

        String prefix = "";
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (org.springframework.core.io.Resource resource : resources) {
            String name = resource.getFilename();
            logger.debug("name : {}", name);
            logger.debug("getFile : {}", resource.getFile());
            logger.debug("isOpen : {}", resource.isOpen());
            logger.debug("isReadable : {}", resource.isReadable());
            logger.debug("getURI : {}", resource.getURI());
            logger.debug("getURL : {}", resource.getURL());

            String url = resource.getURL().toString();
            prefix = url.substring(0, url.lastIndexOf("data/cms-template")
                    + "data/cms-template".length() + 1);
            logger.debug("prefix : {}", prefix);

            String path = url.substring(prefix.length());

            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }

            logger.debug("path : {}", path);

            // list.add(path);
            String content = "";
            boolean hasContent = false;

            if (resource.isReadable()) {
                content = IOUtils.toString(resource.getInputStream(),
                        dataFileEncoding);
                // logger.info("content : {}", content);
                hasContent = true;
            }

            Map<String, Object> map = new HashMap<String, Object>();
            list.add(map);
            map.put("hasContent", hasContent);
            map.put("content", content);
            map.put("path", path);
            map.put("name", name);

            String parentPath = path
                    .substring(0, path.length() - name.length());

            if (parentPath.endsWith("/")) {
                parentPath = parentPath.substring(0, parentPath.length() - 1);
            }

            map.put("parentPath", parentPath);
            logger.debug("map : {}", map);
        }

        CmsTemplateCatalog cmsTemplateCatalog = cmsTemplateCatalogManager
                .findUniqueBy("name", "/");

        if (cmsTemplateCatalog == null) {
            cmsTemplateCatalog = new CmsTemplateCatalog();
            cmsTemplateCatalog.setName("/");
            cmsTemplateCatalog.setPath("/");
            cmsTemplateCatalogManager.save(cmsTemplateCatalog);
        }

        Collections.sort(list, new Comparator<Map<String, Object>>() {
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                if (o1 == null) {
                    return 1;
                }

                if (o2 == null) {
                    return -1;
                }

                String path1 = (String) o1.get("path");
                String path2 = (String) o2.get("path");

                if (path1 == null) {
                    return 1;
                }

                return path1.compareTo(path2);
            }
        });

        for (Map<String, Object> map : list) {
            // logger.info("path : {}", map);
            String parentPath = (String) map.get("parentPath");
            String path = (String) map.get("path");
            String name = (String) map.get("name");
            String content = (String) map.get("content");

            if ("".equals(parentPath)) {
                parentPath = "/";
            }

            cmsTemplateCatalog = cmsTemplateCatalogManager.findUniqueBy("path",
                    parentPath);

            if (cmsTemplateCatalog == null) {
                logger.info("cannot find parent : {}", parentPath);

                continue;
            }

            boolean hasContent = (Boolean) map.get("hasContent");

            if (hasContent) {
                CmsTemplateContent cmsTemplateContent = new CmsTemplateContent();
                cmsTemplateContent.setPath(path);
                cmsTemplateContent.setName(name);
                cmsTemplateContent.setContent(content);
                cmsTemplateContent.setCmsTemplateCatalog(cmsTemplateCatalog);
                cmsTemplateContentManager.save(cmsTemplateContent);
            } else {
                CmsTemplateCatalog child = new CmsTemplateCatalog();
                child.setPath(path);
                child.setName(name);
                child.setCmsTemplateCatalog(cmsTemplateCatalog);
                cmsTemplateCatalogManager.save(child);
            }
        }
    }

    @Resource
    public void setCmsTemplateCatalogManager(
            CmsTemplateCatalogManager cmsTemplateCatalogManager) {
        this.cmsTemplateCatalogManager = cmsTemplateCatalogManager;
    }

    @Resource
    public void setCmsTemplateContentManager(
            CmsTemplateContentManager cmsTemplateContentManager) {
        this.cmsTemplateContentManager = cmsTemplateContentManager;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
