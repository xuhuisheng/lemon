package com.mossle.operation.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.form.FormDTO;
import com.mossle.api.keyvalue.FormParameter;
import com.mossle.api.model.ModelConnector;
import com.mossle.api.model.ModelInfoDTO;
import com.mossle.api.model.ModelItemDTO;
import com.mossle.api.user.UserConnector;

import com.mossle.client.store.StoreClient;

import com.mossle.core.MultipartHandler;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.store.MultipartFileDataSource;

import com.mossle.operation.support.ModelBuilder;

import com.mossle.xform.Xform;
import com.mossle.xform.XformBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.MultiValueMap;

import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ProcessModelService {
    private static Logger logger = LoggerFactory
            .getLogger(ProcessModelService.class);
    private ModelConnector modelConnector;
    private StoreClient storeClient;
    private UserConnector userConnector;
    private JsonMapper jsonMapper = new JsonMapper();
    private BeanMapper beanMapper = new BeanMapper();

    /**
     * 创建模型，是在还没有生成流水号的情况下， 通过流程的配置，创建一个空的模型， 创建之后，会生成流水号。
     */
    public ModelInfoDTO createModel(ModelInfoDTO param, String tenantId) {
        String category = param.getCategory();
        String processId = param.getProcessId();
        String processKey = param.getProcessKey();
        int processVersion = param.getProcessVersion();
        String processName = param.getProcessName();
        String initiator = param.getInitiator();
        String initiatorDept = param.getInitiatorDept();
        String applicant = param.getApplicant();
        String applicantDept = param.getApplicantDept();
        String status = "draft";

        ModelInfoDTO modelInfoDto = new ModelInfoDTO();
        modelInfoDto.setCategory(category);
        modelInfoDto.setProcessId(processId);
        modelInfoDto.setProcessKey(processKey);
        modelInfoDto.setProcessVersion(processVersion);
        modelInfoDto.setProcessName(processName);
        modelInfoDto.setInitiator(initiator);
        modelInfoDto.setInitiatorDept(initiatorDept);
        modelInfoDto.setApplicant(applicant);
        modelInfoDto.setApplicantDept(applicantDept);
        modelInfoDto.setStatus(status);
        modelInfoDto.setCreateTime(new Date());

        modelInfoDto = modelConnector.save(modelInfoDto);

        return modelInfoDto;
    }

    /**
     * 更新表单数据.
     */
    public ModelInfoDTO updateData(String code, FormParameter formParameter,
            String tenantId) throws Exception {
        ModelInfoDTO modelInfoDto = modelConnector.findByCode(code);

        if (modelInfoDto == null) {
            throw new IllegalArgumentException("cannot find model : " + code);
        }

        ModelBuilder modelBuilder = new ModelBuilder();

        for (Map.Entry<String, List<String>> entry : formParameter
                .getMultiValueMap().entrySet()) {
            String key = entry.getKey();

            if (key == null) {
                continue;
            }

            List<String> value = entry.getValue();

            if ((value == null) || (value.isEmpty())) {
                continue;
            }

            if (key.indexOf(".") == -1) {
                String theValue = this.processValue(value);
                modelBuilder.addItem(key, theValue);
            } else {
                for (String theValue : value) {
                    modelBuilder.addItem(key, theValue);
                }
            }
        }

        if (formParameter.getMultiFileMap() != null) {
            for (Map.Entry<String, List<MultipartFile>> entry : formParameter
                    .getMultiFileMap().entrySet()) {
                String key = entry.getKey();

                if (key == null) {
                    continue;
                }

                List<MultipartFile> value = entry.getValue();

                if ((value == null) || (value.isEmpty())) {
                    continue;
                }

                for (MultipartFile multipartFile : value) {
                    if ((multipartFile.getName() == null)
                            || "".equals(multipartFile.getName().trim())) {
                        continue;
                    }

                    if (multipartFile.getSize() == 0) {
                        logger.info("ignore empty file");

                        continue;
                    }

                    String theValue = storeClient.saveStore("form",
                            new MultipartFileDataSource(multipartFile),
                            tenantId).getKey();
                    modelBuilder.addItem(key, theValue);
                }
            }
        }

        ModelInfoDTO data = modelBuilder.build();
        beanMapper.copy(data, modelInfoDto);

        modelInfoDto = modelConnector.save(modelInfoDto);

        return modelInfoDto;
    }

    /**
     * 主要是获得多值属性，比如checkbox.
     */
    public String processValue(List<String> values) {
        if ((values == null) || (values.isEmpty())) {
            return "";
        }

        if (values.size() == 1) {
            return values.get(0);
        }

        StringBuilder buff = new StringBuilder();

        for (String value : values) {
            buff.append(value).append(",");
        }

        buff.deleteCharAt(buff.length() - 1);

        return buff.toString();
    }

    /**
     * 为xform生成数据.
     */
    public Xform processFormData(String code, FormDTO formDto) throws Exception {
        ModelInfoDTO modelInfoDto = modelConnector.findByCode(code);

        if (modelInfoDto == null) {
            Xform xform = new XformBuilder().setContent(formDto.getContent())
                    .build();

            return xform;
        }

        Xform xform = new XformBuilder().setStoreClient(storeClient)
                .setUserConnector(userConnector)
                .setContent(formDto.getContent()).setModelInfoDto(modelInfoDto)
                .build();

        return xform;
    }

    @Resource
    public void setModelConnector(ModelConnector modelConnector) {
        this.modelConnector = modelConnector;
    }

    @Resource
    public void setStoreClient(StoreClient storeClient) {
        this.storeClient = storeClient;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }
}
