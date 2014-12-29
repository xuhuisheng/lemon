package com.mossle.humantask.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.internal.StoreConnector;
import com.mossle.api.internal.StoreDTO;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.MultipartHandler;
import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;
import com.mossle.ext.store.MultipartFileDataSource;

import com.mossle.humantask.persistence.domain.HtHumantask;
import com.mossle.humantask.persistence.manager.HtHumantaskManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.util.MultiValueMap;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("humantask")
public class HtHumantaskController {
    private static Logger logger = LoggerFactory
            .getLogger(HtHumantaskController.class);
    private HtHumantaskManager htHumantaskManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private JsonMapper jsonMapper = new JsonMapper();
    private MessageHelper messageHelper;
    private StoreConnector storeConnector;

    @RequestMapping("humantask-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = htHumantaskManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "humantask/humantask-list";
    }

    @RequestMapping("humantask-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            HtHumantask htHumantask = htHumantaskManager.get(id);
            model.addAttribute("model", htHumantask);
        }

        return "humantask/humantask-input";
    }

    @RequestMapping("humantask-save")
    public String save(@ModelAttribute HtHumantask htHumantask,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        HtHumantask dest = null;
        Long id = htHumantask.getId();

        if (id != null) {
            dest = htHumantaskManager.get(id);
            beanMapper.copy(htHumantask, dest);
        } else {
            dest = htHumantask;
        }

        htHumantaskManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/humantask/humantask-list.do";
    }

    @RequestMapping("humantask-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<HtHumantask> htHumantasks = htHumantaskManager
                .findByIds(selectedItem);

        htHumantaskManager.removeAll(htHumantasks);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/humantask/humantask-list.do";
    }

    @RequestMapping("humantask-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = htHumantaskManager.pagedQuery(page, propertyFilters);

        List<HtHumantask> dynamicModels = (List<HtHumantask>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("dynamic model");
        tableModel.addHeaders("id", "name");
        tableModel.setData(dynamicModels);
        exportor.export(response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setHtHumantaskManager(HtHumantaskManager htHumantaskManager) {
        this.htHumantaskManager = htHumantaskManager;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
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
