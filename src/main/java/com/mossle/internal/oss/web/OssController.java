package com.mossle.internal.oss.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.client.oss.OssClient;

import com.mossle.core.export.Exportor;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.internal.oss.persistence.domain.OssBucket;
import com.mossle.internal.oss.persistence.domain.OssObject;
import com.mossle.internal.oss.persistence.manager.OssBucketManager;
import com.mossle.internal.oss.persistence.manager.OssObjectManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("oss")
public class OssController {
    private OssBucketManager ossBucketManager;
    private OssObjectManager ossObjectManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantHolder tenantHolder;
    private OssClient ossClient;

    @RequestMapping("index")
    public String index(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = ossBucketManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "oss/index";
    }

    @RequestMapping("objects")
    public String objects(@ModelAttribute Page page,
            @RequestParam("bucket") String bucket,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        OssBucket ossBucket = ossBucketManager.findUniqueBy("name", bucket);
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQL_ossBucket.id", Long
                .toString(ossBucket.getId())));
        page = ossObjectManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "oss/objects";
    }

    @RequestMapping("upload")
    public String upload(@RequestParam("file") MultipartFile multipartFile,
            @RequestParam("bucket") String bucket) throws Exception {
        String bucketName = bucket;
        String objectName = multipartFile.getOriginalFilename();
        ossClient.putObject(bucketName, objectName,
                multipartFile.getInputStream());

        return "redirect:/oss/objects.do?bucket=" + bucketName;
    }

    @RequestMapping("remove")
    public String remove(@RequestParam("id") Long id) throws Exception {
        OssObject ossObject = ossObjectManager.get(id);
        String bucketName = ossObject.getOssBucket().getName();
        String objectName = ossObject.getName();
        ossClient.deleteObject(bucketName, objectName);

        return "redirect:/oss/objects.do?bucket=" + bucketName;
    }

    // ~
    @RequestMapping("view/bootstrap-fileinput")
    public String viewBootstrapFileinput() {
        return "oss/view/bootstrap-fileinput";
    }

    @RequestMapping("multipart")
    @ResponseBody
    public Map<String, Object> multipart(
            @RequestParam("file") MultipartFile multipartFile) throws Exception {
        String uuid = UUID.randomUUID().toString();
        String originalFilename = multipartFile.getOriginalFilename();

        String bucketName = "default";
        String objectName = uuid;
        ossClient.putObject(bucketName, objectName,
                multipartFile.getInputStream());

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("originalFilename", originalFilename);
        result.put("uuid", uuid);
        result.put("url", "http://localhost:8080/mossle-web-store/oss/rs/"
                + uuid);
        result.put("contentType", "image/jpg");
        result.put("size", "100");
        result.put("catalog", "catalog");
        result.put("ref", "ref");

        return result;
    }

    // ~ ======================================================================
    @Resource
    public void setOssBucketManager(OssBucketManager ossBucketManager) {
        this.ossBucketManager = ossBucketManager;
    }

    @Resource
    public void setOssObjectManager(OssObjectManager ossObjectManager) {
        this.ossObjectManager = ossObjectManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setOssClient(OssClient ossClient) {
        this.ossClient = ossClient;
    }
}
