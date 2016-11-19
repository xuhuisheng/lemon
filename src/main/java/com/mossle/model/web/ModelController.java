package com.mossle.model.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.keyvalue.KeyValueConnector;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.page.Page;

import com.mossle.model.persistence.domain.ModelField;
import com.mossle.model.persistence.domain.ModelInfo;
import com.mossle.model.persistence.manager.ModelFieldManager;
import com.mossle.model.persistence.manager.ModelInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("model")
public class ModelController {
    private static Logger logger = LoggerFactory
            .getLogger(ModelController.class);
    private ModelInfoManager modelInfoManager;
    private ModelFieldManager modelFieldManager;
    private JdbcTemplate jdbcTemplate;
    private KeyValueConnector keyValueConnector;
    private Exportor exportor;
    private TenantHolder tenantHolder;

    @RequestMapping("index")
    public String index(Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<ModelInfo> modelInfos = modelInfoManager.findBy("tenantId",
                tenantId);
        model.addAttribute("modelInfos", modelInfos);

        return "model/index";
    }

    @RequestMapping("list")
    public String list(@RequestParam("id") Long id, @ModelAttribute Page page,
            @RequestParam(value = "q", required = false) String q, Model model) {
        String tenantId = tenantHolder.getTenantId();
        logger.debug("q : {}", q);
        logger.debug("orderBy : {}", page.getOrderBy());
        logger.debug("order : {}", page.getOrder());

        List<ModelInfo> modelInfos = modelInfoManager.findBy("tenantId",
                tenantId);
        model.addAttribute("modelInfos", modelInfos);

        ModelInfo modelInfo = modelInfoManager.get(id);
        model.addAttribute("searchableFields",
                this.findSearchableFields(modelInfo));

        List<ModelField> listFields = this.findListFields(modelInfo);
        model.addAttribute("listFields", listFields);

        this.findList(modelInfo, listFields, page, q);
        model.addAttribute("page", page);

        return "model/list";
    }

    @RequestMapping("export")
    public void export(@RequestParam("id") Long id, @ModelAttribute Page page,
            @RequestParam(value = "q", required = false) String q,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ModelInfo modelInfo = modelInfoManager.get(id);

        List<ModelField> modelFields = this.findListFields(modelInfo);
        Map<String, String> headers = new HashMap<String, String>();

        for (ModelField modelField : modelFields) {
            headers.put(modelField.getCode(), modelField.getName());
        }

        this.findList(modelInfo, modelFields, page, q);

        List<Map<String, Object>> list = (List<Map<String, Object>>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName(modelInfo.getName());
        tableModel.addHeaders(headers.keySet().toArray(new String[0]));
        tableModel.setData(list);
        exportor.export(request, response, tableModel);
    }

    public List<ModelField> findSearchableFields(ModelInfo modelInfo) {
        return modelFieldManager
                .find("from ModelField where searchable='true' and modelInfo=? order by priority",
                        modelInfo);
    }

    public List<ModelField> findListFields(ModelInfo modelInfo) {
        return modelFieldManager
                .find("from ModelField where viewList='true' and modelInfo=? order by priority",
                        modelInfo);
    }

    public void findList(ModelInfo modelInfo, List<ModelField> modelFields,
            Page page, String q) {
        String tenantId = tenantHolder.getTenantId();
        Map<String, String> headers = new HashMap<String, String>();

        for (ModelField modelField : modelFields) {
            headers.put(modelField.getCode(), modelField.getName());
        }

        String processId = this.findProcessId(modelInfo.getCode());
        page.setTotalCount(this.keyValueConnector.findTotalCount(processId,
                tenantId, q));
        page.setResult(this.keyValueConnector.findResult(page, processId,
                tenantId, headers, q));
    }

    public String findProcessId(String processDefinitionId) {
        String sql = "select p.ID from BPM_PROCESS p, BPM_CONF_BASE cb where p.CONF_BASE_ID=cb.ID and cb.PROCESS_DEFINITION_ID=?";
        String processId = jdbcTemplate.queryForObject(sql, String.class,
                processDefinitionId);

        return processId;
    }

    // ~ ======================================================================
    @Resource
    public void setModelInfoManager(ModelInfoManager modelInfoManager) {
        this.modelInfoManager = modelInfoManager;
    }

    @Resource
    public void setModelFieldManager(ModelFieldManager modelFieldManager) {
        this.modelFieldManager = modelFieldManager;
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Resource
    public void setKeyValueConnector(KeyValueConnector keyValueConnector) {
        this.keyValueConnector = keyValueConnector;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
