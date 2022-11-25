package com.mossle.bpm.data;

import java.io.IOException;

import java.util.List;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;

import com.mossle.api.tenant.TenantConnector;
import com.mossle.api.tenant.TenantDTO;

import com.mossle.bpm.cmd.SyncProcessCmd;

import com.mossle.core.spring.ApplicationContextHelper;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;

import org.springframework.stereotype.Component;

/**
 * 自动部署，并把每个xml都发布成一个Deployment.
 */
@Component("com.mossle.bpm.data.BpmDeployer")
public class BpmDeployer implements ApplicationContextAware {
    private Logger logger = LoggerFactory.getLogger(BpmDeployer.class);
    private ApplicationContext applicationContext;
    private ProcessEngine processEngine;
    private Resource[] deploymentResources = new Resource[0];
    private boolean enable = true;
    private String defaultTenantCode;
    private TenantConnector tenantConnector;
    private ApplicationContextHelper applicationContextHelper;

    @PostConstruct
    public void init() {
        if (!enable) {
            logger.info("skip bpm init data");

            return;
        }

        logger.info("start bpm init data");

        if ((deploymentResources == null) || (deploymentResources.length == 0)) {
            try {
                deploymentResources = applicationContext
                        .getResources("classpath:/data/bpmn2/*");
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            if ((deploymentResources == null)
                    || (deploymentResources.length == 0)) {
                logger.info("cannot find resources, skip");

                return;
            }
        }

        TenantDTO tenantDto = tenantConnector.findByCode(defaultTenantCode);

        RepositoryService repositoryService = processEngine
                .getRepositoryService();

        for (Resource resource : deploymentResources) {
            logger.info("resource : {}", resource);

            String resourceName = null;

            if (resource instanceof ContextResource) {
                resourceName = ((ContextResource) resource)
                        .getPathWithinContext();
            } else if (resource instanceof ByteArrayResource) {
                resourceName = resource.getDescription();
            } else {
                try {
                    resourceName = resource.getFile().getAbsolutePath();
                } catch (IOException ex) {
                    logger.debug(ex.getMessage(), ex);
                    resourceName = resource.getFilename();
                }
            }

            try {
                DeploymentBuilder deploymentBuilder = repositoryService
                        .createDeployment().enableDuplicateFiltering()
                        .name(resourceName);

                if (resourceName.endsWith(".bar")
                        || resourceName.endsWith(".zip")
                        || resourceName.endsWith(".jar")) {
                    deploymentBuilder.addZipInputStream(new ZipInputStream(
                            resource.getInputStream()));
                } else {
                    deploymentBuilder.addInputStream(resourceName,
                            resource.getInputStream());
                }

                Deployment deployment = deploymentBuilder.tenantId(
                        tenantDto.getId()).deploy();
                logger.info("auto deploy : {}", resourceName);

                for (ProcessDefinition processDefinition : repositoryService
                        .createProcessDefinitionQuery()
                        .deploymentId(deployment.getId()).list()) {
                    this.syncProcessDefinition(processDefinition.getId());
                }
            } catch (IOException ex) {
                throw new ActivitiException("couldn't auto deploy resource '"
                        + resource + "': " + ex.getMessage(), ex);
            }
        }

        logger.info("end bpm init data");
    }

    public boolean checkDeploymentUpToDate(String resourceName,
            long lastModified) {
        List<Deployment> deployments = processEngine.getRepositoryService()
                .createDeploymentQuery().deploymentName(resourceName)
                .orderByDeploymenTime().desc().list();

        if (deployments.isEmpty()) {
            return false;
        }

        Deployment deployment = deployments.get(0);

        return deployment.getDeploymentTime().getTime() > lastModified;
    }

    public void syncProcessDefinition(String processDefinitionId) {
        processEngine.getManagementService().executeCommand(
                new SyncProcessCmd(processDefinitionId));
    }

    @javax.annotation.Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public void setDeploymentResources(Resource[] deploymentResources) {
        this.deploymentResources = deploymentResources;
    }

    @Value("${bpm.data.init.enable:false}")
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setDefaultTenantCode(String defaultTenantCode) {
        this.defaultTenantCode = defaultTenantCode;
    }

    @javax.annotation.Resource
    public void setTenantConnector(TenantConnector tenantConnector) {
        this.tenantConnector = tenantConnector;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @javax.annotation.Resource
    public void setApplicationContextHelper(
            ApplicationContextHelper applicationContextHelper) {
        this.applicationContextHelper = applicationContextHelper;
    }
}
