package com.mossle.doc.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.scope.ScopeHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.util.IoUtils;
import com.mossle.core.util.ServletUtils;

import com.mossle.doc.domain.DocInfo;
import com.mossle.doc.manager.DocInfoManager;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;
import com.mossle.ext.store.StoreConnector;
import com.mossle.ext.store.StoreDTO;

import com.mossle.security.util.SpringSecurityUtils;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("doc")
public class DocInfoController {
    private DocInfoManager docInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private StoreConnector storeConnector;

    @RequestMapping("doc-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);

        String userId = userConnector.findByUsername(
                SpringSecurityUtils.getCurrentUsername(),
                ScopeHolder.getUserRepoRef()).getId();
        propertyFilters.add(new PropertyFilter("EQL_userId", userId));
        page = docInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "doc/doc-info-list";
    }

    @RequestMapping("doc-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            DocInfo docInfo = docInfoManager.get(id);
            model.addAttribute("model", docInfo);
        }

        return "doc/doc-info-input";
    }

    @RequestMapping("doc-info-save")
    public String save(@ModelAttribute DocInfo docInfo,
            @RequestParam("attachment") MultipartFile attachment,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) throws Exception {
        DocInfo dest = null;
        Long id = docInfo.getId();

        if (id != null) {
            dest = docInfoManager.get(id);
            beanMapper.copy(docInfo, dest);
        } else {
            dest = docInfo;
            dest.setCreateTime(new Date());

            String userId = SpringSecurityUtils.getCurrentUserId();
            dest.setUserId(Long.parseLong(userId));
        }

        StoreDTO storeDto = storeConnector.save("docinfo",
                attachment.getInputStream(), attachment.getOriginalFilename());

        dest.setName(attachment.getOriginalFilename());
        dest.setPath(storeDto.getKey());

        docInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/doc/doc-info-list.do";
    }

    @RequestMapping("doc-info-download")
    public void download(@RequestParam("id") Long id,
            HttpServletResponse response) throws Exception {
        DocInfo docInfo = docInfoManager.get(id);
        InputStream is = null;

        try {
            ServletUtils.setFileDownloadHeader(response, docInfo.getName());
            is = storeConnector.get("docinfo", docInfo.getPath())
                    .getInputStream();
            IoUtils.copyStream(is, response.getOutputStream());
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    @RequestMapping("doc-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<DocInfo> docInfos = docInfoManager.findByIds(selectedItem);

        docInfoManager.removeAll(docInfos);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/doc/doc-info-list.do";
    }

    @RequestMapping("doc-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = docInfoManager.pagedQuery(page, propertyFilters);

        List<DocInfo> docInfos = (List<DocInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("doc info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(docInfos);
        exportor.export(response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setDocInfoManager(DocInfoManager docInfoManager) {
        this.docInfoManager = docInfoManager;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }
}
