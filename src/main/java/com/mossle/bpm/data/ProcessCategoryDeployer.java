package com.mossle.bpm.data;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.bpm.persistence.domain.BpmCategory;
import com.mossle.bpm.persistence.domain.BpmConfBase;
import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.manager.BpmCategoryManager;
import com.mossle.bpm.persistence.manager.BpmConfBaseManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessCategoryDeployer {
    private static Logger logger = LoggerFactory
            .getLogger(ProcessCategoryDeployer.class);
    private BpmCategoryManager bpmCategoryManager;
    private BpmProcessManager bpmProcessManager;
    private BpmConfBaseManager bpmConfBaseManager;
    private String defaultTenantId = "1";

    @PostConstruct
    public void init() throws Exception {
        String processCategoryDataFilePath = "data/process-category.json";
        String processCategoryDataEncoding = "UTF-8";
        List<Map<String, Object>> list = new JsonParser().parseList(
                processCategoryDataFilePath, processCategoryDataEncoding);
        logger.debug("list : {}", list);
        this.processCategories(list, null);
    }

    public void processCategories(List<Map<String, Object>> list,
            BpmCategory parent) {
        int index = 0;

        for (Map<String, Object> map : list) {
            String name = (String) map.get("name");
            BpmCategory bpmCategory = bpmCategoryManager.findUniqueBy("name",
                    name);
            logger.debug("name : {} {}", name, bpmCategory);

            if (bpmCategory == null) {
                bpmCategory = new BpmCategory();
                bpmCategory.setName(name);
                bpmCategory.setBpmCategory(parent);
                bpmCategory.setPriority(index);
                bpmCategory.setTenantId(defaultTenantId);
                bpmCategoryManager.save(bpmCategory);
            }

            index++;

            List<Map<String, Object>> children = (List<Map<String, Object>>) map
                    .get("children");

            if (children != null) {
                this.processProcesses(children, bpmCategory);
            }
        }
    }

    public void processProcesses(List<Map<String, Object>> list,
            BpmCategory parent) {
        int index = 0;

        for (Map<String, Object> map : list) {
            String code = (String) map.get("code");
            String name = (String) map.get("name");
            BpmProcess bpmProcess = bpmProcessManager
                    .findUniqueBy("name", name);

            if (bpmProcess == null) {
                bpmProcess = new BpmProcess();

                if (StringUtils.isNotBlank(code)) {
                    bpmProcess.setCode(code);

                    String hql = "from BpmConfBase where processDefinitionKey=? order by processDefinitionVersion desc";
                    List<BpmConfBase> bpmConfBases = this.bpmConfBaseManager
                            .find(hql, code);

                    if (!bpmConfBases.isEmpty()) {
                        bpmProcess.setBpmConfBase(bpmConfBases.get(0));
                    }
                }

                bpmProcess.setName(name);
                bpmProcess.setBpmCategory(parent);
                bpmProcess.setPriority(index);
                bpmProcess.setUseTaskConf(0);
                bpmProcess.setTenantId(defaultTenantId);
                bpmProcessManager.save(bpmProcess);
            }

            index++;
        }
    }

    @Resource
    public void setBpmCategoryManager(BpmCategoryManager bpmCategoryManager) {
        this.bpmCategoryManager = bpmCategoryManager;
    }

    @Resource
    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }

    @Resource
    public void setBpmConfBaseManager(BpmConfBaseManager bpmConfBaseManager) {
        this.bpmConfBaseManager = bpmConfBaseManager;
    }
}
