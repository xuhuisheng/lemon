package com.mossle.bpm.support;

import java.io.IOException;

import java.util.List;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;

import com.mossle.api.tenant.TenantConnector;
import com.mossle.api.tenant.TenantDTO;

import com.mossle.bpm.cmd.SyncProcessCmd;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;

/**
 * 自动部署，并把每个xml都发布成一个Deployment.
 */
public class AutoDeployer {
    private Logger logger = LoggerFactory.getLogger(AutoDeployer.class);
    private ProcessEngine processEngine;
    private Resource[] deploymentResources = new Resource[0];
    private boolean enable = true;
    private String defaultTenantCode;
    private TenantConnector tenantConnector;

    @PostConstruct
    public void init() {
        if (!enable) {
            return;
        }

        if ((deploymentResources == null) || (deploymentResources.length == 0)) {
            return;
        }

        TenantDTO tenantDto = tenantConnector.findByCode(defaultTenantCode);

        RepositoryService repositoryService = processEngine
                .getRepositoryService();

        for (Resource resource : deploymentResources) {
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

    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public void setDeploymentResources(Resource[] deploymentResources) {
        this.deploymentResources = deploymentResources;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setDefaultTenantCode(String defaultTenantCode) {
        this.defaultTenantCode = defaultTenantCode;
    }

    public void setTenantConnector(TenantConnector tenantConnector) {
        this.tenantConnector = tenantConnector;
    }
}
