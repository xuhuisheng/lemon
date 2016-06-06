package com.mossle.android.rs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mossle.api.form.FormDTO;
import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskDTO;
import com.mossle.api.keyvalue.KeyValueConnector;
import com.mossle.api.keyvalue.Record;
import com.mossle.api.process.ProcessConnector;
import com.mossle.api.store.StoreConnector;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.bpm.persistence.manager.BpmProcessManager;

import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.mapper.JsonMapper;

import com.mossle.model.support.FormField;

import com.mossle.pim.persistence.manager.PimDeviceManager;

import com.mossle.xform.Xform;
import com.mossle.xform.XformBuilder;

import org.activiti.engine.ProcessEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("android/form")
public class AndroidFormResource {
    private static Logger logger = LoggerFactory
            .getLogger(AndroidFormResource.class);
    private JsonMapper jsonMapper = new JsonMapper();
    private ProcessEngine processEngine;
    private BpmProcessManager bpmProcessManager;
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;
    private PimDeviceManager pimDeviceManager;
    private ProcessConnector processConnector;
    private HumanTaskConnector humanTaskConnector;
    private KeyValueConnector keyValueConnector;
    private StoreConnector storeConnector;

    @POST
    @Path("viewStartForm")
    @Produces(MediaType.APPLICATION_JSON)
    public String viewStartForm(
            @FormParam("processDefinitionId") String processDefinitionId)
            throws Exception {
        logger.info("start : {}", processDefinitionId);

        FormDTO formDto = processConnector.findStartForm(processDefinitionId);

        if (formDto.isRedirect()) {
            return "";
        }

        Map<String, Object> formJson = jsonMapper.fromJson(
                formDto.getContent(), Map.class);
        List<Map<String, Object>> sections = (List<Map<String, Object>>) formJson
                .get("sections");
        List<FormField> formFields = new ArrayList<FormField>();

        for (Map<String, Object> section : sections) {
            if (!"grid".equals(section.get("type"))) {
                continue;
            }

            List<Map<String, Object>> fields = (List<Map<String, Object>>) section
                    .get("fields");

            for (Map<String, Object> field : fields) {
                String type = (String) field.get("type");
                String name = (String) field.get("name");
                String items = (String) field.get("items");
                String label = name;
                Boolean readOnly = (Boolean) field.get("readOnly");

                if (readOnly == null) {
                    readOnly = false;
                }

                if ("label".equals(type)) {
                    continue;
                }

                FormField formField = null;

                formField = new FormField();
                formField.setName(name);
                formField.setLabel(label);
                formField.setType(type);
                formField.setItems(items);
                formField.setReadOnly(readOnly);
                formFields.add(formField);
            }
        }

        StringBuilder buff = new StringBuilder();

        buff.append("<xmlgui>")
                .append("<form id='1' name='form' submitTo='http://192.168.1.106:8080/mossle-app-lemon/rs/android/bpm/startProcess' >");

        // .append("<field name='fname' label='First Name' type='text' required='Y' options=''/>")
        // .append("<field name='lname' label='Last Name' type='text' required='Y' options=''/>")
        // .append("<field name='gender' label='Gender' type='choice' required='Y' options='Male|Female'/>")
        // .append("<field name='age' label='Age on 15 Oct. 2010' type='numeric' required='N' options=''/>")
        for (FormField formField : formFields) {
            String type = "text";
            String options = "";

            if ("radio".equals(formField.getType())
                    || "checkbox".equals(formField.getType())
                    || "select".equals(formField.getType())) {
                type = "choice";
                options = formField.getItems().replaceAll(",", "|");
            } else if ("datepicker".equals(formField.getType())) {
                type = "datepicker";
            }

            buff.append("<field name='" + formField.getName() + "'");
            buff.append(" label='" + formField.getName() + "'");
            buff.append(" type='" + type + "'");
            buff.append(" required='" + (formField.isReadOnly() ? "N" : "Y")
                    + "'");
            buff.append(" readOnly='" + (formField.isReadOnly() ? "Y" : "N")
                    + "'");
            buff.append(" options='" + options + "'/>");
        }

        buff.append("</form>").append("</xmlgui>");

        return buff.toString();
    }

    @POST
    @Path("viewTaskForm")
    @Produces(MediaType.APPLICATION_JSON)
    public String viewTaskForm(@FormParam("taskId") String taskId)
            throws Exception {
        logger.info("start : {}", taskId);

        HumanTaskDTO humanTaskDto = humanTaskConnector.findHumanTask(taskId);

        if (humanTaskDto == null) {
            return "";
        }

        FormDTO formDto = this.findTaskForm(humanTaskDto);

        if (formDto.isRedirect()) {
            return "";
        }

        Map<String, Object> formJson = jsonMapper.fromJson(
                formDto.getContent(), Map.class);
        List<Map<String, Object>> sections = (List<Map<String, Object>>) formJson
                .get("sections");
        List<FormField> formFields = new ArrayList<FormField>();

        for (Map<String, Object> section : sections) {
            if (!"grid".equals(section.get("type"))) {
                continue;
            }

            List<Map<String, Object>> fields = (List<Map<String, Object>>) section
                    .get("fields");

            for (Map<String, Object> field : fields) {
                String type = (String) field.get("type");
                String name = (String) field.get("name");
                String items = (String) field.get("items");
                String label = name;
                Boolean readOnly = (Boolean) field.get("readOnly");

                if (readOnly == null) {
                    readOnly = false;
                }

                if ("complete".equals(humanTaskDto.getStatus())) {
                    readOnly = true;
                }

                if ("label".equals(type)) {
                    continue;
                }

                FormField formField = null;

                formField = new FormField();
                formField.setName(name);
                formField.setLabel(label);
                formField.setType(type);
                formField.setItems(items);
                formField.setReadOnly(readOnly);
                formFields.add(formField);
            }
        }

        // 如果是任务草稿，直接通过processInstanceId获得record，更新数据
        // TODO: 分支肯定有问题
        String processInstanceId = humanTaskDto.getProcessInstanceId();

        Record record = keyValueConnector.findByRef(processInstanceId);

        Xform xform = new XformBuilder().setStoreConnector(storeConnector)
                .setContent(formDto.getContent()).setRecord(record).build();

        StringBuilder buff = new StringBuilder();

        buff.append("<xmlgui>")
                .append("<form id='1' name='form' submitTo='http://192.168.1.106:8080/mossle-app-lemon/rs/android/task/completeTask'")
                .append(" readOnly='")
                .append("complete".equals(humanTaskDto.getStatus()))
                .append("'").append(" >");

        // .append("<field name='fname' label='First Name' type='text' required='Y' options=''/>")
        // .append("<field name='lname' label='Last Name' type='text' required='Y' options=''/>")
        // .append("<field name='gender' label='Gender' type='choice' required='Y' options='Male|Female'/>")
        // .append("<field name='age' label='Age on 15 Oct. 2010' type='numeric' required='N' options=''/>")
        for (FormField formField : formFields) {
            String name = formField.getName();
            String type = "text";
            String options = "";
            String value = "";

            try {
                if (xform.findXformField(name).getValue() != null) {
                    value = xform.findXformField(name).getValue().toString();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if ("radio".equals(formField.getType())
                    || "checkbox".equals(formField.getType())) {
                type = "choice";
                options = formField.getItems().replaceAll(",", "|");
            }

            buff.append("<field name='" + formField.getName() + "'");
            buff.append(" label='" + formField.getName() + "'");
            buff.append(" type='" + type + "'");
            buff.append(" required='" + (formField.isReadOnly() ? "N" : "Y")
                    + "'");
            buff.append(" readOnly='" + (formField.isReadOnly() ? "Y" : "N")
                    + "'");
            buff.append(" options='" + options + "'");
            buff.append(" value='" + value + "'/>");
        }

        buff.append("</form>").append("</xmlgui>");
        logger.info("{}", buff);

        return buff.toString();
    }

    public FormDTO findTaskForm(HumanTaskDTO humanTaskDto) {
        FormDTO formDto = humanTaskConnector.findTaskForm(humanTaskDto.getId());

        return formDto;
    }

    // ~ ======================================================================
    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Resource
    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setPimDeviceManager(PimDeviceManager pimDeviceManager) {
        this.pimDeviceManager = pimDeviceManager;
    }

    @Resource
    public void setProcessConnector(ProcessConnector processConnector) {
        this.processConnector = processConnector;
    }

    @Resource
    public void setHumanTaskConnector(HumanTaskConnector humanTaskConnector) {
        this.humanTaskConnector = humanTaskConnector;
    }

    @Resource
    public void setKeyValueConnector(KeyValueConnector keyValueConnector) {
        this.keyValueConnector = keyValueConnector;
    }

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }
}
