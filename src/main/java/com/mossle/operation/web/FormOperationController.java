package com.mossle.operation.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.form.FormConnector;
import com.mossle.api.form.FormDTO;
import com.mossle.api.store.StoreConnector;
import com.mossle.api.store.StoreDTO;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.MultipartHandler;
import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;
import com.mossle.ext.store.MultipartFileDataSource;

import com.mossle.keyvalue.KeyValue;
import com.mossle.keyvalue.Prop;
import com.mossle.keyvalue.Record;
import com.mossle.keyvalue.RecordBuilder;

import com.mossle.xform.*;

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
@RequestMapping("operation")
public class FormOperationController {
    private static Logger logger = LoggerFactory
            .getLogger(FormOperationController.class);
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private JsonMapper jsonMapper = new JsonMapper();
    private MessageHelper messageHelper;
    private KeyValue keyValue;
    private MultipartResolver multipartResolver;
    private StoreConnector storeConnector;
    private FormConnector formConnector;

    @RequestMapping("form-operation-preview")
    public String preview(@RequestParam("code") String code, Model model)
            throws Exception {
        FormDTO formDto = formConnector.findForm(code);
        Record record = keyValue.findByRef(code);

        if (record == null) {
            record = new Record();
            record.setName(formDto.getName());
            record.setRef(formDto.getCode());
            keyValue.save(record);
        }

        model.addAttribute("record", record);

        Xform xform = new XformBuilder().setStoreConnector(storeConnector)
                .setContent(formDto.getContent()).setRecord(record).build();
        model.addAttribute("xform", xform);

        return "operation/form-operation-preview";
    }

    @RequestMapping("form-operation-test")
    public String test(HttpServletRequest request) throws Exception {
        MultipartHandler multipartHandler = new MultipartHandler(
                multipartResolver);
        FormDTO formDto = null;

        try {
            multipartHandler.handle(request);
            logger.info("{}", multipartHandler.getMultiValueMap());
            logger.info("{}", multipartHandler.getMultiFileMap());

            String ref = multipartHandler.getMultiValueMap().getFirst("ref");

            formDto = formConnector.findForm(ref);

            Record record = keyValue.findByRef(ref);

            record = new RecordBuilder().build(record, multipartHandler,
                    storeConnector);

            keyValue.save(record);
        } finally {
            multipartHandler.clear();
        }

        if (formDto == null) {
            return "redirect:/form/form-template-list.do";
        } else {
            return "redirect:/operation/form-operation-preview.do?code="
                    + formDto.getCode();
        }
    }

    // ~ ======================================================================
    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setKeyValue(KeyValue keyValue) {
        this.keyValue = keyValue;
    }

    @Resource
    public void setMultipartResolver(MultipartResolver multipartResolver) {
        this.multipartResolver = multipartResolver;
    }

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }

    @Resource
    public void setFormConnector(FormConnector formConnector) {
        this.formConnector = formConnector;
    }
}
